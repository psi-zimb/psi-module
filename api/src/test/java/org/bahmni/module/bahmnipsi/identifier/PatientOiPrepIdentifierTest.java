package org.bahmni.module.bahmnipsi.identifier;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnipsi.PatientTestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.webservices.services.IdentifierSourceServiceWrapper;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.Year;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Long.class, Context.class, Year.class, StringUtils.class })
public class PatientOiPrepIdentifierTest {
    @Mock
    private PersonService personService;

    @Mock
    private Person person;

    @Mock
    private PersonAddress personAddress;

    @Mock
    private Year yearObj;

    @Mock
    private IdentifierSourceServiceWrapper identifierSourceServiceWrapper;

    @Mock
    private PatientService patientService;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private String identifierSource = "Prep/Oi Identifier";
    private String identifierType = "PREP/OI Identifier";
    private String patientUuid = "32df-gdffd-343ghh";
    private final String initialArt = "Initial ART service";
    private final String prepInitial = "PrEP Initial";
    private static final String defaultIdentifier = "Not Assigned";
    Patient patient;

    private PatientOiPrepIdentifier patientOiPrepIdentifier = new PatientOiPrepIdentifier();

    @Before
    public void setUp() {
        patientOiPrepIdentifier.setIdentifierSourceServiceWrapper(identifierSourceServiceWrapper);
    }


    @Test
    public void shouldDecreaseIdentifierByOne() throws Exception {
        String nextSeqValue = "3456";
        long seqValueAsLong = 3456;

        PowerMockito.mockStatic(Long.class);
        when(identifierSourceServiceWrapper.getSequenceValue(identifierSource)).thenReturn(nextSeqValue);

        patientOiPrepIdentifier.decreaseIdentifierNextValueByOne();

        verify(identifierSourceServiceWrapper, times(1)).saveSequenceValue(seqValueAsLong - 1, identifierSource);
        verify(identifierSourceServiceWrapper, times(1)).getSequenceValue(identifierSource);
    }

    @Test
    public void shouldSetPatientOiPrepIdentifierToDefaultValue() {
        Patient patient = PatientTestData.setOiPrepIdentifierToPatient();

        patientOiPrepIdentifier.setIdentifierToDefaultValue(patient);

        String expected = "Not Assigned";
        Assert.assertEquals(expected, patient.getPatientIdentifier(identifierType).getIdentifier());
    }

    @Test
    public void shouldHaveAffixAsAWhenVisitIsInitialArt() throws Exception {
        String province = "ward[22]";
        String affix = "A";

        patient = PatientTestData.setOiPrepIdentifierToPatient(defaultIdentifier);
        setUpMocks(patient);
        when(personAddress.getStateProvince()).thenReturn(province);
        when(StringUtils.substringBetween(province, "[", "]")).thenReturn("22");
        String expectedIdentifier = "22-OA-12-2017-A-01234";

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, affix, initialArt);

        Assert.assertEquals(expectedIdentifier, patient.getPatientIdentifier(identifierType).getIdentifier());
    }

     @Test
    public void shouldHaveAffixAsPWhenVisitIsPrepInitial() throws Exception {
        String province = "ward[22]";
        String affix = "P";

        patient = PatientTestData.setOiPrepIdentifierToPatient(defaultIdentifier);
        setUpMocks(patient);
        when(personAddress.getStateProvince()).thenReturn(province);
        when(StringUtils.substringBetween(province, "[", "]")).thenReturn("22");
        String expectedIdentifier = "22-OA-12-2017-P-01234";

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, affix, prepInitial);

        Assert.assertEquals(expectedIdentifier, patient.getPatientIdentifier(identifierType).getIdentifier());
    }

    @Test
    public void shouldThrowErrorWhenRequiredFieldsAreNull() throws Exception {
        String affix = "A";

        patient = PatientTestData.setOiPrepIdentifierToPatient(defaultIdentifier);
        setUpMocks(patient);
        when(personAddress.getStateProvince()).thenReturn(null);
        when(StringUtils.substringBetween(null, "[", "]")).thenReturn(null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Could not able to get required fields to generate Prep/Oi identifier");

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, affix, prepInitial);
    }

    @Test
    public void shouldThrowErrorWhenThereIsAChangeInVisitFromAToP() throws Exception {
        patient = PatientTestData.setOiPrepIdentifierToPatient("00-OA-63-2017-A-01368");

        setUpMocks(patient);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Can not change visit type from Initial Art Service to Prep Initial");

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, "A", prepInitial);
        verify(patientOiPrepIdentifier, times(0)).updateOiPrepIdentifier(patientUuid, "A", prepInitial);
    }

    @Test
    public void shouldChangeIdentifierAffixWhenThereIsAChangeInVisitFromPToA() throws Exception {
        String identifier = "00-OA-63-2017-P-01368";
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);
        setUpMocks(patient);

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, "A", initialArt);
        Assert.assertEquals("00-OA-63-2017-A-01368", patient.getPatientIdentifier(identifierType).getIdentifier());
    }

    private void setUpMocks(Patient patient) throws Exception {
        String district = "marate[OA]";
        String facility = "harare[12]";
        int year = 2017;
        String seqId = "01234";

        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(Year.class);
        PowerMockito.mockStatic(StringUtils.class);

        when(Context.getPersonService()).thenReturn(personService);
        when(personService.getPersonByUuid(patientUuid)).thenReturn(person);
        when(person.getPersonAddress()).thenReturn(personAddress);
        when(personAddress.getCityVillage()).thenReturn(district);
        when(personAddress.getPostalCode()).thenReturn(facility);
        when(StringUtils.substringBetween(district, "[", "]")).thenReturn("OA");
        when(StringUtils.substringBetween(facility, "[", "]")).thenReturn("12");
        when(Year.now()).thenReturn(yearObj);
        when(yearObj.getValue()).thenReturn(year);
        when(identifierSourceServiceWrapper.getSequenceValue(identifierSource)).thenReturn(seqId);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
    }

}
