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

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.alert.LogOnStatusAlert;
import eu.europa.esig.dss.alert.StatusAlert;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.x509.revocation.crl.CRL;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.client.http.NativeHTTPDataLoader;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.ListCertificateSource;
import eu.europa.esig.dss.spi.x509.revocation.RevocationSource;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;

/**
 * This class provides the different sources used to verify the status of a certificate using the trust model. There are
 * four different types of sources to be defined:<br>
 * - Trusted certificates source;<br>
 * - Adjunct certificates source (not trusted);<br>
 * - OCSP source;<br>
 * - CRL source.<br>
 *
 * The {@code DataLoader} should be provided to give access to the certificates through AIA.
 *
 */
public class CommonCertificateVerifier implements CertificateVerifier {

	private static final Logger LOG = LoggerFactory.getLogger(CommonCertificateVerifier.class);
	
	/**
	 * This field contains the reference to multiple trusted certificate sources. These sources are fixed, it means that the same
	 * sources are used for different validations.
	 */
	private ListCertificateSource trustedCertSources = new ListCertificateSource();

	/**
	 * This field contains the reference to arbitrary certificate source, can contain a trust store, 
	 * or the any intermediate certificates.
	 */
	private ListCertificateSource adjunctCertSources = new ListCertificateSource();

	/**
	 * This field contains the reference to the {@code OCSPSource}.
	 */
	private RevocationSource<OCSP> ocspSource;

	/**
	 * This field contains the reference to the {@code CRLSource}.
	 */
	private RevocationSource<CRL> crlSource;

	/**
	 * The data loader used to access AIA certificate source.
	 */
	private DataLoader dataLoader;

	/**
	 * This variable contains the {@code ListRevocationSource} extracted from the
	 * signatures to validate.
	 */
	private ListRevocationSource<CRL> signatureCRLSource;

	/**
	 * This variable contains the {@code ListRevocationSource} extracted from the
	 * signatures to validate.
	 */
	private ListRevocationSource<OCSP> signatureOCSPSource;
	
	/**
	 * This variable contains the {@code ListCertificateSource} extracted from the
	 * signatures to validate.
	 */
	private ListCertificateSource signatureCertificateSource;

	/**
	 * This variable set the default Digest Algorithm what will be used for calculation
	 * of digests for validation tokens and signed data
	 * Default: SHA256
	 */
	private DigestAlgorithm defaultDigestAlgorithm = DigestAlgorithm.SHA256;

	/**
	 * This variable set the behavior to follow in case of invalid timestamp
	 * (augmentation process).
	 * 
	 * Default : ExceptionOnStatusAlert - throw the exception
	 */
	private StatusAlert alertOnInvalidTimestamp = new ExceptionOnStatusAlert();

	/**
	 * This variable set the behavior to follow in case of missing revocation data
	 * (augmentation process).
	 * 
	 * Default : ExceptionOnStatusAlert - throw the exception
	 */
	private StatusAlert alertOnMissingRevocationData = new ExceptionOnStatusAlert();

	/**
	 * This variable set the behavior to follow in case of revoked certificate
	 * (augmentation process).
	 * 
	 * Default : ExceptionOnStatusAlert - throw the exception
	 */
	private StatusAlert alertOnRevokedCertificate = new ExceptionOnStatusAlert();

	/**
	 * This variable set the behavior to follow in case of no revocation data issued
	 * after the bestSignatureTime (augmentation process).
	 * 
	 * Default : LogOnStatusAlert - log a warning message
	 */
	private StatusAlert alertOnNoRevocationAfterBestSignatureTime = new LogOnStatusAlert(Level.WARN);

	/**
	 * This variable set the behavior to follow in case of missing revocation data
	 * for a POE.
	 * 
	 * Default : LogOnStatusAlert - log a warning message
	 */
	private StatusAlert alertOnUncoveredPOE = new LogOnStatusAlert(Level.WARN);

	/**
	 * This variable set the behavior to follow for revocation retrieving in case of
	 * untrusted certificate chains.
	 * 
	 * Default : false (revocation are not checked in case of certificates issued
	 * from an unsure source)
	 */
	private boolean checkRevocationForUntrustedChains = false;

	/**
	 * The default constructor. The {@code DataLoader} is created to allow the
	 * retrieval of certificates through AIA.
	 */
	public CommonCertificateVerifier() {
		this(false);
	}

	/**
	 * This constructor allows to create {@code CommonCertificateVerifier} without {@code DataLoader}. It means that
	 * only a profile -B signatures can be created.
	 *
	 * @param simpleCreationOnly
	 *            if true the {@code CommonCertificateVerifier} will not contain {@code DataLoader}.
	 */
	public CommonCertificateVerifier(final boolean simpleCreationOnly) {
		LOG.info("+ New CommonCertificateVerifier created.");
		if (!simpleCreationOnly) {
			dataLoader = new NativeHTTPDataLoader();
		}
	}

	/**
	 * The constructor with key parameters.
	 *
	 * @param trustedCertSources
	 *            the reference to the trusted certificate sources.
	 * @param crlSource
	 *            contains the reference to the {@code OCSPSource}.
	 * @param ocspSource
	 *            contains the reference to the {@code CRLSource}.
	 * @param dataLoader
	 *            contains the reference to a data loader used to access AIA certificate source.
	 */
	public CommonCertificateVerifier(final List<CertificateSource> trustedCertSources, final CRLSource crlSource, final OCSPSource ocspSource,
			final DataLoader dataLoader) {

		LOG.info("+ New CommonCertificateVerifier created with parameters.");
		this.trustedCertSources = new ListCertificateSource(trustedCertSources);
		this.crlSource = crlSource;
		this.ocspSource = ocspSource;
		this.dataLoader = dataLoader;
		if (dataLoader == null) {
			LOG.warn("DataLoader is null. It's required to access AIA certificate source");
		}
	}

	@Override
	public ListCertificateSource getTrustedCertSources() {
		return trustedCertSources;
	}

	@Override
	public RevocationSource<OCSP> getOcspSource() {
		return ocspSource;
	}

	@Override
	public RevocationSource<CRL> getCrlSource() {
		return crlSource;
	}

	@Override
	public void setCrlSource(final RevocationSource<CRL> crlSource) {
		this.crlSource = crlSource;
	}

	@Override
	public void setOcspSource(final RevocationSource<OCSP> ocspSource) {
		this.ocspSource = ocspSource;
	}

	@Override
	public void setTrustedCertSources(final CertificateSource... certSources) {
		this.trustedCertSources = new ListCertificateSource();
		addTrustedCertSources(certSources);
	}
	
	@Override
	public void addTrustedCertSources(final CertificateSource... certSources) {
		for (CertificateSource certificateSource : certSources) {
			if (certificateSource.getCertificateSourceType().isTrusted()) {
				this.trustedCertSources.add(certificateSource);
			} else {
	            throw new DSSException(String.format("The certificateSource with type [%s] is not allowed in the trustedCertSources. Please, "
	                    + "use CertificateSource with a type TRUSTED_STORE or TRUSTED_LIST.", certificateSource.getCertificateSourceType()));
			}
		}
	}
	
	@Override
	public void setTrustedCertSources(ListCertificateSource trustedListCertificateSource) {
		if (trustedListCertificateSource == null) {
			this.trustedCertSources = new ListCertificateSource();
		} else if (trustedListCertificateSource.areAllCertSourcesTrusted()) {
			this.trustedCertSources = trustedListCertificateSource;
		} else {
            throw new DSSException(String.format("The trusted ListCertificateSource must contain only trusted sources "
                    + "with a type TRUSTED_STORE or TRUSTED_LIST."));
		}
	}

	@Override
	public ListCertificateSource getAdjunctCertSources() {
		return adjunctCertSources;
	}

	@Override
	public void setAdjunctCertSources(final CertificateSource... certSources) {
		this.adjunctCertSources = new ListCertificateSource();
		addAdjunctCertSources(certSources);
	}

	@Override
	public void addAdjunctCertSources(final CertificateSource... certSources) {
		for (CertificateSource certificateSource : certSources) {
			assertNotTrusted(certificateSource);
			this.adjunctCertSources.add(certificateSource);
		}
	}
	
	@Override
	public void setAdjunctCertSources(ListCertificateSource adjunctListCertificateSource) {
		if (adjunctListCertificateSource == null) {
			adjunctListCertificateSource = new ListCertificateSource();
		}
		for (CertificateSource certificateSource : adjunctListCertificateSource.getSources()) {
			assertNotTrusted(certificateSource);
		}
		this.adjunctCertSources = adjunctListCertificateSource;
	}
	
	private void assertNotTrusted(final CertificateSource adjunctCertificateSource) {
		if (adjunctCertificateSource.getCertificateSourceType().isTrusted()) {
			LOG.warn("Adjunct certificate sources shouldn't be trusted. An adjunct certificate source contains missing intermediate certificates");
		}
	}

	@Override
	public DataLoader getDataLoader() {
		return dataLoader;
	}

	@Override
	public void setDataLoader(final DataLoader dataLoader) {
		this.dataLoader = dataLoader;
	}

	@Override
	public ListRevocationSource<CRL> getSignatureCRLSource() {
		return signatureCRLSource;
	}

	@Override
	public void setSignatureCRLSource(final ListRevocationSource<CRL> signatureCRLSource) {
		this.signatureCRLSource = signatureCRLSource;
	}

	@Override
	public ListRevocationSource<OCSP> getSignatureOCSPSource() {
		return signatureOCSPSource;
	}

	@Override
	public void setSignatureOCSPSource(final ListRevocationSource<OCSP> signatureOCSPSource) {
		this.signatureOCSPSource = signatureOCSPSource;
	}

	@Override
	public ListCertificateSource getSignatureCertificateSource() {
		return signatureCertificateSource;
	}

	@Override
	public void setSignatureCertificateSource(ListCertificateSource signatureCertificateSource) {
		this.signatureCertificateSource = signatureCertificateSource;
	}

	@Override
	public StatusAlert getAlertOnInvalidTimestamp() {
		return alertOnInvalidTimestamp;
	}

	@Override
	public void setAlertOnInvalidTimestamp(StatusAlert alertOnInvalidTimestamp) {
		Objects.requireNonNull(alertOnInvalidTimestamp);
		this.alertOnInvalidTimestamp = alertOnInvalidTimestamp;
	}

	@Override
	public StatusAlert getAlertOnMissingRevocationData() {
		return alertOnMissingRevocationData;
	}

	@Override
	public void setAlertOnMissingRevocationData(StatusAlert alertOnMissingRevocationData) {
		Objects.requireNonNull(alertOnMissingRevocationData);
		this.alertOnMissingRevocationData = alertOnMissingRevocationData;
	}

	@Override
	public StatusAlert getAlertOnUncoveredPOE() {
		return alertOnUncoveredPOE;
	}

	@Override
	public void setAlertOnUncoveredPOE(StatusAlert alertOnUncoveredPOE) {
		Objects.requireNonNull(alertOnUncoveredPOE);
		this.alertOnUncoveredPOE = alertOnUncoveredPOE;
	}

	@Override
	public StatusAlert getAlertOnRevokedCertificate() {
		return alertOnRevokedCertificate;
	}

	@Override
	public void setAlertOnRevokedCertificate(StatusAlert alertOnRevokedCertificate) {
		Objects.requireNonNull(alertOnRevokedCertificate);
		this.alertOnRevokedCertificate = alertOnRevokedCertificate;
	}

	@Override
	public StatusAlert getAlertOnNoRevocationAfterBestSignatureTime() {
		return alertOnNoRevocationAfterBestSignatureTime;
	}

	@Override
	public void setAlertOnNoRevocationAfterBestSignatureTime(StatusAlert alertOnNoRevocationAfterBestSignatureTime) {
		Objects.requireNonNull(alertOnNoRevocationAfterBestSignatureTime);
		this.alertOnNoRevocationAfterBestSignatureTime = alertOnNoRevocationAfterBestSignatureTime;
	}

	@Override
	public boolean isCheckRevocationForUntrustedChains() {
		return checkRevocationForUntrustedChains;
	}

	@Override
	public void setCheckRevocationForUntrustedChains(boolean checkRevocationForUntrustedChains) {
		this.checkRevocationForUntrustedChains = checkRevocationForUntrustedChains;
	}

	@Override
	public void setDefaultDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
		this.defaultDigestAlgorithm = digestAlgorithm;
	}
	
	@Override
	public DigestAlgorithm getDefaultDigestAlgorithm() {
		return defaultDigestAlgorithm;
	}

}
