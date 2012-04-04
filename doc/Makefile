#
# $Id: Makefile 1.1 2002/09/20 08:08:59 dds Exp dds $
#

DESTDIR=/dds/pubs/web/home/sw/ckjm
NOTETOOLS=/dds/pubs/courses/tools
XML := $(wildcard *.xml)

all: $(XML)
	sh $(UH)/$(NOTETOOLS)/coursebib.sh bib
	echo "ckjm - A Tool for Calculating Chidamber and Kemerer Java Metrics" | perl $(NOTETOOLS)/thread.pl -h html -r -p ../index.html index ${DESTDIR}/doc
