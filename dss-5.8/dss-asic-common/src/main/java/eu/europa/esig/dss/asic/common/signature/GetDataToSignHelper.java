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

import eu.europa.esig.dss.model.DSSDocument;

import java.util.List;

/**
 * The interface defining a helper to create a {@code ToBeSigned} data
 */
public interface GetDataToSignHelper {

	/**
	 * Returns a signature filename
	 *
	 * @return {@link String} signature filename
	 */
	String getSignatureFilename();

	/**
	 * Returns a timestamp filename
	 *
	 * @return {@link String} timestamp filename
	 */
	String getTimestampFilename();

	/**
	 * Returns a list of signed documents
	 *
	 * @return a list of {@link DSSDocument} that has been signed
	 */
	List<DSSDocument> getSignedDocuments();

	/**
	 * Returns a list of signature documents
	 *
	 * @return a list of {@link DSSDocument} signatures
	 */
	List<DSSDocument> getSignatures();

	/**
	 * Returns a list of manifest documents
	 *
	 * @return a list of {@link DSSDocument} manifests
	 */
	List<DSSDocument> getManifestFiles();

}
