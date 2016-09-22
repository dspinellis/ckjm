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
import java.util.*;
import java.io.*;


/**
 * A container of class metrics mapping class names to their metrics.
 * This class contains the the metrics for all class's during the filter's
 * operation.  Some metrics need to be updated as the program processes
 * other classes, so the class's metrics will be recovered from this
 * container to be updated.
 *
 * @version $Revision: 1.9 $
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

    /** Print the metrics of all the visited classes. */
    public void printMetrics(CkjmOutputHandler handler) {
	Set<Map.Entry<String, ClassMetrics>> entries = m.entrySet();
	Iterator<Map.Entry<String, ClassMetrics>> i;

	for (i = entries.iterator(); i.hasNext(); ) {
	    Map.Entry<String, ClassMetrics> e = i.next();
	    ClassMetrics cm = e.getValue();
	    if (cm.isVisited() && (MetricsFilter.includeAll() || cm.isPublic()))
		handler.handleClass(e.getKey(), cm);
	}
    }
}
