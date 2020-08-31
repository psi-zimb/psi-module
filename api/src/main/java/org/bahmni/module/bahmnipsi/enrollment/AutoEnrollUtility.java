package org.bahmni.module.bahmnipsi.enrollment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AutoEnrollUtility {
    private Log logger = LogFactory.getLog(this.getClass());

    public  boolean checkProgramAlreadyEnrolled(String programName, List<PatientProgram> patientProgramsUsingPatientUUID){
        for(PatientProgram program : patientProgramsUsingPatientUUID){
            if(program.getProgram().getConcept().getName().toString().equals(programName))
                return true;
        }
        return false;
    }

    public  Patient getPatient(BahmniEncounterTransaction bahmniEncounterTransaction){
        return Context.getPatientService().getPatientByUuid(bahmniEncounterTransaction.getPatientUuid());
    }

    public  List<PatientProgram> getPatientProgramsList(Patient patient){
        List<PatientProgram> list = Context.getProgramWorkflowService().getPatientPrograms(patient,null,null,null,null,
                null,false);
        return filterPatientPrograms(list);
    }

    public void enrollProgram(PatientProgram patientProgram){
         Context.getProgramWorkflowService().savePatientProgram(patientProgram);
    }

    public  List<Program> getProgramsList(){
        return Context.getProgramWorkflowService().getAllPrograms();
    }

    public PatientProgram preparePatientProgramEntity(BahmniEncounterTransaction bahmniEncounterTransaction,String programName,List<Program> programs){
        PatientProgram patientProgram = null;
        for(Program program : programs){
            if(program.getConcept().getName().toString().equals(programName))
            {
                patientProgram = new PatientProgram();
                Patient patient = getPatient(bahmniEncounterTransaction);
                patientProgram.setPatient(patient);
                patientProgram.setDateEnrolled(new Date());
                Program tempProgramEntity = new Program();
                tempProgramEntity.setProgramId(program.getProgramId());
                tempProgramEntity.setUuid(program.getUuid());
                patientProgram.setProgram(tempProgramEntity);
                logger.error("patient program prepared successfully ->");
                break;
            }
        }
        return patientProgram;
    }

    private List<PatientProgram> filterPatientPrograms(List<PatientProgram> programsList)
    {
        List<PatientProgram> tempList = new ArrayList<>();
        logger.error("patient Programs list size -> "+programsList.size());
        for(PatientProgram patientProgram : programsList)
        {
            if(patientProgram.getDateCompleted() != null){
                logger.error("program added successfully ->"+patientProgram.toString());
                tempList.add(patientProgram);
            }
        }
        return tempList;
    }
}
