package org.bahmni.module.bahmnipsi.integrationTests;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.identifier.PatientUICIdentifier;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PatientUICIdentifierIT extends BaseModuleWebContextSensitiveTest {
    @Test
    public void shouldUpdateIdentifier() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        PatientUICIdentifier patientUICIdentifier = new PatientUICIdentifier();
        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOEYD130170M0", patient.getPatientIdentifier("UIC").getIdentifier());
    }
}
