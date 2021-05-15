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

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.pades.validation.PAdESSignature;
import eu.europa.esig.dss.pades.validation.PdfRevision;
import eu.europa.esig.dss.pades.validation.RevocationInfoArchival;
import eu.europa.esig.dss.pdf.PdfCMSRevision;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.ByteRange;
import org.bouncycastle.asn1.ASN1Encodable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Utils for dealing with PAdES
 */
public final class PAdESUtils {

	private static final Logger LOG = LoggerFactory.getLogger(PAdESUtils.class);

	/**
	 * Defines a number of a first page in a document
	 */
	public static final int DEFAULT_FIRST_PAGE = 1;

	/** The starting bytes of a PDF document */
	private static final byte[] PDF_PREAMBLE = new byte[]{'%', 'P', 'D', 'F', '-'};

	private PAdESUtils() {
	}

	/**
	 * Returns the original signed content for the {@code padesSignature}
	 * 
	 * @param padesSignature {@link PAdESSignature}
	 * @return {@link InMemoryDocument}
	 */
	public static InMemoryDocument getOriginalPDF(final PAdESSignature padesSignature) {
		List<DSSDocument> coveredOriginalFile = padesSignature.getDetachedContents(); // coveredContent
		if (Utils.collectionSize(coveredOriginalFile) == 1) {
			// data before adding the signature value
			DSSDocument dataToBeSigned = coveredOriginalFile.get(0);
			ByteRange signatureByteRange = padesSignature.getPdfRevision().getByteRange();
			DSSDocument firstByteRangePart = DSSUtils.splitDocument(dataToBeSigned,
					signatureByteRange.getFirstPartStart(), signatureByteRange.getFirstPartEnd());
			return retrieveCompletePDFRevision(firstByteRangePart);
		}
		return null;
	}

	/**
	 * Returns the original signed content for the {@code pdfRevision}
	 * 
	 * @param pdfRevision {@link PdfRevision}
	 * @return {@link InMemoryDocument}
	 */
	public static InMemoryDocument getOriginalPDF(final PdfCMSRevision pdfRevision) {
		byte[] signedDocumentBytes = pdfRevision.getRevisionCoveredBytes();
		ByteRange signatureByteRange = pdfRevision.getByteRange();
		return retrievePreviousPDFRevision(new InMemoryDocument(signedDocumentBytes), signatureByteRange);
	}

	/**
	 * Retrieves the PDF document up to the previous PDF Revision, an empty document if such revision is not found
	 *
	 * @param document {@link DSSDocument} the original document
	 * @param byteRange {@link ByteRange} representing the signed revision, to get the previous covered PDF for
	 * @return {@link InMemoryDocument} the PDF document up to the signed revision
	 */
	public static InMemoryDocument retrievePreviousPDFRevision(DSSDocument document, ByteRange byteRange) {
		DSSDocument firstByteRangePart = DSSUtils.splitDocument(document,
				byteRange.getFirstPartStart(), byteRange.getFirstPartEnd());
		return retrieveCompletePDFRevision(firstByteRangePart);
	}

	/**
	 * Returns the PDF document up to the last complete PDF revision (up to the "%%EOF" string)
	 *
	 * @param firstByteRangePart {@link DSSDocument} the document to get last revision for
	 * @return {@link InMemoryDocument}
	 */
	private static InMemoryDocument retrieveCompletePDFRevision(DSSDocument firstByteRangePart) {
		final byte[] eof = new byte[] { '%', '%', 'E', 'O', 'F' };
		try (InputStream is = firstByteRangePart.openStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			ByteArrayOutputStream tempLine = new ByteArrayOutputStream();
			ByteArrayOutputStream tempRevision = new ByteArrayOutputStream();
			int b;
			while ((b = bis.read()) != -1) {

				tempLine.write(b);
				byte[] stringBytes = tempLine.toByteArray();

				if (Arrays.equals(stringBytes, eof)) {
					tempLine.close();
					tempLine = new ByteArrayOutputStream();

					tempRevision.write(stringBytes);
					int c = bis.read();
					// if \n
					if (c == 0x0a) {
						tempRevision.write(c);
					}
					// if \r
					else if (c == 0x0d) {
						int d = bis.read();
						// if \r\n
						if (d == 0x0a) {
							tempRevision.write(c);
							tempRevision.write(d);
						} else {
							tempLine.write(c);
							tempLine.write(d);
						}
					} else {
						tempLine.write(c);
					}
					baos.write(tempRevision.toByteArray());
					tempRevision.close();
					tempRevision = new ByteArrayOutputStream();
				} else if (b == 0x0a || stringBytes.length > eof.length) {
					tempRevision.write(tempLine.toByteArray());
					tempLine.close();
					tempLine = new ByteArrayOutputStream();
				}

			}
			tempLine.close();
			tempRevision.close();

			baos.flush();
			return new InMemoryDocument(baos.toByteArray(), "original.pdf", MimeType.PDF);

		} catch (IOException e) {
			throw new DSSException("Unable to retrieve the last revision", e);
		}
	}

	/**
	 * Returns a signed content according to the provided byteRange
	 * 
	 * @param dssDocument {@link DSSDocument} to extract the content from
	 * @param byteRange   {@link ByteRange} indicating which content range should be
	 *                    extracted
	 * @return extracted content
	 * @throws IOException in case if an exception occurs
	 */
	public static byte[] getSignedContent(DSSDocument dssDocument, ByteRange byteRange) throws IOException {
		// Adobe Digital Signatures in a PDF (p5): In Figure 4, the hash is calculated
		// for bytes 0 through 840, and 960 through 1200. [0, 840, 960, 1200]
		int beginning = byteRange.getFirstPartStart();
		int startSigValueContent = byteRange.getFirstPartEnd();
		int endSigValueContent = byteRange.getSecondPartStart();
		int endValue = byteRange.getSecondPartEnd();

		byte[] signedContentByteArray = new byte[startSigValueContent + endValue];

		try (InputStream is = dssDocument.openStream()) {

			DSSUtils.skipAvailableBytes(is, beginning);
			DSSUtils.readAvailableBytes(is, signedContentByteArray, 0, startSigValueContent);
			DSSUtils.skipAvailableBytes(is, (long) endSigValueContent - startSigValueContent - beginning);
			DSSUtils.readAvailableBytes(is, signedContentByteArray, startSigValueContent, endValue);

		} catch (IllegalStateException e) {
			LOG.error("Cannot extract signed content. Reason : {}", e.getMessage());
		}

		return signedContentByteArray;
	}

	/**
	 * Returns {@link RevocationInfoArchival} from the given encodable
	 * 
	 * @param encodable the encoded data to be parsed
	 * @return an instance of RevocationValues or null if the parsing failed
	 */
	public static RevocationInfoArchival getRevocationInfoArchival(ASN1Encodable encodable) {
		if (encodable != null) {
			try {
				return RevocationInfoArchival.getInstance(encodable);
			} catch (Exception e) {
				LOG.warn("Unable to parse RevocationInfoArchival", e);
			}
		}
		return null;
	}

	/**
	 * Checks if the given {@code DSSDocument} represents a PDF document
	 *
	 * @param document {@link DSSDocument} to check
	 * @return TRUE if the document is a PDF, FALSE otherwise
	 */
	public static boolean isPDFDocument(DSSDocument document) {
		return DSSUtils.compareFirstBytes(document, PDF_PREAMBLE);
	}

}
