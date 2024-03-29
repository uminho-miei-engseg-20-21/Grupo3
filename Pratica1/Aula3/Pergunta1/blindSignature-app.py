#!/bin/python3
# coding: latin-1
###############################################################################
# eVotUM - Electronic Voting System
#
# generateBlindSignature-app.py
#
# Cripto-7.2.1 - Commmad line app to exemplify the usage of generateBlindSignature
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
Command line app that receives signer's private key from file and Passphrase, Blind message and
initComponents from STDIN and writes Blind signature to STDOUT.
"""

import argparse
from eVotUM.Cripto import utils
from eVotUM.Cripto import eccblind


def parseArgs():
    parser = argparse.ArgumentParser(description="")

    parser.add_argument('-components', type=str, default='components.txt')
    parser.add_argument('-key', type=str)
    parser.add_argument('-bmsg', type=str)

    args = parser.parse_args()

    main(args.components, args.key, args.bmsg)


def showResults(errorCode, blindSignature):
    if (errorCode is None):
        print("Blind signature: %s" % blindSignature)
    elif (errorCode == 1):
        print("Error: it was not possible to retrieve the private key")
    elif (errorCode == 2):
        print("Error: init components are invalid")
    elif (errorCode == 3):
        print("Error: invalid blind message format")


def main(components_file, keyFile, blindMsg):
    with open(components_file, 'r') as f:
        initComponents, _ = f.readlines()

    pemKey = utils.readFile(keyFile)

    # @Jan/2021 - changed raw_input() to input()
    passphrase = input("Passphrase: ")

    errorCode, blindSignature = eccblind.generateBlindSignature(pemKey, passphrase, blindMsg, initComponents)

    showResults(errorCode, blindSignature)


if __name__ == "__main__":
    parseArgs()
