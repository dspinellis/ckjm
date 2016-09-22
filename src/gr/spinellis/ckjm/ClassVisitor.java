/*
 * (C) Copyright 2005 Diomidis Spinellis
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.spinellis.ckjm;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Repository;
import org.apache.bcel.Constants;
import org.apache.bcel.util.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Modifier;

/**
 * Visit a class updating its Chidamber-Kemerer metrics.
 *
 * @see ClassMetrics
 * @version $Revision: 1.21 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
public class ClassVisitor extends org.apache.bcel.classfile.EmptyVisitor {
    /** The class being visited. */
    private JavaClass visitedClass;
    /** The class's constant pool. */
    private ConstantPoolGen cp;
    /** The class's fully qualified name. */
    private String myClassName;
    /** The container where metrics for all classes are stored. */
    private ClassMetricsContainer cmap;
    /** The emtrics for the class being visited. */
    private ClassMetrics cm;
    /* Classes encountered.
     * Its cardinality is used for calculating the CBO.
     */
    private HashSet<String> efferentCoupledClasses = new HashSet<String>();
    /** Methods encountered.
     * Its cardinality is used for calculating the RFC.
     */
    private HashSet<String> responseSet = new HashSet<String>();
    /** Use of fields in methods.
     * Its contents are used for calculating the LCOM.
     * We use a Tree rather than a Hash to calculate the
     * intersection in O(n) instead of O(n*n).
     */
    ArrayList<TreeSet<String>> mi = new ArrayList<TreeSet<String>>();

    public ClassVisitor(JavaClass jc, ClassMetricsContainer classMap) {
	visitedClass = jc;
	cp = new ConstantPoolGen(visitedClass.getConstantPool());
	cmap = classMap;
	myClassName = jc.getClassName();
	cm = cmap.getMetrics(myClassName);
    }

    /** Return the class's metrics container. */
    public ClassMetrics getMetrics() { return cm; }

    public void start() {
	visitJavaClass(visitedClass);
    }

    /** Calculate the class's metrics based on its elements. */
    public void visitJavaClass(JavaClass jc) {
	String super_name   = jc.getSuperclassName();
	String package_name = jc.getPackageName();

	cm.setVisited();
	if (jc.isPublic())
	    cm.setPublic();
	ClassMetrics pm = cmap.getMetrics(super_name);

	pm.incNoc();
	try {
	    cm.setDit(jc.getSuperClasses().length);
	} catch( ClassNotFoundException ex) {
	    System.err.println("Error obtaining all superclasses of " + jc);
	}
	registerCoupling(super_name);

	String ifs[] = jc.getInterfaceNames();
	/* Measuring decision: couple interfaces */
	for (int i = 0; i < ifs.length; i++)
	    registerCoupling(ifs[i]);

	Field[] fields = jc.getFields();
	for(int i=0; i < fields.length; i++)
	    fields[i].accept(this);

	Method[] methods = jc.getMethods();
	for(int i=0; i < methods.length; i++)
	    methods[i].accept(this);
    }

    /** Add a given class to the classes we are coupled to */
    public void registerCoupling(String className) {
	/* Measuring decision: don't couple to Java SDK */
	if ((MetricsFilter.isJdkIncluded() ||
	     !ClassMetrics.isJdkClass(className)) &&
	    !myClassName.equals(className)) {
	    efferentCoupledClasses.add(className);
	    cmap.getMetrics(className).addAfferentCoupling(myClassName);
	}
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
    void registerMethodInvocation(String className, String methodName, Type[] args) {
	registerCoupling(className);
	/* Measuring decision: calls to JDK methods are included in the RFC calculation */
	incRFC(className, methodName, args);
    }

    /** Called when a field access is encountered. */
    public void visitField(Field field) {
	registerCoupling(field.getType());
    }

    /** Called when encountering a method that should be included in the
        class's RFC. */
    private void incRFC(String className, String methodName, Type[] arguments) {
        String argumentList = Arrays.asList(arguments).toString();
        // remove [ ] chars from begin and end
        String args = argumentList.substring(1, argumentList.length() - 1);
        String signature = className + "." + methodName + "(" + args + ")";
        responseSet.add(signature);
    }

    /** Called when a method invocation is encountered. */
    public void visitMethod(Method method) {
	MethodGen mg = new MethodGen(method, visitedClass.getClassName(), cp);

	Type   result_type = mg.getReturnType();
	Type[] argTypes   = mg.getArgumentTypes();

	registerCoupling(mg.getReturnType());
	for (int i = 0; i < argTypes.length; i++)
	    registerCoupling(argTypes[i]);

	String[] exceptions = mg.getExceptions();
	for (int i = 0; i < exceptions.length; i++)
	    registerCoupling(exceptions[i]);

	/* Measuring decision: A class's own methods contribute to its RFC */
	incRFC(myClassName, method.getName(), argTypes);

	cm.incWmc();
	if (Modifier.isPublic(method.getModifiers()))
		cm.incNpm();
	mi.add(new TreeSet<String>());
	MethodVisitor factory = new MethodVisitor(mg, this);
	factory.start();
    }

    /** Return a class name associated with a type. */
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

    /** Do final accounting at the end of the visit. */
    public void end() {
	cm.setCbo(efferentCoupledClasses.size());
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
