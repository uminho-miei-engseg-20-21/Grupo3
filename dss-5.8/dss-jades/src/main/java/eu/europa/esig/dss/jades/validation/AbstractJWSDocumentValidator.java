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
package eu.europa.esig.dss.jades.validation;

import eu.europa.esig.dss.jades.validation.scope.JAdESSignatureScopeFinder;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The abstract class for a JWS signature validation
 */
public abstract class AbstractJWSDocumentValidator extends SignedDocumentValidator {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractJWSDocumentValidator.class);

	/**
	 * Empty constructor
	 */
	protected AbstractJWSDocumentValidator() {
	}

	/**
	 * Default constructor
	 *
	 * @param document {@link DSSDocument} to validate
	 */
	protected AbstractJWSDocumentValidator(DSSDocument document) {
		super(new JAdESSignatureScopeFinder());
		this.document = document;
	}

	@Override
	public List<DSSDocument> getOriginalDocuments(String signatureId) {
		Objects.requireNonNull(signatureId, "Signature Id cannot be null");
		
		List<AdvancedSignature> signatures = getSignatures();
		JAdESSignature signatureById = getSignatureById(signatures, signatureId);
		if (signatureById == null) {
			LOG.warn("Signature with id {} not found", signatureId);
			return Collections.emptyList();
		}
		return signatureById.getOriginalDocuments();
	}
	
	private JAdESSignature getSignatureById(List<AdvancedSignature> signatures, String signatureId) {
		for (AdvancedSignature signature : signatures) {
			if (signatureId.equals(signature.getId())) {
				return (JAdESSignature) signature;
			}
			List<AdvancedSignature> counterSignatures = signature.getCounterSignatures();
			if (Utils.isCollectionNotEmpty(counterSignatures)) {
				JAdESSignature counterSignature = getSignatureById(counterSignatures, signatureId);
				if (counterSignature != null) {
					return counterSignature;
				}
			}
		}
		return null;
	}

	@Override
	public List<DSSDocument> getOriginalDocuments(AdvancedSignature advancedSignature) {
		final JAdESSignature jadesSignature = (JAdESSignature) advancedSignature;
		try {
			return jadesSignature.getOriginalDocuments();
		} catch (DSSException e) {
			LOG.error("Cannot retrieve a list of original documents");
			return Collections.emptyList();
		}
	}

}
