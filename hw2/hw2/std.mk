# Shared (included) makefile
# $Id: std.mk,v 1.1 2010/01/26 07:31:59 zahorjan Exp $

MAKEFLAGS := $(MAKEFLAGS) -r -R -s

.PHONY: all-subdirs clean

all-subdirs:
	@ for d in $(SUBDIRS); do $(MAKE) -C $$d all; done

clean:
	@ rm -f *~ *.bak *.class
	@ rm -rf javadoc
	@ find $$i \( -name '*~' -or -name '*.bak' -or -name '*.class' \) -delete 


