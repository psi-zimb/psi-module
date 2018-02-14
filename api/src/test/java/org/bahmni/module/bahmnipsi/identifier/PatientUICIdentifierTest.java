package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.*;
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

    private String areYouTwin = "Are you a twin?";
    private String areYouFirstBorn = "If yes, are you the firstborn?";

    PatientUICIdentifier patientUICIdentifier = new PatientUICIdentifier();

    @Test
    public void shouldReturnGeneratedUICIdentifier() throws Exception{
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";

        Patient patient = PatientTestData.setUpPatientData();

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOERE130170M", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldThrowExceptionWhenGenderIsNull() {
        Patient patient = PatientTestData.setUpPatientData();
        patient.setGender(null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Gender field should not be empty");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowExceptionWhenPatientLastNameIsNull() {
        Patient patient = PatientTestData.setUpPatientData();
        PersonName personName = patient.getPersonName();
        personName.setFamilyName(null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Patient Last Name field should not be empty");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowExceptionWhenMothersFirstNameIsNull() {
        Patient patient = PatientTestData.setUpPatientData();
        PersonAttribute attribute = patient.getAttribute("Mother's name");
        attribute.setValue(null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Mother's First Name field should not be empty");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");

        patientUICIdentifier.updateUICIdentifier(patient);
     }

    @Test
    public void shouldThrowExceptionWhenDistrictOfBirthIsNull() {
        Patient patient = PatientTestData.setUpPatientData();
        PersonAttribute districtOfBirth = patient.getAttribute("District of Birth");
        patient.removeAttribute(districtOfBirth);

        exception.expect(RuntimeException.class);
        exception.expectMessage("District of Birth field should not be empty");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowExceptionWhenBirthDateIsNull() {
        Patient patient = PatientTestData.setUpPatientData();
        patient.setBirthdate(null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Date of Birth field should not be empty");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowExceptionWhenMothersFirstNameHaveLessThanTwoChars() {
        Patient patient = PatientTestData.setUpPatientData();
        PersonAttribute motherAttribute = patient.getAttribute("Mother's name");
        motherAttribute.setValue("d");
        exception.expect(RuntimeException.class);
        exception.expectMessage("Mother's First Name field should have two characters at least");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowExceptionWhenPatientLastNameHaveLessThanTwoChars() {
        Patient patient = PatientTestData.setUpPatientData();
        patient.getPersonName().setFamilyName("f");

        exception.expect(RuntimeException.class);
        exception.expectMessage("Patient Last Name field should have two characters at least");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("Harare");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowExceptionWhenDisctrictOfBirthHaveLessThanTwoChars() {
        Patient patient = PatientTestData.setUpPatientData();

        exception.expect(RuntimeException.class);
        exception.expectMessage("District of Birth field should have two characters at least");

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("H");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldHaveSuffixAsT1IfTheTwinIsFirstBorn() throws Exception {
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";

        Patient patient = PatientTestData.setUpPatientData();
        PersonAttributeType areYouTwinAttributeType = new PersonAttributeType();
        PersonAttributeType firstBornAttributeType = new PersonAttributeType();
        areYouTwinAttributeType.setName(areYouTwin);
        firstBornAttributeType.setName(areYouFirstBorn);
        PersonAttribute areYouTwinAttribute = new PersonAttribute(areYouTwinAttributeType, "2146");
        PersonAttribute firstBornAttribute = new PersonAttribute(firstBornAttributeType, "2146");
        patient.addAttribute(areYouTwinAttribute);
        patient.addAttribute(firstBornAttribute);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(conceptService.getConcept("2146")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName())
                .thenReturn("Harare")
                .thenReturn("Yes")
                .thenReturn("Yes");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOERE130170MT1", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldHaveSuffixAsT2IfTheTwinIsNotFirstBorn() throws Exception {
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";

        Patient patient = PatientTestData.setUpPatientData();
        PersonAttributeType areYouTwinAttributeType = new PersonAttributeType();
        PersonAttributeType firstBornAttributeType = new PersonAttributeType();
        areYouTwinAttributeType.setName(areYouTwin);
        firstBornAttributeType.setName(areYouFirstBorn);
        PersonAttribute areYouTwinAttribute = new PersonAttribute(areYouTwinAttributeType, "2146");
        PersonAttribute firstBornAttribute = new PersonAttribute(firstBornAttributeType, "2147");
        patient.addAttribute(areYouTwinAttribute);
        patient.addAttribute(firstBornAttribute);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(conceptService.getConcept("2146")).thenReturn(concept);
        when(conceptService.getConcept("2147")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName())
                .thenReturn("Harare")
                .thenReturn("Yes")
                .thenReturn("No");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOERE130170MT2", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldThrowErrorIfThePatientIsTwinAndIsFirstBornNull() throws Exception {
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";

        Patient patient = PatientTestData.setUpPatientData();
        PersonAttributeType areYouTwinAttributeType = new PersonAttributeType();
        areYouTwinAttributeType.setName(areYouTwin);
        PersonAttribute areYouTwinAttribute = new PersonAttribute(areYouTwinAttributeType, "2146");
        patient.addAttribute(areYouTwinAttribute);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(conceptService.getConcept("2146")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName())
                .thenReturn("Harare")
                .thenReturn("Yes");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldNotHaveTSuffixIfThePatientIsNotATwinAndFirstBornIsNull() throws Exception {
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";

        Patient patient = PatientTestData.setUpPatientData();
        PersonAttributeType areYouTwinAttributeType = new PersonAttributeType();
        areYouTwinAttributeType.setName(areYouTwin);
        PersonAttribute areYouTwinAttribute = new PersonAttribute(areYouTwinAttributeType, "2147");
        patient.addAttribute(areYouTwinAttribute);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(conceptService.getConcept("2147")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName())
                .thenReturn("Harare")
                .thenReturn("No");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOERE130170M", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldThrowErrorIfThePatientIsNotATwinAndFirstBornIsYes() throws Exception {
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";

        Patient patient = PatientTestData.setUpPatientData();
        PersonAttributeType areYouTwinAttributeType = new PersonAttributeType();
        PersonAttributeType firstBornAttributeType = new PersonAttributeType();
        areYouTwinAttributeType.setName(areYouTwin);
        firstBornAttributeType.setName(areYouFirstBorn);
        PersonAttribute areYouTwinAttribute = new PersonAttribute(areYouTwinAttributeType, "2147");
        PersonAttribute firstBornAttribute = new PersonAttribute(firstBornAttributeType, "2146");
        patient.addAttribute(areYouTwinAttribute);
        patient.addAttribute(firstBornAttribute);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(conceptService.getConcept("2146")).thenReturn(concept);
        when(conceptService.getConcept("2147")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName())
                .thenReturn("Harare")
                .thenReturn("No")
                .thenReturn("Yes");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowErrorIfThePatientIsNotATwinAndFirstBornIsNo() throws Exception {
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";

        Patient patient = PatientTestData.setUpPatientData();
        PersonAttributeType areYouTwinAttributeType = new PersonAttributeType();
        PersonAttributeType firstBornAttributeType = new PersonAttributeType();
        areYouTwinAttributeType.setName(areYouTwin);
        firstBornAttributeType.setName(areYouFirstBorn);
        PersonAttribute areYouTwinAttribute = new PersonAttribute(areYouTwinAttributeType, "2147");
        PersonAttribute firstBornAttribute = new PersonAttribute(firstBornAttributeType, "2147");
        patient.addAttribute(areYouTwinAttribute);
        patient.addAttribute(firstBornAttribute);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(conceptService.getConcept("2146")).thenReturn(concept);
        when(conceptService.getConcept("2147")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName())
                .thenReturn("Harare")
                .thenReturn("No")
                .thenReturn("No");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowErrorIfThePatientTwinValueIsNullAndFirstBornIsYes() throws Exception {
        String dateFormat = "ddMMyy";
        String formattedDate = "130217";

        Patient patient = PatientTestData.setUpPatientData();
        PersonAttributeType firstBornAttributeType = new PersonAttributeType();
        firstBornAttributeType.setName(areYouFirstBorn);
        PersonAttribute firstBornAttribute = new PersonAttribute(firstBornAttributeType, "2146");
        patient.addAttribute(firstBornAttribute);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(conceptService.getConcept("2146")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName())
                .thenReturn("Harare")
                .thenReturn("Yes");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowErrorIfThePatientTwinValueIsNullAndFirstBornIsNo() throws Exception {
        String dateFormat = "ddMMyy";
        String formattedDate = "130117";

        Patient patient = PatientTestData.setUpPatientData();
        PersonAttributeType firstBornAttributeType = new PersonAttributeType();
        firstBornAttributeType.setName(areYouFirstBorn);
        PersonAttribute firstBornAttribute = new PersonAttribute(firstBornAttributeType, "2147");
        patient.addAttribute(firstBornAttribute);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(SimpleDateFormat.class).withArguments(dateFormat).thenReturn(simpleDateFormat);

        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConcept("510")).thenReturn(concept);
        when(conceptService.getConcept("2147")).thenReturn(concept);
        when(concept.getName()).thenReturn(conceptName);
        when(conceptName.getName())
                .thenReturn("Harare")
                .thenReturn("No");
        when(simpleDateFormat.format(patient.getBirthdate())).thenReturn(formattedDate);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }
}

