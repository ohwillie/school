#!/usr/bin/perl

use strict;
use URI::Escape;

my ($filename, $csenetid, $password) = @ARGV;

open (INFILE, "<$filename") || die "Couldn't open $filename";
my @data = <INFILE>;
print "file=", uri_escape(join("\n",@data)),
      "&csenetid=", uri_escape($csenetid),
      "&password=", uri_escape($password);
