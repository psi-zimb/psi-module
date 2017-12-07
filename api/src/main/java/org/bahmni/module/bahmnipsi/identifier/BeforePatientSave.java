package org.bahmni.module.bahmnipsi.identifier;

import org.openmrs.Patient;
import org.openmrs.module.idgen.webservices.services.IdentifierSourceServiceWrapperImpl;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class BeforePatientSave implements MethodBeforeAdvice {

    private static final String methodToIntercept = "savePatient";

    @Override
    public void before(Method method, Object[] objects, Object o) throws Exception {
        if (method.getName().equalsIgnoreCase(methodToIntercept)) {
            Patient patient = (Patient) objects[0];
            if (patient != null && patient.getPatientId() == null) {
                PatientUICIdentifier patientUICIdentifier = new PatientUICIdentifier();
                patientUICIdentifier.updateUICIdentifier(patient);
                PatientOiPrepIdentifier patientOiPrepIdentifier = new PatientOiPrepIdentifier();
                IdentifierSourceServiceWrapperImpl identifierSourceServiceWrapper = new IdentifierSourceServiceWrapperImpl();
                patientOiPrepIdentifier.setIdentifierSourceServiceWrapper(identifierSourceServiceWrapper);
                patientOiPrepIdentifier.decreaseIdentifierNextValueByOne();
                patientOiPrepIdentifier.setIdentifierToDefaultValue(patient);
            }
        }
    }
}
