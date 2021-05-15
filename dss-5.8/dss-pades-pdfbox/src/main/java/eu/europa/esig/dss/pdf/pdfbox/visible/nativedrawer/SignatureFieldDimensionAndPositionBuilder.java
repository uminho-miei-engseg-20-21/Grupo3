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
package eu.europa.esig.dss.pdf.pdfbox.visible.nativedrawer;

import eu.europa.esig.dss.enumerations.SignerTextHorizontalAlignment;
import eu.europa.esig.dss.enumerations.SignerTextVerticalAlignment;
import eu.europa.esig.dss.enumerations.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.enumerations.VisualSignatureAlignmentVertical;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pdf.AnnotationBox;
import eu.europa.esig.dss.pdf.pdfbox.visible.ImageRotationUtils;
import eu.europa.esig.dss.pdf.visible.CommonDrawerUtils;
import eu.europa.esig.dss.pdf.visible.ImageAndResolution;
import eu.europa.esig.dss.pdf.visible.ImageUtils;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Builds {@code SignatureFieldDimensionAndPosition}
 */
public class SignatureFieldDimensionAndPositionBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(SignatureFieldDimensionAndPositionBuilder.class);

	/** Visual signature parameters */
	private final SignatureImageParameters imageParameters;

	/** The page to add the visual signature into */
	private final PDPage page;

	/** The signature field rectangle */
	private final PDRectangle pageMediaBox;

	/** The font to use */
	private final PDFont pdFont;

	/** Cached {@code SignatureFieldDimensionAndPosition} */
	private SignatureFieldDimensionAndPosition dimensionAndPosition;

	private static final String NOT_SUPPORTED_VERTICAL_ALIGNMENT_ERROR_MESSAGE = "not supported vertical alignment: ";
	private static final String NOT_SUPPORTED_HORIZONTAL_ALIGNMENT_ERROR_MESSAGE = "not supported horizontal alignment: ";

	/**
	 * Default constructor
	 *
	 * @param imageParameters {@code SignatureImageParameters}
	 * @param page {@link PDPage} where the signature will be added
	 * @param pdFont {@link PDFont} to use for a text creation
	 */
	public SignatureFieldDimensionAndPositionBuilder(SignatureImageParameters imageParameters, PDPage page,
			PDFont pdFont) {
		this.imageParameters = imageParameters;
		this.page = page;
		this.pageMediaBox = new PDRectangle(page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
		this.pdFont = pdFont;
	}

	/**
	 * Builds the {@code SignatureFieldDimensionAndPosition}
	 *
	 * @return {@link SignatureFieldDimensionAndPosition}
	 * @throws IOException if an exception occurs
	 */
	public SignatureFieldDimensionAndPosition build() throws IOException {
		this.dimensionAndPosition = new SignatureFieldDimensionAndPosition();
		initDpi();
		assignImageBoundaryBox();
		alignHorizontally();
		alignVertically();
		rotateSignatureField();
		return this.dimensionAndPosition;
	}

	private void initDpi() {
		if (imageParameters.getImage() != null) {
			ImageAndResolution imageAndResolution;
			try {
				imageAndResolution = ImageUtils.readDisplayMetadata(imageParameters.getImage());
			} catch (Exception e) {
				LOG.warn("Cannot access the image metadata : {}. Returns default info.", e.getMessage());
				imageAndResolution = new ImageAndResolution(imageParameters.getImage(), imageParameters.getDpi(),
						imageParameters.getDpi());
			}
			dimensionAndPosition.setImageAndResolution(imageAndResolution);
			dimensionAndPosition.setImageDpi(imageParameters.getDpi());
		}
	}

	private void assignImageBoundaryBox() throws IOException {
		AnnotationBox imageBoundaryBox = ImageUtils.getImageBoundaryBox(imageParameters);
		float imageWidth = imageBoundaryBox.getWidth();
		float imageHeight = imageBoundaryBox.getHeight();

		SignatureFieldParameters fieldParameters = imageParameters.getFieldParameters();
		if (fieldParameters.getWidth() == 0) {
			imageWidth *= CommonDrawerUtils.getPageScaleFactor(dimensionAndPosition.getxDpi());
		}
		if (fieldParameters.getHeight() == 0) {
			imageHeight *= CommonDrawerUtils.getPageScaleFactor(dimensionAndPosition.getyDpi());
		}

		float width = imageWidth;
		float height = imageHeight;

		SignatureImageTextParameters textParameters = imageParameters.getTextParameters();
		// if text is present
		if (!textParameters.isEmpty()) {

			// adds an empty space
			imageWidth = toDpiTextPoint(imageWidth, dimensionAndPosition.getxDpi());
			imageHeight = toDpiTextPoint(imageHeight, dimensionAndPosition.getyDpi());
			width = imageWidth;
			height = imageHeight;

			// native implementation uses dpi-independent font
			AnnotationBox textBox = computeTextDimension(textParameters);
			float textWidth = textBox.getWidth() * CommonDrawerUtils.getTextScaleFactor(imageParameters.getDpi());
			float textHeight = textBox.getHeight() * CommonDrawerUtils.getTextScaleFactor(imageParameters.getDpi());
			if (imageParameters.getImage() != null) {
				textWidth /= CommonDrawerUtils.getTextScaleFactor(dimensionAndPosition.getxDpi());
				textHeight /= CommonDrawerUtils.getTextScaleFactor(dimensionAndPosition.getyDpi());
			}

			switch (textParameters.getSignerTextPosition()) {
			case LEFT:
				if (fieldParameters.getWidth() == 0) {
					width += imageParameters.getImage() != null || width == 0 ? textWidth : 0;
				} else {
					imageWidth -= imageParameters.getImage() != null || width == 0 ? textWidth : 0;
				}
				if (fieldParameters.getHeight() == 0) {
					height = Math.max(height, textHeight);
				}
				dimensionAndPosition.setImageX(width - imageWidth);
				textImageVerticalAlignment(height, imageHeight, textHeight);
				break;
			case RIGHT:
				if (fieldParameters.getWidth() == 0) {
					width += imageParameters.getImage() != null || width == 0 ? textWidth : 0;
				} else {
					imageWidth -= imageParameters.getImage() != null || width == 0 ? textWidth : 0;
				}
				if (fieldParameters.getHeight() == 0) {
					height = Math.max(height, textHeight);
				}
				dimensionAndPosition.setTextX(toDpiPagePoint(width - textWidth, dimensionAndPosition.getxDpi()));
				textImageVerticalAlignment(height, imageHeight, textHeight);
				break;
			case TOP:
				if (fieldParameters.getWidth() == 0) {
					width = Math.max(width, textWidth);
				}
				if (fieldParameters.getHeight() == 0) {
					height += imageParameters.getImage() != null || height == 0 ? textHeight : 0;
				} else {
					imageHeight -= imageParameters.getImage() != null || height == 0 ? textHeight : 0;
				}
				dimensionAndPosition.setTextY(toDpiPagePoint(height - textHeight, dimensionAndPosition.getyDpi()));
				textImageHorizontalAlignment(width, imageWidth, textWidth);
				break;
			case BOTTOM:
				if (fieldParameters.getWidth() == 0) {
					width = Math.max(width, textWidth);
				}
				if (fieldParameters.getHeight() == 0) {
					height += imageParameters.getImage() != null || height == 0 ? textHeight : 0;
				} else {
					imageHeight -= imageParameters.getImage() != null || height == 0 ? textHeight : 0;
				}
				dimensionAndPosition.setImageY(height - imageHeight);
				textImageHorizontalAlignment(width, imageWidth, textWidth);
				break;
			default:
				break;
			}

			dimensionAndPosition.setTextWidth(toDpiPagePoint(textWidth, dimensionAndPosition.getxDpi()));
			dimensionAndPosition.setTextHeight(toDpiPagePoint(textHeight, dimensionAndPosition.getyDpi()));
			dimensionAndPosition.paddingShift(textParameters.getPadding());

			width = toDpiPagePoint(width, dimensionAndPosition.getxDpi());
			height = toDpiPagePoint(height, dimensionAndPosition.getyDpi());
		}

		int rotation = ImageRotationUtils.getRotation(imageParameters.getRotation(), page);
		if (ImageRotationUtils.isSwapOfDimensionsRequired(rotation)) {
			float temp = width;
			width = height;
			height = temp;
			pageMediaBox.setUpperRightX(page.getMediaBox().getHeight());
			pageMediaBox.setUpperRightY(page.getMediaBox().getWidth());
		}
		dimensionAndPosition.setGlobalRotation(rotation);

		dimensionAndPosition.setImageWidth(imageWidth);
		dimensionAndPosition.setImageHeight(imageHeight);
		dimensionAndPosition.setBoxWidth(width);
		dimensionAndPosition.setBoxHeight(height);
	}

	private AnnotationBox computeTextDimension(SignatureImageTextParameters textParameters) throws IOException {
		float properSize = CommonDrawerUtils.computeProperSize(textParameters.getFont().getSize(),
				imageParameters.getDpi());
		properSize *= ImageUtils.getScaleFactor(imageParameters.getZoom()); // scale text block

		PdfBoxFontMetrics pdfBoxFontMetrics = new PdfBoxFontMetrics(pdFont);
		return pdfBoxFontMetrics.computeTextBoundaryBox(textParameters.getText(), properSize,
				textParameters.getPadding());
	}

	private void textImageVerticalAlignment(double height, double imageHeight, float textHeight) {
		SignerTextVerticalAlignment verticalAlignment = imageParameters.getTextParameters()
				.getSignerTextVerticalAlignment();
		switch (verticalAlignment) {
		case TOP:
			dimensionAndPosition.setTextY(toDpiPagePoint((height - textHeight), dimensionAndPosition.getyDpi()));
			dimensionAndPosition.setImageY((float) (height - imageHeight));
			break;
		case BOTTOM:
			dimensionAndPosition.setTextY(0);
			dimensionAndPosition.setImageY(0);
			break;
		case MIDDLE:
			dimensionAndPosition.setTextY(toDpiPagePoint((height - textHeight) / 2, dimensionAndPosition.getyDpi()));
			dimensionAndPosition.setImageY((float) (height - imageHeight) / 2);
			break;
		default:
			throw new IllegalStateException(NOT_SUPPORTED_VERTICAL_ALIGNMENT_ERROR_MESSAGE + verticalAlignment);
		}
	}

	private void textImageHorizontalAlignment(double width, double imageWidth, float textWidth) {
		SignerTextHorizontalAlignment horizontalAlignment = imageParameters.getTextParameters()
				.getSignerTextHorizontalAlignment();
		switch (horizontalAlignment) {
		case LEFT:
			dimensionAndPosition.setTextX(0);
			dimensionAndPosition.setImageX(0);
			break;
		case RIGHT:
			dimensionAndPosition.setTextX(toDpiPagePoint((width - textWidth), dimensionAndPosition.getxDpi()));
			dimensionAndPosition.setImageX((float) (width - imageWidth));
			break;
		case CENTER:
			dimensionAndPosition.setTextX(toDpiPagePoint((width - textWidth) / 2, dimensionAndPosition.getxDpi()));
			dimensionAndPosition.setImageX((float) (width - imageWidth) / 2);
			break;
		default:
			throw new IllegalStateException(NOT_SUPPORTED_HORIZONTAL_ALIGNMENT_ERROR_MESSAGE + horizontalAlignment);
		}
	}

	private void alignHorizontally() {
		float boxWidth = dimensionAndPosition.getBoxWidth();
		if (ImageRotationUtils.isSwapOfDimensionsRequired(dimensionAndPosition.getGlobalRotation())) {
			boxWidth = dimensionAndPosition.getBoxHeight();
		}
		float boxX;
		SignatureFieldParameters fieldParameters = imageParameters.getFieldParameters();

		VisualSignatureAlignmentHorizontal alignmentHorizontal = imageParameters
				.getVisualSignatureAlignmentHorizontal();
		switch (alignmentHorizontal) {
		case LEFT:
		case NONE:
			boxX = fieldParameters.getOriginX();
			break;
		case CENTER:
			boxX = (pageMediaBox.getWidth() - boxWidth) / 2;
			break;
		case RIGHT:
			boxX = pageMediaBox.getWidth() - boxWidth - fieldParameters.getOriginX();
			break;
		default:
			throw new IllegalStateException(NOT_SUPPORTED_HORIZONTAL_ALIGNMENT_ERROR_MESSAGE + alignmentHorizontal);
		}
		dimensionAndPosition.setBoxX(boxX);
	}

	private void alignVertically() {
		float boxHeight = dimensionAndPosition.getBoxHeight();
		if (ImageRotationUtils.isSwapOfDimensionsRequired(dimensionAndPosition.getGlobalRotation())) {
			boxHeight = dimensionAndPosition.getBoxWidth();
		}
		float boxY;
		SignatureFieldParameters fieldParameters = imageParameters.getFieldParameters();

		VisualSignatureAlignmentVertical alignmentVertical = imageParameters.getVisualSignatureAlignmentVertical();
		switch (alignmentVertical) {
		case TOP:
		case NONE:
			boxY = fieldParameters.getOriginY();
			break;
		case MIDDLE:
			boxY = (pageMediaBox.getHeight() - boxHeight) / 2;
			break;
		case BOTTOM:
			boxY = pageMediaBox.getHeight() - boxHeight - fieldParameters.getOriginY();
			break;
		default:
			throw new IllegalStateException(NOT_SUPPORTED_VERTICAL_ALIGNMENT_ERROR_MESSAGE + alignmentVertical);
		}
		dimensionAndPosition.setBoxY(boxY);
	}

	private void rotateSignatureField() {
		switch (dimensionAndPosition.getGlobalRotation()) {
		case ImageRotationUtils.ANGLE_90:
			float boxX = dimensionAndPosition.getBoxX();
			dimensionAndPosition.setBoxX(
					pageMediaBox.getHeight() - dimensionAndPosition.getBoxY() - dimensionAndPosition.getBoxWidth());
			dimensionAndPosition.setBoxY(boxX);
			break;
		case ImageRotationUtils.ANGLE_180:
			dimensionAndPosition.setBoxX(
					pageMediaBox.getWidth() - dimensionAndPosition.getBoxX() - dimensionAndPosition.getBoxWidth());
			dimensionAndPosition.setBoxY(
					pageMediaBox.getHeight() - dimensionAndPosition.getBoxY() - dimensionAndPosition.getBoxHeight());
			break;
		case ImageRotationUtils.ANGLE_270:
			boxX = dimensionAndPosition.getBoxX();
			dimensionAndPosition.setBoxX(dimensionAndPosition.getBoxY());
			dimensionAndPosition.setBoxY(pageMediaBox.getWidth() - boxX - dimensionAndPosition.getBoxHeight());
			break;
		case ImageRotationUtils.ANGLE_360:
			// do nothing
			break;
		default:
			throw new IllegalStateException(ImageRotationUtils.SUPPORTED_ANGLES_ERROR_MESSAGE);
		}
	}

	// decrease size
	private float toDpiPagePoint(double x, Integer dpi) {
		return CommonDrawerUtils.toDpiAxisPoint((float) x, CommonDrawerUtils.getDpi(dpi));
	}

	// increase size
	private float toDpiTextPoint(double x, Integer dpi) {
		return CommonDrawerUtils.computeProperSize((float) x, CommonDrawerUtils.getDpi(dpi));
	}

}
