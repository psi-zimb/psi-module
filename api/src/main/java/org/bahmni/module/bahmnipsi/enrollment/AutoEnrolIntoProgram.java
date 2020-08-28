package org.bahmni.module.bahmnipsi.enrollment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AutoEnrolIntoProgram {

    AutoEnrollUtility autoEnrollUtility;
    private String errorMessage ="There Is An Issue With Auto Enrollment. Please Enroll The Patient Manually in Programs";

    @Autowired
    public AutoEnrolIntoProgram(AutoEnrollUtility autoEnrollUtility)
    {
        this.autoEnrollUtility = autoEnrollUtility;
    }

    static Map<String,String> mapping = prepareVisitTypeAndProgramNameMapping();
    private Log logger = LogFactory.getLog(this.getClass());

    public void autoEnrollIntoProgram(BahmniEncounterTransaction bahmniEncounterTransaction, Collection<BahmniObservation> groupMembers) {
        try{
        Patient patient = autoEnrollUtility.getPatient(bahmniEncounterTransaction);
        List<PatientProgram> patientProgramList = autoEnrollUtility.getPatientProgramsList(patient);
        List<Program> programs = autoEnrollUtility.getProgramsList();
        Set<String> programsToEnroll = prepareProgramsToAutoEnroll(groupMembers, patientProgramList);

        for(String programName: programsToEnroll)
            {
            PatientProgram patientProgram = autoEnrollUtility.preparePatientProgramEntity(bahmniEncounterTransaction,programName,programs);
            autoEnrollUtility.enrollProgram(patientProgram);
            }
        }
        catch(Exception e)
        {
            logger.error(e.getStackTrace());
            new RuntimeException(errorMessage);
        }
    }

    private static Map<String,String> prepareVisitTypeAndProgramNameMapping(){
        Map<String,String> visitTypeToProgramMap = new HashMap<>();
        visitTypeToProgramMap.put("FP Initial","FPS Program");
        visitTypeToProgramMap.put("FP Continuation","FPS Program");
        visitTypeToProgramMap.put("FP Counselling Only","FPS Program");
        visitTypeToProgramMap.put("Initial ART service","ART Program");
        visitTypeToProgramMap.put("ART Routine Service","ART Program");
        visitTypeToProgramMap.put("Unplanned or walk in visit","ART Program");
        visitTypeToProgramMap.put("Urgent","ART Program");
        visitTypeToProgramMap.put("Pick up Drugs (only)","ART Program");
        visitTypeToProgramMap.put("Review by MD/Doctor","ART Program");
        visitTypeToProgramMap.put("Lab test (only)","ART Program");
        visitTypeToProgramMap.put("Basic 1 and ART 1 service","ART Program");
        visitTypeToProgramMap.put("Home visit","ART Program");
        visitTypeToProgramMap.put("Hospital Visit","ART Program");
        visitTypeToProgramMap.put("Adherence Counselling","ART Program");
        visitTypeToProgramMap.put("Enhanced Adherence Counselling","ART Program");
        visitTypeToProgramMap.put("PrEP Initial","PrEP Program");
        visitTypeToProgramMap.put("PrEP Continuation","PrEP Program");
        visitTypeToProgramMap.put("Review by Nurse","IPT Program");
        visitTypeToProgramMap.put("HIV Self Testing","HTS Program");
        visitTypeToProgramMap.put("Provider Testing and Counseling","HTS Program");
        visitTypeToProgramMap.put("Confirmatory HIV testing and Counseling","HTS Program");
        visitTypeToProgramMap.put("Viac Initial","VIAC Program");
        visitTypeToProgramMap.put("Viac Continuation","VIAC Program");
        return visitTypeToProgramMap;
    }

    private Set<String> prepareProgramsToAutoEnroll(Collection<BahmniObservation> groupMembers, List<PatientProgram> patientProgramsUsingPatientUUID){
        Set<String> programSet = new HashSet<>();
        for (BahmniObservation member : groupMembers) {
            LinkedHashMap<String, String> value = (LinkedHashMap<String, String>) member.getValue();
            if(value.get("name")!=null && mapping.containsKey(value.get("name")) &&
                    (!(autoEnrollUtility.checkProgramAlreadyEnrolled(mapping.get(value.get("name")),patientProgramsUsingPatientUUID)))) {
                programSet.add(mapping.get(value.get("name")));
            }
        }
        return programSet;
    }

    public void setAutoEnrollUtility(AutoEnrollUtility autoEnrollUtility) {
        this.autoEnrollUtility = autoEnrollUtility;
    }
}
