package gr.spinellis.ckjm;

import java.util.*;
import java.io.*;

/** A container of class metrics */
class ClassMap {

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
