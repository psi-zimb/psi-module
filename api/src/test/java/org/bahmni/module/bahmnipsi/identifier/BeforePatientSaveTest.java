package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, BeforePatientSave.class})
public class BeforePatientSaveTest {
    private Method method;
    Patient patient;
    Object output;
    Object[] input;

    @Mock
    private PatientUICIdentifier patientUICIdentifier;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldNotCallUpdateUICIdentifierIfPatientIsNull() throws Exception {
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        input= new Object[]{null};

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
        input= new Object[]{patient};

        whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(1)).updateUICIdentifier(patient);
    }

    @Test
    public void shouldNotDoAnythingIfInterceptIsNotSavePatient() throws Exception{
        String methodToIntercept = "dontSavePatient";
        method = this.getClass().getMethod(methodToIntercept);
        input= new Object[]{patient};

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
        input= new Object[]{patient};

        whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);

        exception.expectMessage("Given Prep/Oi Identifier is not matching with the Expected Pattern");
        exception.expect(RuntimeException.class);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);
    }

    public void savePatient() {

    }

    public void dontSavePatient() {

    }
}

