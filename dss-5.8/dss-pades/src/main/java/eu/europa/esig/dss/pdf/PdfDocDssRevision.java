/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.pdf;

import java.util.List;
import java.util.Objects;

import eu.europa.esig.dss.pades.validation.PdfModificationDetection;
import eu.europa.esig.dss.pades.validation.PdfRevision;
import eu.europa.esig.dss.pades.validation.PdfSignatureDictionary;

/**
 * This class represents an LT-level PDF revision containing a DSS dictionary
 *
 */
public class PdfDocDssRevision implements PdfRevision {
	
	private final PdfDssDict dssDictionary;
	
	public PdfDocDssRevision(PdfDssDict dssDictionary) {
		Objects.requireNonNull(dssDictionary, "The dssDictionary cannot be null!");
		this.dssDictionary = dssDictionary;
	}

	/**
	 * Returns DSS dictionary
	 * 
	 * @return {@link PdfDssDict}
	 */
	public PdfDssDict getDssDictionary() {
		return dssDictionary;
	}

	@Override
	public PdfSignatureDictionary getPdfSigDictInfo() {
		// not applicable for DSS revision
		return null;
	}

	@Override
	public List<String> getFieldNames() {
		// not applicable for DSS revision
		return null;
	}

	@Override
	public PdfModificationDetection getModificationDetection() {
		// not applicable
		return null;
	}

}
