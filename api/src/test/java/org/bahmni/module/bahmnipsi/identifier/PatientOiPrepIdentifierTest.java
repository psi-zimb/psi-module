package org.bahmni.module.bahmnipsi.identifier;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.Year;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, Year.class, StringUtils.class, String.class, PatientOiPrepIdentifier.class })
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
    private PatientService patientService;

    @Mock
    private PatientIdentifierService patientIdentifierService;

    @Mock
    private PatientIdentifier patientIdentifier;

    @Mock
    private PatientIdentifierType patientIdentifierType;

    @Mock
    private StringBuffer stringBuffer;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private String identifierType = "PREP/OI Identifier";
    private String patientUuid = "32df-gdffd-343ghh";
    Patient patient;

    private PatientOiPrepIdentifier patientOiPrepIdentifier = new PatientOiPrepIdentifier();

    @Test
    public void shouldThrowErrorWhenRequiredFieldsAreNull() throws Exception {
        String affix = "A";

        patient = PatientTestData.setUpPatientData();
        setUpMocks(patient);
        when(personAddress.getStateProvince()).thenReturn(null);
        when(StringUtils.substringBetween(null, "[", "]")).thenReturn(null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Province, District and Facility should not be empty on the Registration first page to generate Prep/Oi Identifier.");

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, affix);
    }

    @Test
    public void shouldThrowErrorWhenCodeIsNotAvailableInRequiredFields() throws Exception {
        String affix = "A";
        String province = "province";

        patient = PatientTestData.setUpPatientData();
        setUpMocks(patient);
        when(personAddress.getStateProvince()).thenReturn(province);
        when(StringUtils.substringBetween(province, "[", "]")).thenReturn("");

        exception.expect(RuntimeException.class);
        exception.expectMessage("Province, District and Facility code lengths must be 2");

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, affix);
    }

    @Test
    public void shouldThrowErrorWhenNextSeqValueIsNotAbleToGet() throws Exception {
        String affix = "A";
        String province = "province[0D]";

        patient = PatientTestData.setUpPatientData();
        setUpMocks(patient);
        when(personAddress.getStateProvince()).thenReturn(province);
        when(StringUtils.substringBetween(province, "[", "]")).thenReturn("0D");
        doThrow(RuntimeException.class).when(patientIdentifierService).getNextSeqValue();

        exception.expect(RuntimeException.class);
        exception.expectMessage("Could not able to get next Sequence Value of the Prep/Oi Identifier");

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, affix);
    }

    @Test
    public void shouldUpdatePrepOiIdentifier() throws Exception {
        String affix = "A";
        String province = "province[0D]";
        int nextSeqValue = 8;
        String identifier = "0D-OA-12-2017-A-00008";
        String suffix = "00008";

        patient = PatientTestData.setUpPatientData();
        setUpMocks(patient);
        when(personAddress.getStateProvince()).thenReturn(province);
        when(StringUtils.substringBetween(province, "[", "]")).thenReturn("0D");
        when(patientIdentifierService.getNextSeqValue()).thenReturn(nextSeqValue);
        when(String.format("%05d", nextSeqValue)).thenReturn(suffix);
        doNothing().when(patientIdentifier).setIdentifier(identifier);
        doNothing().when(patientIdentifierService).incrementSeqValueByOne(nextSeqValue + 1);

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, affix);

        verifyStatic(VerificationModeFactory.times(2));
        Context.getPatientService();
        verifyStatic(VerificationModeFactory.times(1));
        Context.getPersonService();
        verifyStatic(VerificationModeFactory.times(1));
        Year.now();
        verifyStatic(VerificationModeFactory.times(3));
        Context.getService(PatientIdentifierService.class);
        verifyStatic(VerificationModeFactory.times(1));
        String.format("%05d", nextSeqValue);

        verify(patientService, times(2)).getPatientByUuid(patientUuid);
        verify(personService, times(1)).getPersonByUuid(patientUuid);
        verify(person, times(1)).getPersonAddress();
        verify(personAddress, times(1)).getStateProvince();
        verify(personAddress, times(1)).getCityVillage();
        verify(personAddress, times(1)).getPostalCode();
        verify(patientIdentifierService, times(1)).getNextSeqValue();
        verify(patientIdentifierService, times(1)).getIdentifierTypeId(identifierType);
        verify(patientIdentifier, times(1)).setIdentifierType(patientIdentifierType);
        verify(patientIdentifier, times(1)).setIdentifier(identifier);
        verify(patientIdentifierService, times(1)).incrementSeqValueByOne(nextSeqValue);
    }

    @Test
    public void shouldThrowErrorWhenThereIsAChangeInVisitFromAToP() throws Exception {
        patient = PatientTestData.setOiPrepIdentifierToPatient("00-OA-63-2017-A-01368");

        setUpMocks(patient);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Can not change visit type from Initial Art Service to Prep Initial");

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, "P");

        verifyStatic(VerificationModeFactory.times(1));
        Context.getPatientService();

        verify(patientService, times(1)).getPatientByUuid(patientUuid);
    }

    @Test
    public void shouldChangeIdentifierAffixWhenThereIsAChangeInVisitFromPToA() throws Exception {
        String identifier = "00-OA-63-2017-P-01368";
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);
        setUpMocks(patient);

        patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, "A");

        Assert.assertEquals("00-OA-63-2017-A-01368", patient.getPatientIdentifier(identifierType).getIdentifier());
    }

    private void setUpMocks(Patient patient) throws Exception {
        String district = "marate[OA]";
        String facility = "harare[12]";
        int year = 2017;
        int identifierTypeId = 5;

        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(Year.class);
        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.mockStatic(String.class);

        when(Context.getPersonService()).thenReturn(personService);
        when(personService.getPersonByUuid(patientUuid)).thenReturn(person);
        when(person.getPersonAddress()).thenReturn(personAddress);
        when(personAddress.getCityVillage()).thenReturn(district);
        when(personAddress.getPostalCode()).thenReturn(facility);
        when(StringUtils.substringBetween(district, "[", "]")).thenReturn("OA");
        when(StringUtils.substringBetween(facility, "[", "]")).thenReturn("12");
        when(Year.now()).thenReturn(yearObj);
        when(yearObj.getValue()).thenReturn(year);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);
        when(patientIdentifierService.getIdentifierTypeId(identifierType)).thenReturn(identifierTypeId);
        whenNew(PatientIdentifier.class).withNoArguments().thenReturn(patientIdentifier);
        whenNew(PatientIdentifierType.class).withArguments(identifierTypeId).thenReturn(patientIdentifierType);
        doNothing().when(patientIdentifier).setIdentifierType(patientIdentifierType);
    }
}
