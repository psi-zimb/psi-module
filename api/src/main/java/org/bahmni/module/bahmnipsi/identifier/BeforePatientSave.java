package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class BeforePatientSave implements MethodBeforeAdvice {

    private static final String methodToIntercept = "savePatient";
    private final String regexP = "\\w{2}-\\w{2}-\\w{2}-\\d{4}-[P]{1}-\\d{5}";
    private final String regexPR = "\\w{2}-\\w{2}-\\w{2}-\\d{4}-(A|PR){1}-\\d{5}";
    private String identifierType = "PREP/OI Identifier";

    @Override
    public void before(Method method, Object[] objects, Object o) throws Exception {
        if (method.getName().equalsIgnoreCase(methodToIntercept)) {
            Patient patient = (Patient) objects[0];
            if(patient != null) {
                PatientUICIdentifier patientUICIdentifier = new PatientUICIdentifier();
                patientUICIdentifier.updateUICIdentifier(patient);

                PatientIdentifier patientIdentifier = patient.getPatientIdentifier(identifierType);

                if(patientIdentifier != null && !"".equals(patientIdentifier.getIdentifier())) {
                    String prepOiIdentifier = patientIdentifier.getIdentifier();
                    if (prepOiIdentifier.matches(regexP)) {
                        int year = Integer.parseInt(prepOiIdentifier.split("-")[3]);
                        int lastSeq = Context.getService(PatientIdentifierService.class).getLastSeqValue();
                        String sequenceIdEntered = prepOiIdentifier.split("-")[5];
                        int sequenceId = Integer.parseInt(sequenceIdEntered);
                        if(year >= 2019 && sequenceId > lastSeq)
                            throw new RuntimeException("Given Prep/Oi Identifier is not matching with the Expected Pattern");
                    } else if (!prepOiIdentifier.matches(regexPR)) {
                        throw new RuntimeException("Given Prep/Oi Identifier is not matching with the Expected Pattern");
                    } else {
                        String sequenceTypeEntered = prepOiIdentifier.split("-")[4];
                        String sequenceIdEntered = prepOiIdentifier.split("-")[5];
                        String sequenceType = "";
                        String sequence = "";
                        if("A".equals(sequenceTypeEntered)) {
                            sequenceType = "INIT_ART_SERVICE";
                            sequence = "Initial ART";
                        }
                        else if("PR".equals(sequenceTypeEntered)) {
                            sequenceType = "PrEP_INIT";
                            sequence = "PrEP";
                        }
                        int nextSequenceValue = Context.getService(PatientIdentifierService.class).getNextSeqValue(sequenceType);
                        int sequenceId = Integer.parseInt(sequenceIdEntered);
                        if(nextSequenceValue < sequenceId) {
                            throw new RuntimeException("Next available " +  sequence + " sequence number is " + nextSequenceValue + " Update Prep/OI Identified field");
                        }
                        else if (nextSequenceValue == sequenceId) {
                            Context.getService(PatientIdentifierService.class).incrementSeqValueByOne(nextSequenceValue,sequenceType);
                        }
                    }
                }
            }
        }
    }
}
