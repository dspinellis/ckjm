package gr.spinellis.jmetrics;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Repository;
import org.apache.bcel.Constants;
import org.apache.bcel.util.*;
import java.io.*;
import java.util.*;

/**
 * Visit a class updating its Chidamber-Kemerer metrics.
 *
 * @see ClassMetrics
 * @version $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\ClassVisitor.java,v 1.7 2005/02/18 12:30:43 dds Exp $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
public class ClassVisitor extends org.apache.bcel.classfile.EmptyVisitor {
  private JavaClass         _clazz;
  private ConstantPoolGen   _cp;
  private String myClassName;
  private ClassMap   cmap;
  private ClassMetrics   cm;
  /* Classes encountered (CBO) */
  private HashSet<String> coupledClasses = new HashSet<String>();
  /** Methods encountered (WMC) */
  private HashSet<String> responseSet = new HashSet<String>();
  /** Use of fields in methods (LCOM)
   * We use a Tree rather than a Hash to calculate the
   * intersection in O(n) instead of O(n*n).
   */
  ArrayList<TreeSet<String>> mi = new ArrayList<TreeSet<String>>();

  /** @param clazz Java class to "decompile"
   */
  public ClassVisitor(JavaClass clazz, ClassMap classMap) {
    _clazz = clazz;
    _cp = new ConstantPoolGen(_clazz.getConstantPool());
    cmap = classMap;
    myClassName = clazz.getClassName();
    cm = cmap.getMetrics(myClassName);
  }

  public ClassMetrics getMetrics() { return cm; }

  /** Start Java code generation
   */
  public void start() {
    visitJavaClass(_clazz);
  }

  public void visitJavaClass(JavaClass clazz) {
    String super_name   = clazz.getSuperclassName();
    String package_name = clazz.getPackageName();

    cm.setVisited();
    ClassMetrics pm = cmap.getMetrics(super_name);

    pm.incNoc();
    cm.setParent(pm);
    registerCoupling(super_name);
    String ifs[] = clazz.getInterfaceNames();
    /* Measuring decision: couple interfaces */
    for (int i = 0; i < ifs.length; i++)
	    registerCoupling(ifs[i]);

    Field[] fields = clazz.getFields();

      for(int i=0; i < fields.length; i++)
	fields[i].accept(this);

    Method[] methods = clazz.getMethods();

    for(int i=0; i < methods.length; i++)
      methods[i].accept(this);
  }

  /* Add a given class to the classes we are coupled to */
  public void registerCoupling(String className) {
        /* Measuring decision: don't couple to Java SDK */
	if (!ClassMetrics.isJdkClass(className))
		coupledClasses.add(className);
  }

  /* Add the type's class to the classes we are coupled to */
  public void registerCoupling(Type t) {
  	registerCoupling(className(t));
  }

  /* Add a given class to the classes we are coupled to */
  void registerFieldAccess(String className, String fieldName) {
  	registerCoupling(className);
	if (className.equals(myClassName))
		mi.get(mi.size() - 1).add(fieldName);
  }

  /* Add a given method to our response set */
  void registerMethodInvocation(String className, String methodName) {
  	registerCoupling(className);
    /* Measuring decision: calls to JDK methods are included in the RFC calculation */
  	responseSet.add(className + "." + methodName);
  }

  public void visitField(Field field) {
    registerCoupling(field.getType());
  }

  public void visitMethod(Method method) {
    MethodGen mg = new MethodGen(method, _clazz.getClassName(), _cp);

    Type   result_type = mg.getReturnType();
    Type[] arg_types   = mg.getArgumentTypes();

    registerCoupling(mg.getReturnType());
    for (int i = 0; i < arg_types.length; i++)
	    registerCoupling(arg_types[i]);
    cm.incWmc();
    mi.add(new TreeSet<String>());
    MethodVisitor factory = new MethodVisitor(mg, this);
    factory.start();
  }

  /** Return a class name associated with a type */
  static String className(Type t) {
    String ts = t.toString();

    if (t.getType() <= Constants.T_VOID) {
      return "java.PRIMITIVE";
    } else if(t instanceof ArrayType) {
      ArrayType at = (ArrayType)t;
      return className(at.getBasicType());
    } else {
      return t.toString();
    }
  }

  /** Do final accounting at the end of the visit */
  public void end() {
  	cm.setCbo(coupledClasses.size());
  	cm.setRfc(responseSet.size());
	/*
	 * Calculate LCOM  as |P| - |Q| if |P| - |Q| > 0 or 0 otherwise
	 * where
	 * P = set of all empty set intersections
	 * Q = set of all nonempty set intersections
	 */
	int lcom = 0;
	System.out.println(mi.size());
	for (int i = 0; i < mi.size(); i++)
		for (int j = i + 1; j < mi.size(); j++) {
			/* A shallow unknown-type copy is enough */
			TreeSet<?> intersection = (TreeSet<?>)mi.get(i).clone();
			intersection.retainAll(mi.get(j));
			if (intersection.size() == 0)
				lcom++;
			else
				lcom--;
		}
	cm.setLcom(lcom > 0 ? lcom : 0);
  }
}
