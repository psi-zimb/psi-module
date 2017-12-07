package org.bahmni.module.bahmnipsi.integrationTests;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.identifier.PatientIdentifierSaveCommandImpl;
import org.junit.Test;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PatientPrepOiIdentifierIT extends BaseModuleWebContextSensitiveTest {
    @Autowired
    PatientIdentifierSaveCommandImpl patientIdentifierSaveCommand;

    @Test
    public void shouldUpdatePrepOiIdentifier() throws Exception {
        executeDataSet("PatientIdentifier.xml");
        String prepInitial = "PrEP Initial";
        String conceptName = "Reason for visit";
        String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(prepInitial, conceptName, patientUuid);

        patientIdentifierSaveCommand.update(bahmniEncounterTransaction);
    }
}
