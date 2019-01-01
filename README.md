# Chidamber and Kemerer Java Metrics

[![DOI](https://zenodo.org/badge/3928910.svg)](https://zenodo.org/badge/latestdoi/3928910)


The program _ckjm_ calculates Chidamber and Kemerer object-oriented metrics by processing the bytecode of compiled Java files. The program calculates for each class the following six metrics proposed by Chidamber and Kemerer.

* WMC: Weighted methods per class
* DIT: Depth of Inheritance Tree
* NOC: Number of Children
* CBO: Coupling between object classes
* RFC: Response for a Class
* LCOM: Lack of cohesion in methods

In addition it also calculates for each class

* Ca: Afferent couplings
* NPM: Number of public methods

If you use this tool in your research, please cite the following paper.

Diomidis Spinellis. [Tool writing: A forgotten art?](http://www.spinellis.gr/pubs/jrnl/2005-IEEESW-TotT/html/v22n4.html) _IEEE Software_, 22(4):9–11, July/August 2005. [doi:10.1109/MS.2005.111](http://dx.doi.org/10.1109/MS.2005.111).

Visit the project's [home page](http://www.spinellis.gr/sw/ckjm/) for more information.
