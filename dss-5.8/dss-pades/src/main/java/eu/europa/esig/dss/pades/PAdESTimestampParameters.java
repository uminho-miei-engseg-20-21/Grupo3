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
package eu.europa.esig.dss.pades;

import eu.europa.esig.dss.cades.signature.CAdESTimestampParameters;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.pdf.PAdESConstants;

import java.util.Date;

@SuppressWarnings("serial")
public class PAdESTimestampParameters extends CAdESTimestampParameters implements PAdESCommonParameters {
	
	/**
	 * Date of the timestamp
	 */
	protected Date timestampDate = new Date();
	
	/**
	 * This attribute define a length of a reserved space for the timestamp inside a /Contents attribute
	 * 
	 * Default value is 9472 (from PDFBox)
	 */
	private int timestampSize = 9472;

	/**
	 * This attribute allows to override the used Filter for a Timestamp.
	 * 
	 * Default value is Adobe.PPKLite
	 */
	private String timestampFilter = PAdESConstants.TIMESTAMP_DEFAULT_FILTER;

	/**
	 * This attribute allows to override the used subFilter for a Timestamp.
	 * 
	 * Default value is ETSI.RFC3161
	 */
	private String timestampSubFilter = PAdESConstants.TIMESTAMP_DEFAULT_SUBFILTER;

	/**
	 * This attribute is used to create a visible timestamp in PAdES form
	 */
	private SignatureImageParameters timestampImageParameters;
	
	/**
	 * Password used to encrypt a PDF
	 */
	private String passwordProtection;

	/**
	 * Empty constructor
	 */
	public PAdESTimestampParameters() {
	}

	/**
	 * Default constructor
	 *
	 * @param digestAlgorithm {@link DigestAlgorithm}
	 */
	public PAdESTimestampParameters(DigestAlgorithm digestAlgorithm) {
		super(digestAlgorithm);
	}

	/**
	 * The constructor is used internally to recreate parameters from CAdES Timestamp Parameters
	 *
	 * @param cadesTimestampParameters {@link CAdESTimestampParameters}
	 */
	PAdESTimestampParameters(CAdESTimestampParameters cadesTimestampParameters) {
		this(cadesTimestampParameters.getDigestAlgorithm());
	}

	@Override
	public String getFilter() {
		return timestampFilter;
	}

	/**
	 * Sets the filter
	 *
	 * @param timestampFilter {@link String}
	 */
	public void setFilter(String timestampFilter) {
		this.timestampFilter = timestampFilter;
	}

	@Override
	public String getSubFilter() {
		return timestampSubFilter;
	}

	/**
	 * Sets the sub filter
	 *
	 * @param timestampSubFilter {@link String}
	 */
	public void setSubFilter(String timestampSubFilter) {
		this.timestampSubFilter = timestampSubFilter;
	}

	@Override
	public SignatureImageParameters getImageParameters() {
		if (timestampImageParameters == null) {
			timestampImageParameters = new SignatureImageParameters();
		}
		return timestampImageParameters;
	}

	/**
	 * Sets the {@code SignatureImageParameters} for a visual timestamp creation
	 *
	 * @param timestampImageParameters {@link SignatureImageParameters}
	 */
	public void setImageParameters(SignatureImageParameters timestampImageParameters) {
		this.timestampImageParameters = timestampImageParameters;
	}

	@Override
	public int getContentSize() {
		return timestampSize;
	}

	/**
	 * This setter allows to reserve more than the default size for a timestamp
	 *
	 * Default : 9472 bytes
	 *
	 * @param timestampSize representing the reserved space for /Context element
	 */
	public void setContentSize(int timestampSize) {
		this.timestampSize = timestampSize;
	}

	@Override
	public Date getSigningDate() {
		return timestampDate;
	}

	/**
	 * Sets signature field id where the timestamp will be placed on
	 * 
	 * Deprecated. Use {@code getImageParameters().getFieldParameters().setFieldId()}
	 * 
	 * @param timestampFieldId {@link String}
	 */
	@Deprecated
	public void setFieldId(String timestampFieldId) {
		getImageParameters().getFieldParameters().setFieldId(timestampFieldId);
	}

	@Override
	public String getPasswordProtection() {
		return passwordProtection;
	}

	/**
	 * Sets a password string
	 * 
	 * @param passwordProtection {@link String} password to set
	 */
	public void setPasswordProtection(String passwordProtection) {
		this.passwordProtection = passwordProtection;
	}

}
