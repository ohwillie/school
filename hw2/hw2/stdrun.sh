#!/bin/bash
# $Id: stdrun.sh,v 1.10 2010/01/29 06:34:17 zahorjan Exp $

########################################################################################
# YOU MUST HAVE ALREADY REGISTERED AT
#  http://abstract.cs.washington.edu/~zahorjan/cse461/10wi/hw2/registerUser.cgi
# FOR THE DATA UPLOAD PORTION OF THIS SCRIPT TO WORK!

# YOU MUST CHANGE THE NEXT TWO LINES.
#   1. Change "xxx" on the next line to your CSENETID (not screen name)
#   2. Change "xxx" on the second next line to your HW2 screen name password (NOT YOUR CSENETID PASSWORD).
########################################################################################
CSENETID='rob'
HW2PASSWORD='dashdashdash'

# if you don't like the idea of editing cseneetid/passwords into this file,
# you can also set some environment variables.  (See following code for details.)
if [[ ${CSENETID} == xxx && X${HW2ID} != X ]]; then
    CSENETID=${HW2ID}
fi
if [[ ${CSENETID} == xxx && X${USER} != X ]]; then
    CSENETID=${USER}
fi
if [[ ${HW2PASSWORD} == xxx && X${HW2PWD} != X ]]; then
    HW2PASSWORD=${HW2PWD}
fi

if [[ $# < 1 ]]; then
    echo 'You must give one or more arguments (i.e., one or more of uniform, gaussian, or consecutive).'
    exit 2
fi

TAGCNTS="20 40 80 120 160"
TRIALS=500
BER=.01
BW=100000
TIME=1000

OUTFILE='.hw2OutputFile'

if [[ $CSENETID == 'xxx' ]]; then
  echo You must edit this script to set CSENETID before using it
  exit 1
fi
if [[ $HW2PASSWORD == 'xxx' ]]; then
  echo You must edit this script to set HW2PASSWORD before using it
  exit 1
fi

for DIST in $*; do
  # create an empty output file
  echo '' >${OUTFILE}
  for n in ${TAGCNTS}; do
      java -classpath classes sim/RFIDSim ${TRIALS} $n ${DIST} ${BER} ${BW} ${TIME} | tee -a ${OUTFILE}
  done

  wget --post-data=`./prepare.pl ${OUTFILE} ${CSENETID} ${HW2PASSWORD}` -O .wgetResponse http://abstract.cs.washington.edu/~zahorjan/cse461/10wi/submithw2data.cgi >/dev/null 2>&1
done
