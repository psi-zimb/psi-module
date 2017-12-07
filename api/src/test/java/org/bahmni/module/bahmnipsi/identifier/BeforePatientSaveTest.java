package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.webservices.services.IdentifierSourceServiceWrapperImpl;
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

    @Mock
    private PatientOiPrepIdentifier patientOiPrepIdentifier;

    @Mock
    private IdentifierSourceServiceWrapperImpl identifierSourceServiceWrapperImpl;


    @Test
    public void shouldNotCallUpdateUICIdentifierIfPatientIsNull() throws Exception {
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        input= new Object[]{null};

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(0)).updateUICIdentifier(anyObject());
        verify(patientOiPrepIdentifier, times(0)).decreaseIdentifierNextValueByOne();
        verify(patientOiPrepIdentifier, times(0)).setIdentifierToDefaultValue(anyObject());
    }

    @Test
    public void shouldUpdateUICAndDecreasePrepIdByOne() throws Exception {
        String methodToIntercept = "savePatient";
        method = this.getClass().getMethod(methodToIntercept);
        patient = PatientTestData.setUpPatientData();
        output = new Object();
        input= new Object[]{patient};

        whenNew(PatientUICIdentifier.class).withNoArguments().thenReturn(patientUICIdentifier);
        whenNew(PatientOiPrepIdentifier.class).withNoArguments().thenReturn(patientOiPrepIdentifier);
        whenNew(IdentifierSourceServiceWrapperImpl.class).withNoArguments().thenReturn(identifierSourceServiceWrapperImpl);
        doNothing().when(patientUICIdentifier).updateUICIdentifier(patient);
        doNothing().when(patientOiPrepIdentifier).decreaseIdentifierNextValueByOne();
        doNothing().when(patientOiPrepIdentifier).setIdentifierToDefaultValue(patient);
        doNothing().when(patientOiPrepIdentifier).setIdentifierSourceServiceWrapper(identifierSourceServiceWrapperImpl);

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(1)).updateUICIdentifier(patient);
        verify(patientOiPrepIdentifier, times(1)).decreaseIdentifierNextValueByOne();
        verify(patientOiPrepIdentifier, times(1)).setIdentifierToDefaultValue(patient);
        verify(patientOiPrepIdentifier, times(1)).setIdentifierSourceServiceWrapper(identifierSourceServiceWrapperImpl);
    }

    @Test
    public void shouldNotDoAnythingIfInterceptIsNotSavePatient() throws Exception{
        String methodToIntercept = "dontSavePatient";
        method = this.getClass().getMethod(methodToIntercept);
        input= new Object[]{patient};

        BeforePatientSave beforePatientSave = new BeforePatientSave();
        beforePatientSave.before(method, input, output);

        verify(patientUICIdentifier, times(0)).updateUICIdentifier(anyObject());
        verify(patientOiPrepIdentifier, times(0)).decreaseIdentifierNextValueByOne();
        verify(patientOiPrepIdentifier, times(0)).setIdentifierToDefaultValue(anyObject());
        verify(patientOiPrepIdentifier, times(0)).setIdentifierSourceServiceWrapper(identifierSourceServiceWrapperImpl);
    }

    public void savePatient() {

    }

    public void dontSavePatient() {

    }
}

