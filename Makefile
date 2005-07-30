#
# $Id: \\dds\\src\\Research\\ckjm.RCS\\Makefile,v 1.5 2005/07/30 14:17:36 dds Exp $
#

VERSION=1.4
TARBALL=ckjm-$(VERSION).tar.gz
ZIPBALL=ckjm-$(VERSION).zip
DISTDIR=ckjm-$(VERSION)
WEBDIR=/dds/pubs/web/home/sw/ckjm
SRCFILE=README.txt LICENSE.txt build.xml src/*.java src/ant/*.java xsl/*.xsl
BCEL=bcel-5.1.jar
EGHTML=output_simple.html output_extra.html
ART=smallpic.jpg

all: antcompile

antcompile:
	ant -Dversion=$(VERSION)

$(EGHTML):
	ant -Dversion=$(VERSION) html

$(TARBALL): docs Makefile
	-cmd /c rd /s/q $(DISTDIR)
	mkdir $(DISTDIR)
	mkdir $(DISTDIR)/{doc,src,lib,build,xsl}
	mkdir $(DISTDIR)/src/ant
	cp $(WEBDIR)/doc/* $(DISTDIR)/doc
	cp build/ckjm-$(VERSION).jar $(DISTDIR)/build
	cp lib/$(BCEL) $(DISTDIR)/lib
	for i in $(SRCFILE) ;\
	do\
	perl -p -e 'BEGIN {binmode(STDOUT);} s/\r//' $$i >$(DISTDIR)/$$i;\
	done
	perl -p -e 'BEGIN {binmode(STDOUT);} s/\r//;print q{<property name="version" value="'$(VERSION)'"/> } if (/VERSION/)' build.xml >$(DISTDIR)/build.xml
	tar cvf - $(DISTDIR) | gzip -c >$(TARBALL)
	zip -r $(ZIPBALL) $(DISTDIR)

docs:
	#rm $(WEBDIR)/doc/*
	(cd doc && make)

web: $(TARBALL) $(EGHTML)
	cp $(ART) $(EGHTML) $(TARBALL) $(ZIPBALL) $(WEBDIR)
	sed "s/VERSION/$(VERSION)/g" index.html >$(WEBDIR)/index.html
