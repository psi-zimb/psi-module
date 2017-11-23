package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
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


    @Test
    public void shouldNotCallUpdateUICIdentifierIfPatientIsNull() throws Exception {
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        input= new Object[]{null};

        PatientUICIdentifier patientUICIdentifier = mock(PatientUICIdentifier.class);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(0)).updateUICIdentifier(anyObject());
    }

    @Test
    public void shouldCallUpdateUICIdentifierIfPatientIsNotNull() throws Exception {
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        patient = PatientTestData.setUpPatientData();
        output = new Object();
        input= new Object[]{patient};
        PatientUICIdentifier patientUICIdentifier = mock(PatientUICIdentifier.class);

        whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);
        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(1)).updateUICIdentifier(patient);
    }

    @Test
    public void shouldNotPatientUicIdentifierMethodWhenInterceptIsNotSavePatient() throws Exception{
        String methodToIntercept = "dontSavePatient";
        method = this.getClass().getMethod(methodToIntercept);
        input= new Object[]{null};

        PatientUICIdentifier patientUICIdentifier = mock(PatientUICIdentifier.class);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(0)).updateUICIdentifier(anyObject());

    }

    public void savePatient() {

    }

    public void dontSavePatient() {

    }
}

