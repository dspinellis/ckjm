/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\MethodVisitor.java,v 1.6 2005/02/18 19:35:48 dds Exp $
 *
 * (C) Copyright 2005 Diomidis Spinellis
 *
 * Permission to use, copy, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in
 * supporting documentation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package gr.spinellis.ckjm;

import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.Constants;
import org.apache.bcel.util.*;
import java.io.PrintWriter;
import java.util.*;

/**
 * Visit a method calculating the class's Chidamber-Kemerer metrics.
 * A helper class for ClassVisitor.
 *
 * @see ClassVisitor
 * @version $Revision: 1.6 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
class MethodVisitor extends EmptyVisitor {
  private MethodGen       _mg;
  private ConstantPoolGen _cp;
  private ClassVisitor    cv;
  private ClassMetrics    cm;

  MethodVisitor(MethodGen mg, ClassVisitor c) {
    _mg  = mg;
    cv = c;
    _cp  = mg.getConstantPool();
    cm = cv.getMetrics();
  }

  private HashMap branch_map = new HashMap(); // Map<Instruction, InstructionHandle>

  public void start() {

    if(!_mg.isAbstract() && !_mg.isNative()) {
      for(InstructionHandle ih = _mg.getInstructionList().getStart();
	  ih != null; ih = ih.getNext()) {
	Instruction i = ih.getInstruction();

	if(!visitInstruction(i))
	  i.accept(this);
      }

      updateExceptionHandlers();
    }
  }

  private boolean visitInstruction(Instruction i) {
    short opcode = i.getOpcode();

    if((InstructionConstants.INSTRUCTIONS[opcode] != null) &&
       !(i instanceof ConstantPushInstruction) &&
       !(i instanceof ReturnInstruction)) { // Handled below
      return true;
    }
    return false;
  }

  public void visitLocalVariableInstruction(LocalVariableInstruction i) {
    if(i.getOpcode() != Constants.IINC)
      cv.registerCoupling(i.getType(_cp));
  }

  public void visitArrayInstruction(ArrayInstruction i) {
    cv.registerCoupling(i.getType(_cp));
  }

  public void visitFieldInstruction(FieldInstruction i) {
    cv.registerFieldAccess(i.getClassName(_cp), i.getFieldName(_cp));
    cv.registerCoupling(i.getFieldType(_cp));
  }

  public void visitInvokeInstruction(InvokeInstruction i) {

    Type[] arg_types   = i.getArgumentTypes(_cp);
    for (int j = 0; j < arg_types.length; j++)
	    cv.registerCoupling(arg_types[j]);
    cv.registerCoupling(i.getReturnType(_cp));

    /* Measuring decision: don't measure overloaded methods separately */
    cv.registerMethodInvocation(i.getClassName(_cp), i.getMethodName(_cp));
  }

  public void visitINSTANCEOF(INSTANCEOF i) {
    cv.registerCoupling(i.getType(_cp));
  }

  public void visitCHECKCAST(CHECKCAST i) {
    cv.registerCoupling(i.getType(_cp));
  }

  public void visitReturnInstruction(ReturnInstruction i) {
    cv.registerCoupling(i.getType(_cp));
  }

  private void updateExceptionHandlers() {
    CodeExceptionGen[] handlers = _mg.getExceptionHandlers();

    /* Measuring decision: couple exceptions */
    for(int i=0; i < handlers.length; i++) {
      Type t = handlers[i].getCatchType();
      if (t != null)
      	cv.registerCoupling(t);
    }
  }
}
