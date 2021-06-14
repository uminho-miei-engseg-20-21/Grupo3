package eu.europa.esig.dss.web.service;

import org.hsqldb.lib.StringInputStream;
import org.springframework.stereotype.Component;
import wsdlservice.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class CMDService {
    private final static byte[] APPLICATION_ID = "b826359c-06f8-425e-8ec3-50a97a418916".getBytes(StandardCharsets.UTF_8);

    private final CCMovelDigitalSignature service;
    private final CCMovelSignature connector;

    public CMDService() {
//        this.service = new CCMovelDigitalSignature(getClass().getResource("ama.wsdl"));
        this.service = new CCMovelDigitalSignature(getClass().getResource("CCMovelDigitalSignature.svc"));

        this.connector = service.getBasicHttpBindingCCMovelSignature();
    }

    public List<String> getCertificatesOf(String userId) throws CertificateException, IOException {
        final String certificates = connector.getCertificate(APPLICATION_ID, userId);

        BufferedInputStream contentPemFile = new BufferedInputStream(new StringInputStream(certificates));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        List<String> certChain = new ArrayList<>();
        while(contentPemFile.available() > 0) {
            Certificate certificate = cf.generateCertificate(contentPemFile);

            certChain.add(Base64.getEncoder().encodeToString(certificate.getEncoded()));
        }

        return certChain;
    }

    public String sign(String docName, byte[] doc, String userId, String userPin) {
        SignRequest request = new SignRequest();

        ObjectFactory factory = new ObjectFactory();

        request.setApplicationId(APPLICATION_ID);
        request.setDocName(factory.createSignRequestDocName(docName));
//        request.setDocName(docName);
        request.setHash(doc);
        request.setUserId(userId);
        request.setPin(userPin);

        SignStatus status = connector.ccMovelSign(request);

        return status.getProcessId();
    }

    public String validateOtp(String processId, String userOtp) {
        SignResponse response = connector.validateOtp(userOtp, processId, APPLICATION_ID);

        byte[] signatureBytes = response.getSignature();

        if(signatureBytes == null) {
            return null;
        }

        return Base64.getEncoder().encodeToString(signatureBytes);
    }
}
