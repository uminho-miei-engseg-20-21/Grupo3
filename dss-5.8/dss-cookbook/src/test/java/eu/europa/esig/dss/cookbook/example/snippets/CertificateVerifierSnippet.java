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
package eu.europa.esig.dss.cookbook.example.snippets;

import org.slf4j.event.Level;

import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.alert.LogOnStatusAlert;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

public class CertificateVerifierSnippet {

	public static void main(String[] args) {

		DataLoader dataLoader = null;
		CertificateSource adjunctCertSource = null;
		CertificateSource trustedCertSource = null;
		CRLSource crlSource = null;
		OCSPSource ocspSource = null;

		// tag::demo[]

		CertificateVerifier cv = new CommonCertificateVerifier();

		// This data loader is used to collect certificates from external resources
		// (AIA)
		cv.setDataLoader(dataLoader);

		// The adjunct certificate source is used to provide missing intermediate certificates
		// (not trusted certificates)
		cv.setAdjunctCertSources(adjunctCertSource);

		// The trusted certificate source is used to provide trusted certificates 
		// (the trust anchors where the certificate chain building should stop)
		cv.setTrustedCertSources(trustedCertSource);

		// The CRL Source to be used for external accesses (can be configured with a
		// cache,...)
		cv.setCrlSource(crlSource);

		// The OCSP Source to be used for external accesses (can be configured with a
		// cache,...)
		cv.setOcspSource(ocspSource);
		
		// Sets the default digest algorithm that will be used for digest calculation
		// of tokens used during the validation process. 
		// The values will be used in validation reports.
		// Default : DigestAlgorithm.SHA256
		cv.setDefaultDigestAlgorithm(DigestAlgorithm.SHA512);

		// Define the behavior to be followed by DSS in case of revocation checking for
		// certificates issued from an unsure source (DSS v5.4+)
		// Default : revocation check is disabled for unsure sources (security reasons)
		cv.setCheckRevocationForUntrustedChains(false);

		// DSS v5.4+ : The 3 below configurations concern the extension mode (LT/LTA
		// extension)

		// DSS throws an exception by default in case of missing revocation data
		// Default : ExceptionOnStatusAlert
		cv.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());

		// DSS logs a warning if a TSU certificate chain is not covered with a
		// revocation data (timestamp generation time > CRL/OCSP production time).
		// Default : LogOnStatusAlert
		cv.setAlertOnUncoveredPOE(new LogOnStatusAlert(Level.WARN));

		// DSS interrupts by default the extension process if a revoked certificate is
		// present
		// Default : ExceptionOnStatusAlert
		cv.setAlertOnRevokedCertificate(new ExceptionOnStatusAlert());

		// DSS stops the extension process if an invalid timestamp is met
		// Default : ExceptionOnStatusAlert
		cv.setAlertOnInvalidTimestamp(new ExceptionOnStatusAlert());
		
		// DSS v5.5+ : logs a warning message in case if there is no valid revocation
		// data
		// with thisUpdate time after the best signature time
		// Example: if a signature was extended to T level then the obtained revocation 
		// must have thisUpdate time after production time of the signature timestamp.
		// Default : LogOnStatusAlert
		cv.setAlertOnNoRevocationAfterBestSignatureTime(new LogOnStatusAlert(Level.ERROR));
		
		// 5.7 : The below methods have been moved to DocumentValidator /
		// CertificateValidator
//		cv.setIncludeCertificateRevocationValues(true);
//		cv.setIncludeCertificateRevocationValues(true);
//		cv.setIncludeTimestampTokenValues(true);

		// end::demo[]

	}

}
