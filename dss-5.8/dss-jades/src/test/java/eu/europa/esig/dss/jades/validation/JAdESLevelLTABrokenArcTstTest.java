package eu.europa.esig.dss.jades.validation;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.ArchiveTimestampType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JAdESLevelLTABrokenArcTstTest extends AbstractJAdESTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/validation/jades-lta-broken-arcTst.json");
    }

    @Override
    protected void checkSignatureLevel(DiagnosticData diagnosticData) {
        assertEquals(SignatureLevel.JAdES_BASELINE_LTA, diagnosticData.getSignatureFormat(diagnosticData.getFirstSignatureId()));
    }

    @Override
    protected void checkTimestamps(DiagnosticData diagnosticData) {
        List<TimestampWrapper> timestampList = diagnosticData.getTimestampList();
        assertEquals(2, timestampList.size());

        boolean sigTstFound = false;
        boolean archiveTstFound = false;
        for (TimestampWrapper timestamp : timestampList) {
            assertTrue(timestamp.isMessageImprintDataFound());

            if (TimestampType.SIGNATURE_TIMESTAMP.equals(timestamp.getType())) {
                assertTrue(timestamp.isMessageImprintDataIntact());
                sigTstFound = true;

            } else if (TimestampType.ARCHIVE_TIMESTAMP.equals(timestamp.getType())) {
                assertEquals(ArchiveTimestampType.JAdES, timestamp.getArchiveTimestampType());
                assertFalse(timestamp.isMessageImprintDataIntact());
                archiveTstFound = true;
            }
        }
        assertTrue(sigTstFound);
        assertTrue(archiveTstFound);
    }

}
