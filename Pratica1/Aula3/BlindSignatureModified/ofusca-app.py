#!/bin/python3
# coding: latin-1
###############################################################################
# eVotUM - Electronic Voting System
#
# generateBlindData-app.py
#
# Cripto-7.1.1 - Commmad line app to exemplify the usage of blindData
#       function (see eccblind.py)
#
# Copyright (c) 2016 Universidade do Minho
# Developed by André Baptista - Devise Futures, Lda. (andre.baptista@devisefutures.com)
# Reviewed by Ricardo Barroso - Devise Futures, Lda. (ricardo.barroso@devisefutures.com)
#
# Reviewed and tested with Python 3 @Jan/2021 by
#      José Miranda - Devise Futures, Lda. (jose.miranda@devisefutures.com)
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
#
###############################################################################
"""
Command line app that receives Data and pRDashComponents from STDIN and writes
blindMessage and blindComponents and pRComponents to STDOUT.
"""

import argparse
import os
from eVotUM.Cripto import eccblind


def parseArgs():
    parser = argparse.ArgumentParser(description="")

    parser.add_argument('-msg', type=str)
    parser.add_argument('-RDash', type=str)
    parser.add_argument('-file', type=str, default='signed-bmsg.txt')

    args = parser.parse_args()

    main(args.msg, args.RDash, args.file)


def showResults(errorCode, result, file):
    if (errorCode is None):
        blindComponents, pRComponents, blindM = result

        print("Blind message: %s" % blindM)
        # print("Blind components: %s" % blindComponents)
        # print("pRComponents: %s" % pRComponents)
        with open(file, 'w') as f:
            f.write(f'{blindComponents}{os.linesep}')
            f.write(f'{pRComponents}{os.linesep}')

    elif (errorCode == 1):
        print("Error: pRDashComponents are invalid")


def main(msg, pRDashComponents, file):
    errorCode, result = eccblind.blindData(pRDashComponents, msg)
    
    showResults(errorCode, result, file)


if __name__ == "__main__":
    parseArgs()
