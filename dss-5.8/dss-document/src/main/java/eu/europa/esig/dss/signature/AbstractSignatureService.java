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
package eu.europa.esig.dss.signature;

import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.model.SerializableSignatureParameters;
import eu.europa.esig.dss.model.SerializableTimestampParameters;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSSecurityProvider;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.Signature;
import java.util.Date;
import java.util.Objects;

/**
 * The abstract class containing the main methods for a signature creation/extension
 *
 * @param <SP> SignatureParameters
 * @param <TP> TimestampParameters
 */
@SuppressWarnings("serial")
public abstract class AbstractSignatureService<SP extends SerializableSignatureParameters, TP extends SerializableTimestampParameters> 
				implements DocumentSignatureService<SP, TP> {

	static {
		Security.addProvider(DSSSecurityProvider.getSecurityProvider());
	}

	private static final Logger LOG = LoggerFactory.getLogger(AbstractSignatureService.class);

	/** The TSPSourse to use for timestamp requests */
	protected TSPSource tspSource;

	/** The CertificateVerifier used for a certificate chain validation */
	protected final CertificateVerifier certificateVerifier;

	/**
	 * To construct a signature service the <code>CertificateVerifier</code> must be set and cannot be null.
	 *
	 * @param certificateVerifier
	 *            {@code CertificateVerifier} provides information on the sources to be used in the validation process
	 *            in the context of a signature.
	 */
	protected AbstractSignatureService(final CertificateVerifier certificateVerifier) {
		Objects.requireNonNull(certificateVerifier, "CertificateVerifier cannot be null !");
		this.certificateVerifier = certificateVerifier;
	}

	@Override
	public void setTspSource(final TSPSource tspSource) {
		this.tspSource = tspSource;
	}

	/**
	 * This method raises an exception if the signing rules forbid the use on an expired certificate.
	 *
	 * @param parameters
	 *            set of driving signing parameters
	 */
	protected void assertSigningDateInCertificateValidityRange(final SerializableSignatureParameters parameters) {
		if (parameters.getSigningCertificate() == null) {
			if (parameters.isGenerateTBSWithoutCertificate()) {
				return;
			} else {
				throw new DSSException("Signing Certificate is not defined!");
			}
		} else if (parameters.isSignWithExpiredCertificate()) {
			return;
		}
		final CertificateToken signingCertificate = parameters.getSigningCertificate();
		final Date notAfter = signingCertificate.getNotAfter();
		final Date notBefore = signingCertificate.getNotBefore();
		final Date signingDate = parameters.bLevel().getSigningDate();
		if (signingDate.after(notAfter) || signingDate.before(notBefore)) {
			throw new DSSException(String.format("Signing Date (%s) is not in certificate validity range (%s, %s).", signingDate.toString(),
					notBefore.toString(), notAfter.toString()));
		}
	}

	/**
	 * Generates and returns a final name for the document to create
	 *
	 * @param originalFile {@link DSSDocument} original signed/extended document
	 * @param operation {@link SigningOperation} the performed signing operation
	 * @param level {@link SignatureLevel} the final signature level
	 * @param containerMimeType {@link MimeType} the expected mimeType
	 * @return {@link String} the document filename
	 */
	protected String getFinalDocumentName(DSSDocument originalFile, SigningOperation operation, SignatureLevel level, MimeType containerMimeType) {
		StringBuilder finalName = new StringBuilder();

		String originalName = null;
		if (containerMimeType != null) {
			originalName = "container";
		} else {
			originalName = originalFile.getName();
		}

		String originalExtension = Utils.EMPTY_STRING;
		if (Utils.isStringNotEmpty(originalName)) {
			int dotPosition = originalName.lastIndexOf('.');
			if (dotPosition > 0) {
				// remove extension
				finalName.append(originalName.substring(0, dotPosition));
				originalExtension = originalName.substring(dotPosition + 1);
			} else {
				finalName.append(originalName);
			}
		} else {
			finalName.append("document");
		}
		
		switch (operation) {
			case SIGN:
				finalName.append("-signed");
				break;
			case COUNTER_SIGN:
				finalName.append("-counter-signed");
				break;
			case TIMESTAMP:
				finalName.append("-timestamped");
				break;
			case EXTEND:
				finalName.append("-extended");
				break;
			case ADD_SIG_POLICY_STORE:
				finalName.append("-sig-policy-store");
				break;
			default:
				throw new DSSException(String.format("The following operation '%s' is not supported!", operation));
		}

		if (level != null) {
			finalName.append('-');
			finalName.append(Utils.lowerCase(level.name().replace("_", "-")));
		}

		String extension = getFileExtensionString(level, containerMimeType);
		extension = Utils.isStringNotBlank(extension) ? extension : originalExtension;
		if (Utils.isStringNotBlank(extension)) {
			finalName.append('.');
			finalName.append(extension);
		}

		return finalName.toString();
	}
	
	private String getFileExtensionString(SignatureLevel level, MimeType containerMimeType) {
		if (containerMimeType != null) {
			return MimeType.getExtension(containerMimeType);
		} else if (level != null) {
			SignatureForm signatureForm = level.getSignatureForm();
			switch (signatureForm) {
				case XAdES:
					return "xml";
				case CAdES:
					return "pkcs7";
				case PAdES:
					return "pdf";
				case JAdES:
					// TODO : use another extension ?
					return "json";
				default:
					throw new DSSException("Unable to generate a full document name");
			}
		}
		return Utils.EMPTY_STRING;
	}

	/**
	 * Returns the final name for the document to create
	 *
	 * @param originalFile {@link DSSDocument} original signed/extended document
	 * @param operation {@link SigningOperation} the performed signing operation
	 * @return {@link String} the document filename
	 */
	protected String getFinalFileName(DSSDocument originalFile, SigningOperation operation) {
		return getFinalFileName(originalFile, operation, null);
	}

	/**
	 * Returns the final name for the document to create
	 *
	 * @param originalFile {@link DSSDocument} original signed/extended document
	 * @param operation {@link SigningOperation} the performed signing operation
	 * @param level {@link SignatureLevel} the final signature level
	 * @return {@link String} the document filename
	 */
	protected String getFinalFileName(DSSDocument originalFile, SigningOperation operation, SignatureLevel level) {
		return getFinalDocumentName(originalFile, operation, level, null);
	}

	@Override
	public DSSDocument timestamp(DSSDocument toTimestampDocument, TP parameters) {
		throw new UnsupportedOperationException("Unsupported operation for this file format");
	}

	@Override
	public boolean isValidSignatureValue(ToBeSigned toBeSigned, SignatureValue signatureValue, CertificateToken signingCertificate) {
		Objects.requireNonNull(toBeSigned, "ToBeSigned cannot be null!");
		Objects.requireNonNull(signatureValue, "SignatureValue cannot be null!");
		Objects.requireNonNull(signingCertificate, "CertificateToken cannot be null!");

		try {
			Signature signature = Signature.getInstance(signatureValue.getAlgorithm().getJCEId(), DSSSecurityProvider.getSecurityProviderName());
			signature.initVerify(signingCertificate.getPublicKey());
			signature.update(toBeSigned.getBytes());
			return signature.verify(signatureValue.getValue());
		} catch (GeneralSecurityException e) {
			LOG.error("Unable to verify the signature value : {}", e.getMessage());
			return false;
		}
	}

}

