package gr.spinellis.jmetrics;

class ClassMetrics {

	ClassMetrics() {
		wmc = 0;
		noc = 0;
		cbo = 0;
		parent = null;
	}

	/** Weighted methods per class */
	private int wmc;
	/** Increment the weighted methods count */
	public void incWmc() { wmc++; }
	/** Return the weighted methods per class metric */
	public int getWmc() { return wmc; }

	/** Number of children */
	private int noc;
	/** Increment the number of children */
	public void incNoc() { noc++; }
	/** Return the number of children */
	public int getNoc() { return noc; }

	/** Response for a Class */
	private int rfc;
	/** Increment the Response for a Class */
	public void setRfc(int r) { rfc = r; }
	/** Return the Response for a Class */
	public int getRfc() { return rfc; }

	/** The class's parent class */
	private ClassMetrics parent;
	/* Set the class's parent */
	public void setParent(ClassMetrics p) { parent = p; }
	/** Return the class's parent */
	public ClassMetrics getParent() { return parent; }
	/** Return the depth of the class's inheritance tree */
	public int getDit() {
		int i = 0;
		ClassMetrics c = parent;
		while (c != null) {
			c = c.getParent();
			i++;
		}
		return i;
	}

	/** Coupling between object classes */
	private int cbo;
	/** Increment the coupling between object classes metric */
	public void setCbo(int c) { cbo = c; }
	/** Return the coupling between object classes metric */
	public int getCbo() { return cbo; }

	/** Lack of cohesion in methods */
	private int lcom;
	/** Return the class's lack of cohesion in methods metric */
	public int getLcom() { return lcom; }
	/** Set the class's lack of cohesion in methods metric */
	public void setLcom(int l) { lcom = l; }

	public String toString() {
		return
			"WMC=" + wmc +
			" DIT=" + getDit() +
			" NOC=" + noc +
			" CBO=" + cbo +
			" RFC=" + rfc +
			" LCOM=" + lcom;
	}
}
