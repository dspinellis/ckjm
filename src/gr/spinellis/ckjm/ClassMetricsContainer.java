/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\ClassMetricsContainer.java,v 1.5 2005/02/18 19:35:48 dds Exp $
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

import java.util.*;
import java.io.*;


/** A container of class metrics
 * @version $Revision: 1.5 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
class ClassMetricsContainer {

    /** The map from class names to the corresponding metrics */
    private HashMap<String, ClassMetrics> m = new HashMap<String, ClassMetrics>();

    /** Return a class's metrics */
    public ClassMetrics getMetrics(String name) {
	ClassMetrics cm = m.get(name);
	if (cm == null) {
	    cm = new ClassMetrics();
	    m.put(name, cm);
	}
	return cm;
    }

    /** Print the metrics of all classes */
    public void printMetrics(PrintStream out) {
	Set<Map.Entry<String, ClassMetrics>> entries = m.entrySet();
	Iterator<Map.Entry<String, ClassMetrics>> i;

	for (i = entries.iterator(); i.hasNext(); ) {
	    Map.Entry<String, ClassMetrics> e = i.next();
	    if (!ClassMetrics.isJdkClass(e.getKey())) {
		ClassMetrics cm = e.getValue();
		if (cm.isVisited())
		    out.println(e.getKey() + " " + cm);
	    }
	}
    }
}
