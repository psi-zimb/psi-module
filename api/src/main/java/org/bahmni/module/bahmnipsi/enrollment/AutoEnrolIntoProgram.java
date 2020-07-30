package org.bahmni.module.bahmnipsi.enrollment;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AutoEnrolIntoProgram {


    public void autoEnrollIntoProgram(BahmniEncounterTransaction bahmniEncounterTransaction) {

        PatientProgram patientProgram = new PatientProgram();
        Patient patient = new Patient();
        patient.setId(Integer.valueOf(bahmniEncounterTransaction.getPatientId()));
        patient.setUuid(bahmniEncounterTransaction.getPatientUuid());


        Program program = new Program();
        program.setProgramId(6);
        program.setUuid("26a51046-b88b-11e9-b67c-080027e15975");
        patientProgram.setProgram(program);
        patientProgram.setDateEnrolled(new Date());

        Context.getProgramWorkflowService().savePatientProgram(patientProgram);
    }
}
