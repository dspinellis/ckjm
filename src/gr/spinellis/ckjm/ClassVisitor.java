package gr.spinellis.jmetrics;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Repository;
import org.apache.bcel.Constants;
import org.apache.bcel.util.*;
import java.io.*;
import java.util.*;

/** 
 * This class takes a given JavaClass object and converts it to a
 * Java program that creates that very class using BCEL. This
 * gives new users of BCEL a useful example showing how things
 * are done with BCEL. It does not cover all features of BCEL,
 * but tries to mimic hand-written code as close as possible.
 *
 * @version $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\ClassVisitor.java,v 1.3 2005/02/18 07:32:37 dds Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A> 
 */
public class ClassVisitor extends org.apache.bcel.classfile.EmptyVisitor {
  private JavaClass         _clazz;
  private PrintWriter       _out;
  private ConstantPoolGen   _cp;
  private String myClassName;
  private ClassMap   cmap;
  private ClassMetrics   cm;
  /* Classes encountered */
  private HashSet<String> coupledClasses;
  /** Methods encountered */
  private HashSet<String> responseSet;
  /** Use of fields in methods */
  ArrayList<TreeSet<String>> mi = new ArrayList();

  /** @param clazz Java class to "decompile"
   * @param out where to output Java program
   */
  public ClassVisitor(JavaClass clazz, ClassMap classMap, OutputStream out) {
    _clazz = clazz;
    _out = new PrintWriter(out);
    _cp = new ConstantPoolGen(_clazz.getConstantPool());
    cmap = classMap;
    myClassName = clazz.getClassName();
    cm = cmap.getMetrics(myClassName);
    coupledClasses = new HashSet();
    responseSet = new HashSet();
  }

  public ClassMetrics getMetrics() { return cm; }

  /** Start Java code generation
   */
  public void start() {
    visitJavaClass(_clazz);
    _out.flush();
  }

  public void visitJavaClass(JavaClass clazz) {
    String class_name   = clazz.getClassName();
    String super_name   = clazz.getSuperclassName();
    String package_name = clazz.getPackageName();
    String inter        = Utility.printArray(clazz.getInterfaceNames(),
					     false, true);
    ClassMetrics pm = cmap.getMetrics(super_name);

    pm.incNoc();
    cm.setParent(pm);

    if(!"".equals(package_name)) {
      class_name = class_name.substring(package_name.length() + 1);
      _out.println("package " + package_name + ";\n");
     }


    _out.println("  public " + class_name  + "Creator() {");
    _out.println("class " +
		 (("".equals(package_name))? class_name :
		  package_name + "." + class_name) +
		 " extends " + super_name);

    Field[] fields = clazz.getFields();

      for(int i=0; i < fields.length; i++)
	fields[i].accept(this);

    Method[] methods = clazz.getMethods();

    for(int i=0; i < methods.length; i++)
      methods[i].accept(this);
  }

  /* Add a given class to the classes we are coupled to */
  private void registerCoupling(String classname) {
  	coupledClasses.add(classname);
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
  	responseSet.add(methodName);
  }

  public void visitField(Field field) {
    _out.println("field " + field.getName() + "\n");
  }

  public void visitMethod(Method method) {
    MethodGen mg = new MethodGen(method, _clazz.getClassName(), _cp);

    Type   result_type = mg.getReturnType();
    Type[] arg_types   = mg.getArgumentTypes();

    _out.println("    MethodGen method = new MethodGen(" +
		 printFlags(method.getAccessFlags()) +
		 ", " + printType(result_type) +
		 ", " + printArgumentTypes(arg_types) + ", " +
		 "new String[] { " +
		 Utility.printArray(mg.getArgumentNames(), false, true) +
		 " }, \"" + method.getName() + "\", \"" +
		 _clazz.getClassName() + "\", il, _cp);\n");

    cm.incWmc();
    mi.add(new TreeSet());
    MethodVisitor factory = new MethodVisitor(mg, this, _out);
    factory.start();

  }

  static String printFlags(int flags) {
    return printFlags(flags, false);
  }

  static String printFlags(int flags, boolean for_class) {
    if(flags == 0)
      return "0";

    StringBuffer buf = new StringBuffer();
    for(int i=0, pow=1; i <= Constants.MAX_ACC_FLAG; i++) {
      if((flags & pow) != 0) {
	if((pow == Constants.ACC_SYNCHRONIZED) && for_class)
	  buf.append("ACC_SUPER | ");
	else
	  buf.append("ACC_" + Constants.ACCESS_NAMES[i].toUpperCase() + " | ");
      }

      pow <<= 1;
    }

    String str = buf.toString();
    return str.substring(0, str.length() - 3);
  }

  static String printArgumentTypes(Type[] arg_types) {
    if(arg_types.length == 0)
      return "Type.NO_ARGS";

    StringBuffer args = new StringBuffer();

    for(int i=0; i < arg_types.length; i++) {
      args.append(printType(arg_types[i]));

      if(i < arg_types.length - 1)
	args.append(", ");
    }

    return "new Type[] { " + args.toString() + " }";
  }

  static String printType(Type type) {
    return printType(type.getSignature());
  }

  static String printType(String signature) {
    Type type = Type.getType(signature);
    byte t    = type.getType();

    if(t <= Constants.T_VOID) {
      return "Type." + Constants.TYPE_NAMES[t].toUpperCase();
    } else if(type.toString().equals("java.lang.String")) {
      return "Type.STRING";
    } else if(type.toString().equals("java.lang.Object")) {
      return "Type.OBJECT";
    } else if(type.toString().equals("java.lang.StringBuffer")) {
      return "Type.STRINGBUFFER";
    } else if(type instanceof ArrayType) {
      ArrayType at = (ArrayType)type;

      return "new ArrayType(" + printType(at.getBasicType()) +
	", " + at.getDimensions() + ")";
    } else {
      return "new ObjectType(\"" + Utility.signatureToString(signature, false) +
	"\")";
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
	for (int i = 0; i < mi.size(); i++)
		for (int j = i + 1; j < mi.size(); j++) {
			/* A shallow copy is enough */
			TreeSet<String> intersection = (TreeSet<String>)mi.get(i).clone();
			intersection.retainAll(mi.get(j));
			if (intersection.size() == 0)
				lcom++;
			else
				lcom--;
		}
	cm.setLcom(lcom > 0 ? lcom : 0);
  }

  /** Default main method
   */
  public static void main(String[] argv) throws Exception {
    JavaClass java_class;
    ClassMap cm = new ClassMap();
    String    name = argv[0];

    OutputStream nul = new FileOutputStream("nul");
    if((java_class = Repository.lookupClass(name)) == null)
      java_class = new ClassParser(name).parse(); // May throw IOException

    ClassVisitor visitor = new ClassVisitor(java_class, cm, nul);
    visitor.start();
    visitor.end();
    cm.printMetrics(System.out);
  }
}
