#
# $Id: \\dds\\src\\Research\\ckjm.RCS\\Makefile,v 1.1 2005/02/21 17:24:44 dds Exp $
#

VERSION=1.1
TARBALL=ckjm-$(VERSION).tar.gz
ZIPBALL=ckjm-$(VERSION).zip
DISTDIR=ckjm-$(VERSION)
WEBDIR=/dds/pubs/web/home/sw/ckjm
SRCFILE=README.txt LICENSE.txt build.xml src/*.java
BCEL=bcel-5.1.jar
ART=smallpic.jpg

all: antcompile

antcompile:
	ant -Dversion=$(VERSION)

$(TARBALL): docs Makefile
	-cmd /c rd /s/q $(DISTDIR)
	mkdir $(DISTDIR)
	mkdir $(DISTDIR)/{doc,src,lib,build}
	cp $(WEBDIR)/doc/* $(DISTDIR)/doc
	cp build/ckjm-$(VERSION).jar $(DISTDIR)/build
	cp lib/$(BCEL) $(DISTDIR)/lib
	for i in $(SRCFILE) ;\
	do\
	perl -p -e 'BEGIN {binmode(STDOUT);} s/\r//' $$i >$(DISTDIR)/$$i;\
	done
	tar cvf - $(DISTDIR) | gzip -c >$(TARBALL)
	zip -r $(ZIPBALL) $(DISTDIR)

docs:
	(cd doc && make)

web: $(TARBALL)
	cp $(ART) $(TARBALL) $(ZIPBALL) $(WEBDIR)
	sed "s/VERSION/$(VERSION)/g" index.html >$(WEBDIR)/index.html
