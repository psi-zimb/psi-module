package org.bahmni.module.bahmnipsi.identifier;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class BeforePatientSave implements MethodBeforeAdvice {

    private static final String methodToIntercept = "savePatient";
    private final String regex = "\\w{2}-\\w{2}-\\w{2}-\\d{4}-[AP]{1}-\\d{5}";
    private String identifierType = "PREP/OI Identifier";

    @Override
    public void before(Method method, Object[] objects, Object o) throws Exception {
        if (method.getName().equalsIgnoreCase(methodToIntercept)) {
            Patient patient = (Patient) objects[0];
            if(patient != null) {
                PatientUICIdentifier patientUICIdentifier = new PatientUICIdentifier();
                patientUICIdentifier.updateUICIdentifier(patient);

                PatientIdentifier patientIdentifier = patient.getPatientIdentifier(identifierType);
                if(patientIdentifier != null) {
                    String prepOiIdentifier = patientIdentifier.getIdentifier();
                    if (!prepOiIdentifier.matches(regex)) {
                        throw new RuntimeException("Given Prep/Oi Identifier is not matching with the Expected Pattern");
                    }
                }
            }
        }
    }
}
