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
package eu.europa.esig.dss.tsl.function.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.tsl.OtherTSLPointer;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.trustedlist.jaxb.tsl.DigitalIdentityListType;
import eu.europa.esig.trustedlist.jaxb.tsl.OtherTSLPointerType;
import eu.europa.esig.trustedlist.jaxb.tsl.ServiceDigitalIdentityListType;

public class OtherTSLPointerConverter implements Function<OtherTSLPointerType, OtherTSLPointer> {

	@Override
	public OtherTSLPointer apply(OtherTSLPointerType original) {
		return new OtherTSLPointer(original.getTSLLocation(), Collections.unmodifiableList(getCertificates(original.getServiceDigitalIdentities())));
	}

	private List<CertificateToken> getCertificates(ServiceDigitalIdentityListType serviceDigitalIdentities) {
		List<CertificateToken> certificates = new ArrayList<>();
		if (serviceDigitalIdentities != null && Utils.isCollectionNotEmpty(serviceDigitalIdentities.getServiceDigitalIdentity())) {
			DigitalIdentityListTypeConverter converter = new DigitalIdentityListTypeConverter();
			for (DigitalIdentityListType digitalIdentityList : serviceDigitalIdentities.getServiceDigitalIdentity()) {
				certificates.addAll(converter.apply(digitalIdentityList));
			}
		}
		return certificates;
	}

}
