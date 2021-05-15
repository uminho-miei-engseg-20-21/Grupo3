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
package eu.europa.esig.dss.validation.process.bbb.xcv.rac;

import eu.europa.esig.dss.detailedreport.jaxb.XmlRAC;
import eu.europa.esig.dss.diagnostic.CertificateRevocationWrapper;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.RevocationWrapper;
import eu.europa.esig.dss.diagnostic.TokenProxy;
import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.enumerations.RevocationType;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.SubContext;
import eu.europa.esig.dss.policy.ValidationPolicy;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.validation.process.Chain;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;
import eu.europa.esig.dss.validation.process.bbb.cv.checks.SignatureIntactCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.checks.ProspectiveCertificateChainCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.rac.checks.RevocationAcceptanceCheckerResultCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.rac.checks.RevocationCertHashMatchCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.rac.checks.RevocationCertHashPresenceCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.rac.checks.RevocationConsistentCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.rac.checks.RevocationDataKnownCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.rac.checks.RevocationIssuerRevocationDataAvailableCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.rac.checks.SelfIssuedOCSPCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.rfc.checks.AcceptableRevocationDataAvailableCheck;
import eu.europa.esig.dss.validation.process.bbb.xcv.sub.checks.IdPkixOcspNoCheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Checks if the revocation is acceptable and can be used
 */
public class RevocationAcceptanceChecker extends Chain<XmlRAC> {

	/** The certificate in question */
	private final CertificateWrapper certificate;

	/** The revocation data */
	private final CertificateRevocationWrapper revocationData;

	/** Validation time */
	private final Date controlTime;

	/** Validation policy */
	private final ValidationPolicy policy;

	/** Internal list of processed tokens (avoids infinite loop) */
	private final List<String> validatedTokens;

	/**
	 * Default constructor
	 *
	 * @param i18nProvider {@link I18nProvider}
	 * @param certificate {@link CertificateWrapper}
	 * @param revocationData {@link CertificateRevocationWrapper}
	 * @param controlTime {@link Date}
	 * @param policy {@link ValidationPolicy}
	 */
	public RevocationAcceptanceChecker(I18nProvider i18nProvider, CertificateWrapper certificate,
									   CertificateRevocationWrapper revocationData, Date controlTime,
									   ValidationPolicy policy) {
		this(i18nProvider, certificate, revocationData, controlTime, policy, new ArrayList<String>());
		result.setId(revocationData.getId());
		result.setRevocationProductionDate(revocationData.getProductionDate());
	}
	
	@Override
	protected MessageTag getTitle() {
		return MessageTag.RAC;
	}
	
	private RevocationAcceptanceChecker(I18nProvider i18nProvider, CertificateWrapper certificate, CertificateRevocationWrapper revocationData,
			Date controlTime, ValidationPolicy policy, List<String> validatedTokens) {
		super(i18nProvider, new XmlRAC());
		this.certificate = certificate;
		this.revocationData = revocationData;
		this.controlTime = controlTime;
		this.policy = policy;
		this.validatedTokens = validatedTokens;
	}

	@Override
	protected void initChain() {

		ChainItem<XmlRAC> item = firstItem = revocationDataKnown();

		/*
		 * certHash extension can be present in an OCSP Response. If present, a digest match indicates the OCSP
		 * responder knows the certificate as we have it, and so also its revocation state
		 */
		if (RevocationType.OCSP.equals(revocationData.getRevocationType())) {

			item = item.setNextItem(revocationCertHashPresent());
			
			if (revocationData.isCertHashExtensionPresent()) {
				item = item.setNextItem(revocationCertHashMatch());
			}

			item = item.setNextItem(selfIssuedOcsp());

		}

		item = item.setNextItem(revocationDataConsistent());
		
		item = item.setNextItem(revocationDataIntact());
		
		item = item.setNextItem(prospectiveCertificateChain(revocationData.getSigningCertificate()));
		
		for (CertificateWrapper revocationCertificate : revocationData.getCertificateChain()) {
			
			if (revocationCertificate.isTrusted()) {
				break;
			}
			
			if (isTokenValidated(revocationCertificate)) {
				continue;
			}
			
			item = item.setNextItem(certificateIntact(revocationCertificate));
			
			if (revocationCertificate.isIdPkixOcspNoCheck()) {
				item = item.setNextItem(idPkixOcspNoCheck(revocationCertificate));
			}
			
			if (ValidationProcessUtils.isRevocationCheckRequired(revocationCertificate, controlTime)) {

				SubContext subContext = revocationData.getSigningCertificate().getId().equals(revocationCertificate.getId()) ? 
						SubContext.SIGNING_CERT : SubContext.CA_CERTIFICATE;
				
				item = item.setNextItem(revocationDataPresentForRevocationChain(revocationCertificate, subContext));
				
				CertificateRevocationWrapper latestRevocationData = null;
				for (CertificateRevocationWrapper revocationWrapper : revocationCertificate.getCertificateRevocationData()) {
					
					if (isTokenValidated(revocationWrapper)) {
						if (latestRevocationData == null || 
								revocationWrapper.getProductionDate().before(latestRevocationData.getProductionDate())) {
							latestRevocationData = revocationWrapper;
						}
						continue;
					}
					
					RevocationAcceptanceChecker rac = revocationAcceptanceChecker(revocationCertificate, revocationWrapper);
					XmlRAC racResult = rac.execute();
					
					item = item.setNextItem(revocationAcceptanceResultCheck(racResult));
					
					if (isValid(racResult) && (latestRevocationData == null || 
							revocationWrapper.getProductionDate().after(latestRevocationData.getProductionDate()))) {
						latestRevocationData = revocationWrapper;
					}
					
				}
				
				item = item.setNextItem(acceptableRevocationDataAvailable(latestRevocationData, revocationCertificate, subContext));
				
			}
			
		}
		
	}

	private ChainItem<XmlRAC> revocationDataKnown() {
		return new RevocationDataKnownCheck(i18nProvider, result, revocationData, policy.getUnknownStatusConstraint());
	}

	private ChainItem<XmlRAC> revocationCertHashPresent() {
		LevelConstraint constraint = policy.getOCSPResponseCertHashPresentConstraint();
		return new RevocationCertHashPresenceCheck(i18nProvider, result, revocationData, constraint);
	}

	private ChainItem<XmlRAC> revocationCertHashMatch() {
		LevelConstraint constraint = policy.getOCSPResponseCertHashMatchConstraint();
		return new RevocationCertHashMatchCheck(i18nProvider, result, revocationData, constraint);
	}

	private ChainItem<XmlRAC> selfIssuedOcsp() {
		LevelConstraint constraint = policy.getSelfIssuedOCSPConstraint();
		return new SelfIssuedOCSPCheck(i18nProvider, result, certificate, revocationData, constraint);
	}

	private ChainItem<XmlRAC> revocationDataConsistent() {
		return new RevocationConsistentCheck<>(i18nProvider, result, certificate, revocationData, getFailLevelConstraint());
	}
	
	private ChainItem<XmlRAC> revocationDataIntact() {
		LevelConstraint constraint = policy.getSignatureIntactConstraint(Context.REVOCATION);
		return new SignatureIntactCheck<>(i18nProvider, result, revocationData, Context.REVOCATION, constraint);
	}

	private ChainItem<XmlRAC> prospectiveCertificateChain(CertificateWrapper signingCertificate) {
		LevelConstraint constraint = policy.getProspectiveCertificateChainConstraint(Context.REVOCATION);
		return new ProspectiveCertificateChainCheck<>(i18nProvider, result, signingCertificate, Context.REVOCATION, constraint);
	}
	
	private boolean isTokenValidated(TokenProxy token) {
		boolean validated = validatedTokens.contains(token.getId());
		validatedTokens.add(token.getId());
		return validated;
	}
	
	private ChainItem<XmlRAC> certificateIntact(CertificateWrapper certificate) {
		LevelConstraint constraint = policy.getSignatureIntactConstraint(Context.CERTIFICATE);
		return new SignatureIntactCheck<>(i18nProvider, result, certificate, Context.CERTIFICATE, constraint);
	}

	private ChainItem<XmlRAC> idPkixOcspNoCheck(CertificateWrapper certificateWrapper) {
		return new IdPkixOcspNoCheck<>(i18nProvider, result, certificateWrapper, controlTime, getWarnLevelConstraint());
	}
	
	private ChainItem<XmlRAC> revocationDataPresentForRevocationChain(CertificateWrapper certificate, SubContext subContext) {
		LevelConstraint constraint = policy.getRevocationDataAvailableConstraint(Context.REVOCATION, subContext);
		return new RevocationIssuerRevocationDataAvailableCheck(i18nProvider, result, certificate, constraint);
	}
	
	private RevocationAcceptanceChecker revocationAcceptanceChecker(CertificateWrapper certificateWrapper, CertificateRevocationWrapper revocationWrapper) {
		return new RevocationAcceptanceChecker(i18nProvider, certificateWrapper, revocationWrapper, controlTime, policy, validatedTokens);
	}
	
	private ChainItem<XmlRAC> revocationAcceptanceResultCheck(XmlRAC racResult) {
		return new RevocationAcceptanceCheckerResultCheck<>(i18nProvider, result, racResult, getWarnLevelConstraint());
	}

	private ChainItem<XmlRAC> acceptableRevocationDataAvailable(RevocationWrapper revocationData, 
			CertificateWrapper certificateWrapper, SubContext subContext) {
		LevelConstraint constraint = policy.getRevocationDataAvailableConstraint(Context.REVOCATION, subContext);
		return new AcceptableRevocationDataAvailableCheck<>(i18nProvider, result, certificateWrapper, revocationData, constraint);
	}

}
