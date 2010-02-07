# System Makefile
# $Id: makefile,v 1.1 2010/01/26 07:31:59 zahorjan Exp $

SHELL   = bash
MAKEFLAGS = -r
ROOTDIR = ${shell pwd}
CLASSPATH = ${ROOTDIR}/classes
SOURCEPATH = ${ROOTDIR}
SUBDIRS = component frame sim
PACKAGESTRING = component:frame:sim

all:
	if [ ! -d classes ]; then mkdir classes; fi
	make clean
	javac -classpath ${CLASSPATH} -sourcepath ${SOURCEPATH} -d ${CLASSPATH} -Xlint sim/RFIDSim.java

jdoc:
	rm -rf javadoc
	javadoc -classpath '.' -d javadoc -sourcepath '.' -package -subpackages ${PACKAGESTRING}

include $(ROOTDIR)/std.mk
