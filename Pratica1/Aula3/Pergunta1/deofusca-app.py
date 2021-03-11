#!/bin/python3
# coding: latin-1
###############################################################################
# eVotUM - Electronic Voting System
#
# unblindSignature-app.py
#
# Cripto-7.3.1 - Commmad line app to exemplify the usage of unblindSignature
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
Command line app that receives Blind signature, Blind components and prDashComponents
from STDIN and writes the unblinded signature to STDOUT.
"""

import argparse
import os
from eVotUM.Cripto import eccblind


def parseArgs():
    parser = argparse.ArgumentParser(description="")

    parser.add_argument('-s', type=str)
    parser.add_argument('-RDash', type=str)
    parser.add_argument('-file', type=str, default='signed-bmsg.txt')

    args = parser.parse_args()

    main(args.s, args.RDash, args.file)


def showResults(errorCode, signature):
    if (errorCode is None):
        print("Signature: %s" % signature)
    elif (errorCode == 1):
        print("Error: pRDash components are invalid")
    elif (errorCode == 2):
        print("Error: blind components are invalid")
    elif (errorCode == 3):
        print("Error: invalid blind signature format")


def main(blindSignature, pRDashComponents, blindComponentsFile):
    # @Jan/2021 - changed raw_input() to input()
    if not os.path.exists(blindComponentsFile):
        print(f'The file {blindComponentsFile} doesn\'t exist!')
        return

    with open(blindComponentsFile, 'r') as f:
        blindComponents, _ = f.readlines()

    errorCode, signature = eccblind.unblindSignature(blindSignature, pRDashComponents, blindComponents)
    
    showResults(errorCode, signature)


if __name__ == "__main__":
    parseArgs()
