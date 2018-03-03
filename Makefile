#
# $Id: \\dds\\src\\Research\\ckjm.RCS\\Makefile,v 1.15 2008/04/17 08:44:48 dds Exp $
#

VERSION=1.9
TARBALL=ckjm-$(VERSION).tar.gz
ZIPBALL=ckjm-$(VERSION).zip
DISTDIR=ckjm-$(VERSION)
WEBDIR=$(UH)/dds/pubs/web/home/sw/ckjm
SRCFILE=README.txt LICENSE.txt build.xml src/gr/spinellis/ckjm/*.java src/gr/spinellis/ckjm/ant/*.java xsl/*.xsl lib/*.txt
BCEL=bcel-6.0.jar
EGHTML=output_simple.html output_extra.html
ART=smallpic.jpg

all: antcompile

antcompile:
	ant -Dversion=$(VERSION) -Dbcel=$(BCEL)
	ant -Dversion=$(VERSION) -Dbcel=$(BCEL) javadocs

$(EGHTML): Makefile xsl/ckjm.xsl xsl/ckjm_extra.xsl
	ant -Dversion=$(VERSION) -Dbcel=$(BCEL) html

$(TARBALL): docs Makefile
	-cmd /c rd /s/q $(DISTDIR)
	mkdir $(DISTDIR)
	mkdir $(DISTDIR)/{doc,src,lib,build,xsl}
	mkdir -p $(DISTDIR)/src/gr/spinellis/ckjm/ant
	tar cf - javadoc contrib | tar -C $(DISTDIR) -xf -
	cp $(WEBDIR)/doc/* $(DISTDIR)/doc
	cp build/ckjm-$(VERSION).jar $(DISTDIR)/build
	cp lib/$(BCEL) $(DISTDIR)/lib
	for i in $(SRCFILE) ;\
	do \
		perl -p -e 'BEGIN {binmode(STDOUT);} s/\r//' $$i >$(DISTDIR)/$$i;\
	done
	perl -p -e 'BEGIN {binmode(STDOUT);} s/\r//;print q{<property name="version" value="'$(VERSION)'"/> } if (/VERSION/); if (/THE END/) { print qq{</project>\n}; last;}' build.xml >$(DISTDIR)/build.xml
	tar czvf $(TARBALL) $(DISTDIR)
	zip -r $(ZIPBALL) $(DISTDIR)

docs:
	#rm $(WEBDIR)/doc/*
	(cd doc && make)
	tar cf - javadoc | tar -C $(WEBDIR) -xf -

web: $(TARBALL) $(EGHTML)
	cp $(ART) $(EGHTML) $(TARBALL) $(ZIPBALL) $(WEBDIR)
	sed "s/VERSION/$(VERSION)/g" index.html >$(WEBDIR)/index.html

# A simple regression test
test: antcompile
	jar tf test/ckjm-1.8.jar | sed -n '/\.class$$/s/^/test\/ckjm-1.8.jar /p' | java -jar build/ckjm-$(VERSION).jar >test/new.txt
	diff test/old.txt test/new.txt && echo OK
