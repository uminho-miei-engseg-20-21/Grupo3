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
package eu.europa.esig.dss.asic.cades.signature;

import eu.europa.esig.dss.asic.cades.ASiCWithCAdESCommonParameters;
import eu.europa.esig.dss.asic.cades.ASiCWithCAdESContainerExtractor;
import eu.europa.esig.dss.asic.cades.signature.asice.DataToSignASiCEWithCAdESFromArchive;
import eu.europa.esig.dss.asic.cades.signature.asice.DataToSignASiCEWithCAdESFromFiles;
import eu.europa.esig.dss.asic.cades.signature.asics.DataToSignASiCSWithCAdESFromArchive;
import eu.europa.esig.dss.asic.cades.signature.asics.DataToSignASiCSWithCAdESFromFiles;
import eu.europa.esig.dss.asic.common.ASiCExtractResult;
import eu.europa.esig.dss.asic.common.ASiCUtils;
import eu.europa.esig.dss.asic.common.ZipUtils;
import eu.europa.esig.dss.asic.common.signature.AbstractASiCDataToSignHelperBuilder;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.signature.SigningOperation;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Builds a relevant {@code GetDataToSignASiCWithCAdESHelper} for ASiC with CAdES dataToSign creation
 */
public class ASiCWithCAdESDataToSignHelperBuilder extends AbstractASiCDataToSignHelperBuilder {

	/**
	 * Builds a {@code GetDataToSignASiCWithCAdESHelper} from the given list of documents and defined parameters
	 * 
	 * @param operation {@link SigningOperation}
	 * @param documents a list of {@link DSSDocument}s to get a helper from
	 * @param parameters {@link ASiCWithCAdESCommonParameters}
	 * @return {@link GetDataToSignASiCWithCAdESHelper}
	 */
	public GetDataToSignASiCWithCAdESHelper build(SigningOperation operation, List<DSSDocument> documents,
			ASiCWithCAdESCommonParameters parameters) {
		if (Utils.isCollectionNotEmpty(documents) && documents.size() == 1) {
			DSSDocument archiveDocument = documents.get(0);
			if (ASiCUtils.isZip(archiveDocument)) {
				List<String> filenames = ZipUtils.getInstance().extractEntryNames(archiveDocument);
				if (ASiCUtils.isAsicFileContent(filenames)) {
					return fromZipArchive(operation, archiveDocument, parameters);
				}
			}
		}
		return fromFiles(operation, documents, parameters);
	}
	
	private GetDataToSignASiCWithCAdESHelper fromZipArchive(SigningOperation operation, DSSDocument archiveDoc, 
			ASiCWithCAdESCommonParameters parameters) {
		ASiCWithCAdESContainerExtractor extractor = new ASiCWithCAdESContainerExtractor(archiveDoc);
		ASiCExtractResult result = extractor.extract();
		assertContainerTypeValid(result);

		if (Utils.isCollectionNotEmpty(result.getSignatureDocuments())
				|| Utils.isCollectionNotEmpty(result.getTimestampDocuments())) {

			ASiCContainerType currentContainerType = ASiCUtils.getContainerType(archiveDoc,
					result.getMimeTypeDocument(), result.getZipComment(), result.getSignedDocuments());

			boolean asice = ASiCUtils.isASiCE(parameters.aSiC());

			if (asice && ASiCContainerType.ASiC_E.equals(currentContainerType)) {
				return new DataToSignASiCEWithCAdESFromArchive(operation, result, parameters);
			} else if (!asice && ASiCContainerType.ASiC_S.equals(currentContainerType)) {
				return new DataToSignASiCSWithCAdESFromArchive(result, parameters.aSiC());
			} else {
				throw new UnsupportedOperationException(
						String.format("Original container type '%s' vs parameter : '%s'", currentContainerType,
								parameters.aSiC().getContainerType()));
			}
		}

		return fromFiles(operation, Arrays.asList(archiveDoc), parameters);
	}
	
	private GetDataToSignASiCWithCAdESHelper fromFiles(SigningOperation operation, List<DSSDocument> documents, 
			ASiCWithCAdESCommonParameters parameters) {
		assertDocumentNamesDefined(documents);
		if (ASiCUtils.isASiCE(parameters.aSiC())) {
			return new DataToSignASiCEWithCAdESFromFiles(operation, documents, parameters);
		} else {
			return new DataToSignASiCSWithCAdESFromFiles(documents, parameters.getZipCreationDate(), parameters.aSiC());
		}
	}

	private static void assertContainerTypeValid(ASiCExtractResult result) {
		if (ASiCUtils.areFilesContainSignatures(DSSUtils.getDocumentNames(result.getAllDocuments()))
				&& Utils.isCollectionEmpty(result.getSignatureDocuments())) {
			throw new UnsupportedOperationException("Container type doesn't match");
		}
	}

}
