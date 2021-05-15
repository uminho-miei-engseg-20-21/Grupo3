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

import java.io.IOException;
import java.util.Date;

/**
 * The usage of this interface permit the user to choose the underlying PDF
 * library use to created PDF signatures.
 */
public interface PdfDict {

	/**
	 * Gets an embedded dictionary by name
	 *
	 * @param name {@link String} of a dictionary to extract
	 * @return {@link PdfDict}
	 */
	PdfDict getAsDict(String name);

	/**
	 * Gets the pdfArray by name
	 *
	 * @param name {@link String}
	 * @return {@link PdfArray}
	 */
	PdfArray getAsArray(String name);

	/**
	 * Gets binaries by dictionary name
	 *
	 * @param name {@link String}
	 * @return byte array
	 * @throws IOException if an exception occurs
	 */
	byte[] getBinariesValue(String name) throws IOException;

	/**
	 * Lists all encapsulated dictionary names
	 *
	 * @return an array of {@link String}s
	 */
	String[] list();

	/**
	 * Gets a string value by property name
	 *
	 * @param name {@link String} property name
	 * @return {@link String} value
	 */
	String getStringValue(String name);

	/**
	 * Gets a name of the dictionary
	 *
	 * @param name {@link String} property name
	 * @return {@link String} value
	 */
	String getNameValue(String name);

	/**
	 * Gets a date
	 *
	 * @param name {@link String} property name
	 * @return {@link String} value
	 */
	Date getDateValue(String name);

}
