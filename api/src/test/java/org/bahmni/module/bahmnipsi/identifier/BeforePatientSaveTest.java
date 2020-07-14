package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.Calendar;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, BeforePatientSave.class})
public class BeforePatientSaveTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Patient patient;
    Object output;
    Object[] input;
    private Method method;
    @Mock
    private PatientUICIdentifier patientUICIdentifier;
    @Mock
    private PatientIdentifierService patientIdentifierService;

    @Test
    public void shouldNotCallUpdateUICIdentifierIfPatientIsNull() throws Exception {
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        input = new Object[]{null};

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(0)).updateUICIdentifier(anyObject());
    }

    @Test
    public void shouldCallUpdateUIC() throws Exception {
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        patient = PatientTestData.setUpPatientData();
        output = new Object();
        input = new Object[]{patient};

        PowerMockito.whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(1)).updateUICIdentifier(patient);
    }

    @Test
    public void shouldNotDoAnythingIfInterceptIsNotSavePatient() throws Exception {
        String methodToIntercept = "dontSavePatient";
        method = this.getClass().getMethod(methodToIntercept);
        input = new Object[]{patient};

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(0)).updateUICIdentifier(anyObject());
    }

    @Test
    public void shouldThrowExceptionIfTheGivenIdentifierIsNotMatchingWithPattern() throws Exception {
        String identifier = "00-OA-63-2017";
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);
        output = new Object();
        input = new Object[]{patient};

        PowerMockito.whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);

        exception.expectMessage("Given Prep/Oi Identifier is not matching with the Expected Pattern");
        exception.expect(RuntimeException.class);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);
    }

    @Test
    public void shouldNotThrowSequenceErrorIfYearEnteredIsLowerThanCurrentYear() throws Exception {
        String identifier = "00-OA-63-2017-A-00009";
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);
        output = new Object();
        input = new Object[]{patient};
        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);
        when(patientIdentifierService.getNextSeqValue(Matchers.anyString())).thenReturn(4);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);
    }

    @Test
    public void shouldThrowSequenceErrorIfYearEnteredIsSameAsCurrentYear() throws Exception {
        String identifier = "00-OA-63-2020-A-00009";
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);
        output = new Object();
        input = new Object[]{patient};
        PowerMockito.mockStatic(Context.class);
        mockStatic(Calendar.class);
        setCurrentYearTo(2020);
        PowerMockito.whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);
        when(patientIdentifierService.getNextSeqValue(Matchers.anyString())).thenReturn(4);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);
        exception.expectMessage("Next available Initial ART sequence number is 4 . Last five digits of PREP/OI Identifier entered cannot be greater than Next Available Sequence number. Update PrEP/OI Identifier to 00-OA-63-2020-A-4");
        exception.expect(RuntimeException.class);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);
    }

    @Test
    public void shouldThrowAppropriateErrorIfYearEnteredIsHigherThanCurrentYear() throws Exception {
        String identifier = "00-OA-63-2030-A-00009";
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);
        output = new Object();
        input = new Object[]{patient};
        PowerMockito.mockStatic(Context.class);
        mockStatic(Calendar.class);
        setCurrentYearTo(2020);
        PowerMockito.whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);
        when(patientIdentifierService.getNextSeqValue(Matchers.anyString())).thenReturn(4);
        when(Context.getService(PatientIdentifierService.class)).thenReturn(patientIdentifierService);

        exception.expectMessage("Year in identifier cannot be greater than current year.");
        exception.expect(RuntimeException.class);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);
    }

    private void setCurrentYearTo(int year) {
        when(Calendar.getInstance().get(Calendar.YEAR)).thenReturn(year);
    }

    public void savePatient() {

    }

    public void dontSavePatient() {

    }
}

