/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\CkjmOutputHandler.java,v 1.1 2005/05/11 20:40:31 dds Exp $
 *
 * (C) Copyright 2005 Diomidis Spinellis, Julien Rentrop
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

/**
 * Interface of output handlers
 * Use this interface to couple your tool to CKJM. Example implenations
 * which could use this tool are ant task writing, IDE integration,
 * GUI based interfaces etc.
 *
 * @author Julien Rentrop
 */
public interface CkjmOutputHandler {
    /**
     * Method called when metrics are generated
     * @param name Name of the class
     * @param c Value object that contains the corresponding metrics
     */
    void handleClass(String name, ClassMetrics c);
}
