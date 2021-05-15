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
package eu.europa.esig.dss.asic.cades.signature.asics;

import eu.europa.esig.dss.asic.cades.signature.GetDataToSignASiCWithCAdESHelper;
import eu.europa.esig.dss.asic.common.ASiCParameters;
import eu.europa.esig.dss.asic.common.ASiCUtils;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A class to generate a DataToSign with ASiC-S with CAdES from a files to be signed
 */
public class DataToSignASiCSWithCAdESFromFiles extends AbstractGetDataToSignASiCSWithCAdES implements GetDataToSignASiCWithCAdESHelper {

	/** The list of documents to be signed */
	private final List<DSSDocument> filesToBeSigned;

	/** The signing date */
	private final Date signingDate;

	/** The cached ToBeSigned document */
	private List<DSSDocument> signedDocuments;

	/**
	 * The default constructor
	 *
	 * @param filesToBeSigned a list of {@link DSSDocument}s to sign
	 * @param signingDate {@link Date} of signing
	 * @param asicParameters {@link ASiCParameters}
	 */
	public DataToSignASiCSWithCAdESFromFiles(final List<DSSDocument> filesToBeSigned, final Date signingDate,
											 final ASiCParameters asicParameters) {
		super(asicParameters);
		this.filesToBeSigned = filesToBeSigned;
		this.signingDate = signingDate;
	}

	@Override
	public String getSignatureFilename() {
		return getSignatureFileName();
	}

	@Override
	public String getTimestampFilename() {
		return getTimestampFileName();
	}

	@Override
	public DSSDocument getToBeSigned() {
		return getSignedDocuments().get(0);
	}

	@Override
	public List<DSSDocument> getDetachedContents() {
		return Collections.emptyList();
	}

	@Override
	public List<DSSDocument> getSignedDocuments() {
		if (signedDocuments == null) {
			if (Utils.collectionSize(filesToBeSigned) > 1) {
				DSSDocument packageZip = createPackageZip(filesToBeSigned, signingDate,
						ASiCUtils.getZipComment(asicParameters));
				signedDocuments = Arrays.asList(packageZip);
			} else {
				signedDocuments = new ArrayList<>(filesToBeSigned);
			}
		}
		return signedDocuments;
	}

	@Override
	public List<DSSDocument> getManifestFiles() {
		// No manifest file in ASiC-S
		return Collections.emptyList();
	}

	@Override
	public List<DSSDocument> getSignatures() {
		// new container
		return new ArrayList<>();
	}

	@Override
	public List<DSSDocument> getArchiveManifestFiles() {
		// not supported
		return Collections.emptyList();
	}

	@Override
	public List<DSSDocument> getTimestamps() {
		// new container
		return new ArrayList<>();
	}

}
