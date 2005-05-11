/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\PrintPlainResults.java,v 1.1 2005/05/11 20:40:31 dds Exp $
 *
 * (C) Copyright 2005 Diomidis Spinellis, Julien Rentrop
 *
 * Permission to use, copy, and distribute this software and its documentation
 * for any purpose and without fee is hereby granted, provided that the above
 * copyright notice appear in all copies and that both that copyright notice and
 * this permission notice appear in supporting documentation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package gr.spinellis.ckjm;

import java.io.PrintStream;

/**
 * Simple plain text output formatter
 * @author Julien Rentrop
 */
public class PrintPlainResults implements CkjmOutputHandler {
    private PrintStream p;

    public PrintPlainResults (PrintStream p) {
        this.p = p;
    }

    public void handleClass(String name, ClassMetrics c) {
        p.println(name + " " + c.toString());
    }
}
