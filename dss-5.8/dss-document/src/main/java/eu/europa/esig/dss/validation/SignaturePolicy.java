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
package eu.europa.esig.dss.validation;

import eu.europa.esig.dss.enumerations.SignaturePolicyType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.Digest;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Represents the value of a SignaturePolicy
 *
 */
public class SignaturePolicy {

	/** The signature policy identifier */
	private final String identifier;

	/** If it is a zero-hash policy */
	private boolean zeroHash;

	/** The digest of the signature policy */
	private Digest digest;

	/** The policy content document */
	private DSSDocument policyContent;

	/** The policy description */
	private String description;

	/** The documentation references */
	private List<String> documentationReferences;

	/** The transforms Element (used in XAdES) */
	private Element transforms;

	/** The build list of transforms descriptions */
	private List<String> transformsDescription;

	/**
	 * Two qualifiers for the signature policy have been identified so far:
	 * ...
	 * A URL where a copy of the signature policy MAY be obtained;
	 */
	private String url;

	/**
	 * Two qualifiers for the signature policy have been identified so far:
	 * ...
	 * User notice that should be displayed when the signature is verified.
	 */
	private String notice;

	/**
	 * The default constructor for SignaturePolicy. It represents the implied policy.
	 */
	public SignaturePolicy() {
		this.identifier = SignaturePolicyType.IMPLICIT_POLICY.name();
	}

	/**
	 * The default constructor for SignaturePolicy.
	 *
	 * @param identifier
	 *            the policy identifier
	 */
	public SignaturePolicy(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Returns the signature policy identifier
	 * 
	 * @return the signature policy identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets if the policy is a zero-hash (no hash check shall be performed)
	 *
	 * @return TRUE if the policy is a zero-hash, FALSE otherwise
	 */
	public boolean isZeroHash() {
		return zeroHash;
	}

	/**
	 * Sets if the policy is a zero-hash (no hash check shall be performed)
	 *
	 * @param zeroHash if the policy is a zero-hash
	 */
	public void setZeroHash(boolean zeroHash) {
		this.zeroHash = zeroHash;
	}

	/**
	 * Gets the {@code Digest}
	 *
	 * @return {@link Digest}
	 */
	public Digest getDigest() {
		return digest;
	}

	/**
	 * Sets the {@code Digest}
	 *
	 * @param digest {@link Digest}
	 */
	public void setDigest(Digest digest) {
		this.digest = digest;
	}

	/**
	 * Returns the signature policy url (if found)
	 * 
	 * @return the url of the signature policy (or null if not available information)
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the signature policy url
	 *
	 * @param url signature policy url
	 */
	public void setUrl(final String url) {
		this.url = url;
	}
	
	/**
	 * Gets description
	 *
	 * @return {@link String}
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets description (*optional)
	 *
	 * @param description {@link String}
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets the documentation references
	 * NOTE: optional, used in XAdES
	 * 
	 * @return a list of {@link String} documentation references
	 */
	public List<String> getDocumentationReferences() {
		return documentationReferences;
	}

	/**
	 * Sets the documentation references
	 *
	 * @param documentationReferences a list of {@link String} documentation references
	 */
	public void setDocumentationReferences(List<String> documentationReferences) {
		this.documentationReferences = documentationReferences;
	}

	/**
	 * Returns a 'ds:Tranforms' element if found
	 * NOTE: XAdES only
	 * 
	 * @return 'ds:Tranforms' {@link Element} if found, NULL otherwise
	 */
	public Element getTransforms() {
		return transforms;
	}

	/**
	 * Sets a 'ds:Tranforms' node
	 * 
	 * @param transforms {@link Element}
	 */
	public void setTransforms(Element transforms) {
		this.transforms = transforms;
	}

	/**
	 * Gets a list of Strings describing the 'ds:Tranforms' element
	 * NOTE: XAdES only
	 * 
	 * @return a description of 'ds:Tranforms' if present, null otherwise
	 */
	public List<String> getTransformsDescription() {
		return transformsDescription;
	}

	/**
	 * Sets a list of Strings describing the 'ds:Tranforms' element
	 * 
	 * @param transformsDescription a list of Strings describing the 'ds:Transforms' element
	 */
	public void setTransformsDescription(List<String> transformsDescription) {
		this.transformsDescription = transformsDescription;
	}

	/**
	 * Gets user notice that should be displayed when the signature is verified
	 *
	 * @return {@link String}
	 */
	public String getNotice() {
		return notice;
	}

	/**
	 * Sets user notice that should be displayed when the signature is verified
	 *
	 * @param notice {@link String} user notice
	 */
	public void setNotice(final String notice) {
		this.notice = notice;
	}

	/**
	 * Returns a DSSDocument with the signature policy content
	 * 
	 * @return a DSSDocument which contains the signature policy
	 */
	public DSSDocument getPolicyContent() {
		return policyContent;
	}

	/**
	 * Sets policy document content
	 *
	 * @param policyContent {@link DSSDocument}
	 */
	public void setPolicyContent(DSSDocument policyContent) {
		this.policyContent = policyContent;
	}

}
