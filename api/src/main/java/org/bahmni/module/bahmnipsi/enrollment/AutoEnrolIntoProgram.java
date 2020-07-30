package org.bahmni.module.bahmnipsi.enrollment;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class AutoEnrolIntoProgram {


    public void autoEnrollIntoProgram(BahmniEncounterTransaction bahmniEncounterTransaction) {

        /*
         * Section to get Programs and get programId mapped to VisitType
         * List<Program> programs = Context.getProgramWorkflowService().getAllPrograms();
         * */

        /*
         * Section to get enrollment details
         *  Context.getProgramWorkflowService().getPatientProgramByUuid(bahmniEncounterTransaction.getPatientUuid());
         * */
        PatientProgram patientProgram = new PatientProgram();
        Patient patient = Context.getPatientService().getPatientByUuid(bahmniEncounterTransaction.getPatientUuid());
        Program program = new Program();
        program.setProgramId(6);
        program.setUuid("26a51046-b88b-11e9-b67c-080027e15975");
        patientProgram.setPatient(patient);
        patientProgram.setProgram(program);
        patientProgram.setDateEnrolled(new Date());

        Context.getProgramWorkflowService().savePatientProgram(patientProgram);
    }

    private List<Program> getProgramsList(){
        return Context.getProgramWorkflowService().getAllPrograms();
    }
}
