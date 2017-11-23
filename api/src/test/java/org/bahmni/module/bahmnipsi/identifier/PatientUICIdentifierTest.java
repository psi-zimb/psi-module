package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;

import static junit.framework.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class PatientUICIdentifierTest {
    @Mock
    private ConceptService conceptService;

    @Mock
    private Concept concept;

    @Mock
    private ConceptName conceptName;

    @Mock
    private PatientIdentifierService patientIdentifierService;

    @Mock
    private SimpleDateFormat simpleDateFormat;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    PatientUICIdentifier patientUICIdentifier = new PatientUICIdentifier();

    @Test
    public void shouldReturnGeneratedUICIdentifier() throws Exception{
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";
        String withoutSuffix = "ENOERE130170M";
        int count = 0;

        Patient patient = PatientTestData.setUpPatientData();

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);
        when(patientIdentifierService.getCountOfPatients(withoutSuffix)).thenReturn(count);

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOERE130170M0", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldThrowExceptionWhenOneOfRequiredFieldsIsNull() {
        Patient patient = PatientTestData.setUpPatientData();
        patient.setGender(null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Required fields Patient family name and Mothers first name should not be null");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowExceptionWhenNamesHaveLessThanTwoChars() {
        Patient patient = PatientTestData.setUpPatientData();
        PersonAttribute motherAttribute = patient.getAttribute("Mother's name");
        motherAttribute.setValue("d");
        exception.expect(RuntimeException.class);
        exception.expectMessage("Patient family name, Mothers first name fields should have two characters at least");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");

        patientUICIdentifier.updateUICIdentifier(patient);
    }
}

