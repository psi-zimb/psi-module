package org.bahmni.module.bahmnipsi.identifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import  java.net.HttpURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Component
public class PatientIdentifierSaveCommandImpl implements EncounterDataPreSaveCommand {
    private static final int TWO = 2;
    private final String reasonForVisit = "Reason for visit";
    private final String initialArt = "Initial ART service";
    private final String prepInitial = "PrEP Initial";
    private final String INIT_ART_SEQ_TYPE = "INIT_ART_SERVICE";
    private final String PREP_INIT_SEQ_TYPE = "PrEP_INIT";
    private PatientOiPrepIdentifier patientOiPrepIdentifier;
    private Log log = LogFactory.getLog(this.getClass());

    @Autowired
    public PatientIdentifierSaveCommandImpl(PatientOiPrepIdentifier patientOiPrepIdentifier) {
        this.patientOiPrepIdentifier = patientOiPrepIdentifier;
    }

    @Override
    public BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction) {
        String patientUuid = bahmniEncounterTransaction.getPatientUuid();
        Collection<BahmniObservation> groupMembers = getVisitTypeObs(bahmniEncounterTransaction);

        String requiredObs = checkForArtPrepServiceObs(groupMembers);

        if (!requiredObs.isEmpty()) {
            if (requiredObs.equalsIgnoreCase(initialArt)) {
                try {
                    autoEnrollIntoProgram(bahmniEncounterTransaction);
                    patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, "A", INIT_ART_SEQ_TYPE);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            } else if (requiredObs.equalsIgnoreCase(prepInitial)) {
                try {
                    patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, "PR", PREP_INIT_SEQ_TYPE);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return bahmniEncounterTransaction;
    }

    private String checkForArtPrepServiceObs(Collection<BahmniObservation> groupMembers) {
        int artPrepServiceCount = 0;
        String requiredObs = "";
        for (BahmniObservation member : groupMembers) {
            LinkedHashMap<String, String> value = (LinkedHashMap<String, String>) member.getValue();
            if (value != null && value.get("name").equals(initialArt)) {
                artPrepServiceCount++;
                requiredObs = initialArt;
            } else if (value != null && value.get("name").equals(prepInitial)) {
                artPrepServiceCount++;
                requiredObs = prepInitial;
            }
            if (artPrepServiceCount == TWO) {
                throw new RuntimeException("Both Initial Art Service and Prep Initial can not be selected at time. Please unselect one");
            }
        }

        return requiredObs;
    }

    private Collection<BahmniObservation> getVisitTypeObs(BahmniEncounterTransaction bahmniEncounterTransaction) {
        Collection<BahmniObservation> observations = bahmniEncounterTransaction.getObservations();
        for (BahmniObservation obs : observations) {
            Collection<BahmniObservation> groupMembers = obs.getGroupMembers();
            for (BahmniObservation member : groupMembers) {
                String visitType = member.getConcept().getName();

                if (visitType.equalsIgnoreCase(reasonForVisit)) {
                    return groupMembers;
                }
            }
        }

        return Collections.emptyList();
    }

    public void setPatientOiPrepIdentifier(PatientOiPrepIdentifier patientOiPrepIdentifier) {
        this.patientOiPrepIdentifier = patientOiPrepIdentifier;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void autoEnrollIntoProgram(BahmniEncounterTransaction bahmniEncounterTransaction) throws IOException, DatabaseUpdateException, InputRequiredException {

//        URL url = new URL("http://localhost/openmrs/ws/rest/v1/program");
//        HttpURLConnection con = (HttpURLConnection)url.openConnection();
//        con.setRequestMethod("GET");
//        con.setRequestProperty("Content-Type", "application/json");
//        String credential = Base64.getEncoder().encodeToString( ("superman"+":"+"Admin123").getBytes("UTF-8"));
//        con.addRequestProperty("Authorization", "Basic " + credential.substring(0, credential.length()-1));
//        con.connect();
//        String response = con.getResponseMessage();
//        con.disconnect();
//        log.info(response);
//        //new stuff
        PatientProgram patientProgram = new PatientProgram();
        Patient patient = new Patient();
        patient.setId(Integer.valueOf(bahmniEncounterTransaction.getPatientId()));
        patient.setUuid(bahmniEncounterTransaction.getPatientUuid());

        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());

        Program program = new Program();
        program.setProgramId(6);
        program.setUuid("26a51046-b88b-11e9-b67c-080027e15975");
        patientProgram.setProgram(program);

        Context.getProgramWorkflowService().savePatientProgram(patientProgram);
//        Context.getProgramWorkflowService().getAllPrograms()
    }
}