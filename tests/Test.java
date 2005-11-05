/*
 * ckjm test data
 *
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\tests\\Test.java,v 1.2 2005/11/05 08:37:28 dds Exp $
 *
 */

import java.util.*;

/* NOC = 1, LCOM = 2 - 1 = 1 */
class Test {
    int fieldname_a;
    int fieldname_b;
    int fieldname_c;
    int fieldname_d;
    int fieldname_e;
    int fieldname_x;
    int fieldname_y;
    int fieldname_z;
    String fieldname_s1;
    String fieldname_s2[];
    Set<Integer> fieldname_s3;
    static int sa;

    Test() {
	System.out.println(
		fieldname_a + fieldname_b +
		fieldname_c + fieldname_d +
		fieldname_e);
    }

    int methodname_2(int i) {
	System.out.println("hi");
	return (fieldname_a + fieldname_b + fieldname_e);
    }

    AbstractCollection methodname_3(AbstractCollection a, ArrayList b[]) {
	System.out.println("hi");
	Integer i = new Integer(fieldname_x + fieldname_y + fieldname_z);
	return a;
    }
}

/* Coupling  via extension + DIT=2 */
class STest extends Test {
}

/* DIT=3 */
class SSTest extends STest {
}

/* Coupling (1) via field type */
class Test2 {
    Test a;
    Test b;
}

/* Coupling (1) via method invocation */
class Test3 {
    int foo() { Test a = null; return a.methodname_2(1); }
}

/* Coupling (2) via local variable type ctor */
class Test4 {
    void foo() { STest a = new SSTest(); a.methodname_2(4); }
}


/* Coupling (1) via field access */
class Test5 {
    int foo() { return Test.sa; }
}

/* Coupling (1) via method return type */
class Test6 {
    Test foo() { return null; }
}

/* Coupling (1) via method argument type; also public method */
class Test7 {
    public void foo(Test a) {}
}

