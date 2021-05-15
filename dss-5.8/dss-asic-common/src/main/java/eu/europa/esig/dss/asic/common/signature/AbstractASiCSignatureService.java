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
package eu.europa.esig.dss.asic.common.signature;

import eu.europa.esig.dss.asic.common.ASiCExtractResult;
import eu.europa.esig.dss.asic.common.ASiCParameters;
import eu.europa.esig.dss.asic.common.ASiCUtils;
import eu.europa.esig.dss.asic.common.AbstractASiCContainerExtractor;
import eu.europa.esig.dss.asic.common.ZipUtils;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.model.SerializableCounterSignatureParameters;
import eu.europa.esig.dss.model.SerializableSignatureParameters;
import eu.europa.esig.dss.model.SerializableTimestampParameters;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.signature.CounterSignatureService;
import eu.europa.esig.dss.signature.MultipleDocumentsSignatureService;
import eu.europa.esig.dss.signature.SigningOperation;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The abstract class containing the main methods for ASiC signature creation/extension
 */
public abstract class AbstractASiCSignatureService<SP extends SerializableSignatureParameters, TP extends SerializableTimestampParameters, 
					CSP extends SerializableCounterSignatureParameters> extends AbstractSignatureService<SP, TP> 
					implements MultipleDocumentsSignatureService<SP, TP>, CounterSignatureService<CSP> {

	private static final long serialVersionUID = 243114076381526665L;

	/** The extracted content (documents) of the ASiC container */
	protected ASiCExtractResult archiveContent = new ASiCExtractResult();

	/**
	 * The default constructor
	 *
	 * @param certificateVerifier {@link CertificateVerifier}
	 */
	protected AbstractASiCSignatureService(CertificateVerifier certificateVerifier) {
		super(certificateVerifier);
	}

	/**
	 * Returns the relevant signature document extension for the implemented format
	 *
	 * @return {@link String} signature extension
	 */
	protected abstract String getExpectedSignatureExtension();

	@Override
	public TimestampToken getContentTimestamp(DSSDocument toSignDocument, SP parameters) {
		return getContentTimestamp(Arrays.asList(toSignDocument), parameters);
	}

	@Override
	public ToBeSigned getDataToSign(DSSDocument toSignDocument, SP parameters) {
		Objects.requireNonNull(toSignDocument, "toSignDocument cannot be null!");
		return getDataToSign(Arrays.asList(toSignDocument), parameters);
	}

	@Override
	public DSSDocument signDocument(DSSDocument toSignDocument, SP parameters, SignatureValue signatureValue) {
		Objects.requireNonNull(toSignDocument, "toSignDocument cannot be null!");
		return signDocument(Arrays.asList(toSignDocument), parameters, signatureValue);
	}

	@Override
	public DSSDocument timestamp(DSSDocument toTimestampDocument, TP parameters) {
		Objects.requireNonNull(toTimestampDocument, "toTimestampDocument cannot be null!");
		return timestamp(Arrays.asList(toTimestampDocument), parameters);
	}

	/**
	 * Extracts the content (documents) of the ASiC container
	 *
	 * @param archive {@link DSSDocument} representing an ASiC container
	 */
	protected void extractCurrentArchive(DSSDocument archive) {
		AbstractASiCContainerExtractor extractor = getArchiveExtractor(archive);
		archiveContent = extractor.extract();
	}

	/**
	 * Returns a relevant ASiC container extractor for the given format
	 *
	 * @param archive {@link DSSDocument} to get an extractor for
	 * @return an instance of {@link AbstractASiCContainerExtractor}
	 */
	protected abstract AbstractASiCContainerExtractor getArchiveExtractor(DSSDocument archive);

	/**
	 * Returns a list of signature documents embedded into the ASiC container
	 *
	 * @return a list of {@link DSSDocument}s
	 */
	protected List<DSSDocument> getEmbeddedSignatures() {
		return archiveContent.getSignatureDocuments();
	}

	/**
	 * Returns a list of manifest documents embedded into the ASiC container
	 *
	 * @return a list of {@link DSSDocument}s
	 */
	protected List<DSSDocument> getEmbeddedManifests() {
		return archiveContent.getManifestDocuments();
	}

	/**
	 * Returns a list of archive manifest documents embedded into the ASiC container
	 *
	 * @return a list of {@link DSSDocument}s
	 */
	protected List<DSSDocument> getEmbeddedArchiveManifests() {
		return archiveContent.getArchiveManifestDocuments();
	}

	/**
	 * Returns a list of timestamp documents embedded into the ASiC container
	 *
	 * @return a list of {@link DSSDocument}s
	 */
	protected List<DSSDocument> getEmbeddedTimestamps() {
		return archiveContent.getTimestampDocuments();
	}

	/**
	 * Returns a list of signed documents embedded into the ASiC container
	 *
	 * @return a list of {@link DSSDocument}s
	 */
	protected List<DSSDocument> getEmbeddedSignedDocuments() {
		return archiveContent.getSignedDocuments();
	}

	/**
	 * Returns a mimetype document embedded into the ASiC container
	 *
	 * @return {@link DSSDocument}
	 */
	protected DSSDocument getEmbeddedMimetype() {
		return archiveContent.getMimeTypeDocument();
	}

	/**
	 * Creates a ZIP-Archive by copying the existing {@code archiveDocument} entries
	 * and overwriting matching ones with {@code filesToAdd}
	 * 
	 * @param archiveDocument {@link DSSDocument} the original ASiC container to
	 *                        extend
	 * @param filesToAdd      a list of {@link DSSDocument} signatures to embed
	 * @param creationTime    {@link Date} of the archive creation
	 * @param zipComment      {@link String}
	 * @return {@link DSSDocument} the merged ASiC Container
	 */
	protected DSSDocument mergeArchiveAndExtendedSignatures(DSSDocument archiveDocument, List<DSSDocument> filesToAdd,
			Date creationTime, String zipComment) {
		List<DSSDocument> containerEntriesList = getListOfArchiveDocumentToAdd(archiveDocument, filesToAdd);
		DSSDocument zipArchive = ZipUtils.getInstance().createZipArchive(containerEntriesList, creationTime,
				zipComment);
		zipArchive.setMimeType(archiveDocument.getMimeType());
		return zipArchive;
	}

	private List<DSSDocument> getListOfArchiveDocumentToAdd(DSSDocument archiveDocument, List<DSSDocument> filesToAdd) {
		List<DSSDocument> result = new ArrayList<>();
		List<String> filesToAddNames = DSSUtils.getDocumentNames(filesToAdd);
		List<DSSDocument> containerContent = ZipUtils.getInstance().extractContainerContent(archiveDocument);
		for (DSSDocument entry : containerContent) {
			if (!filesToAddNames.contains(entry.getName())) {
				result.add(entry);
			}
		}
		result.addAll(filesToAdd);
		return result;
	}

	/**
	 * Creates a ZIP-Archive by copying the provided documents to the new container
	 * 
	 * @param documentsToBeSigned    a list of {@link DSSDocument}s to be originally
	 *                               signed
	 * @param signatures             a list of {@link DSSDocument} representing
	 *                               signature
	 * @param metaInfFolderDocuments a list of {@link DSSDocument} representing a
	 *                               META-INF directory content
	 * @param asicParameters         {@link ASiCParameters}
	 * @param creationTime           {@link Date} of the archive creation
	 * @return {@link DSSDocument} the created ASiC Container
	 */
	protected DSSDocument buildASiCContainer(List<DSSDocument> documentsToBeSigned, List<DSSDocument> signatures,
			List<DSSDocument> metaInfFolderDocuments, ASiCParameters asicParameters, Date creationTime) {
		List<DSSDocument> containerEntriesList = getListOfArchiveDocumentToAdd(documentsToBeSigned, signatures,
				metaInfFolderDocuments, asicParameters);
		DSSDocument zipArchive = ZipUtils.getInstance().createZipArchive(containerEntriesList, creationTime,
				ASiCUtils.getZipComment(asicParameters));
		zipArchive.setMimeType(ASiCUtils.getMimeType(asicParameters));
		return zipArchive;
	}

	private List<DSSDocument> getListOfArchiveDocumentToAdd(List<DSSDocument> documentsToBeSigned,
			List<DSSDocument> signatures, List<DSSDocument> metaInfFolderDocuments, ASiCParameters asicParameters) {
		List<DSSDocument> result = new ArrayList<>();
		result.add(getMimetypeDocument(asicParameters));
		result.addAll(documentsToBeSigned);
		if (ASiCUtils.isASiCE(asicParameters)) {
			result.addAll(metaInfFolderDocuments);
		}
		result.addAll(signatures);
		return result;
	}

	private DSSDocument getMimetypeDocument(final ASiCParameters asicParameters) {
		final byte[] mimeTypeBytes = ASiCUtils.getMimeTypeString(asicParameters).getBytes(StandardCharsets.UTF_8);
		return new InMemoryDocument(mimeTypeBytes, ASiCUtils.MIME_TYPE);
	}

	/**
	 * Verifies a validity of counter signature parameters
	 *
	 * @param parameters counter signature parameters to verify
	 */
	protected void assertCounterSignatureParametersValid(CSP parameters) {
		if (Utils.isStringEmpty(parameters.getSignatureIdToCounterSign())) {
			throw new DSSException("The Id of a signature to be counter signed shall be defined! "
					+ "Please use SerializableCounterSignatureParameters.setSignatureIdToCounterSign(signatureId) method.");
		}
	}

	/**
	 * Verifies if incorporation of a SignaturePolicyStore is possible
	 */
	protected void assertAddSignaturePolicyStorePossible() {
		if (Utils.isCollectionEmpty(getEmbeddedSignatures())) {
			throw new UnsupportedOperationException(
					"Signature documents of the expected format are not found in the provided ASiC Container! "
					+ "Add a SignaturePolicyStore is not possible!");
		}
	}

	/**
	 * Generates and returns a final name for the archive to create
	 *
	 * @param originalFile {@link DSSDocument} original signed/extended document container
	 * @param operation {@link SigningOperation} the performed signing operation
	 * @param containerMimeType {@link MimeType} the expected mimeType
	 * @return {@link String} the archive filename
	 */
	protected String getFinalArchiveName(DSSDocument originalFile, SigningOperation operation, MimeType containerMimeType) {
		return getFinalDocumentName(originalFile, operation, null, containerMimeType);
	}

}
