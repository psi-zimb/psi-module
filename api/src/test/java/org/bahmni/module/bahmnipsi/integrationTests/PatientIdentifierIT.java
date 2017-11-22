package org.bahmni.module.bahmnipsi.integrationTests;

import org.bahmni.module.bahmnipsi.identifier.PatientUICIdentifier;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PatientIdentifierIT extends BaseModuleWebContextSensitiveTest {
    @Test
    public void shouldUpdateIdentifier() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = Context.getPatientService().getPatient(2);
        PatientUICIdentifier patientUICIdentifier = new PatientUICIdentifier();
        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("REOOYD221117M0", patient.getPatientIdentifier("UIC").getIdentifier());
    }
}
