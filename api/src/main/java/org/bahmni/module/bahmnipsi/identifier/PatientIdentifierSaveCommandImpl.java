package org.bahmni.module.bahmnipsi.identifier;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

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
                    autoEnrollIntoProgram(groupMembers);
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

    private void autoEnrollIntoProgram(Collection<BahmniObservation> groupMembers) throws Exception {

        //try
        {
            URL url = new URL("https://dev-91.digitalhealthunit.org/openmrs/ws/rest/v1/program");
            HttpsURLConnectionImpl con = (HttpsURLConnectionImpl) url.openConnection();
            con.setDefaultHostnameVerifier((hostname, session) -> true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            String credential = Base64.getEncoder().encodeToString(("superman" + ":" + "Admin123").getBytes("UTF-8"));
            con.addRequestProperty("Authorization", "Basic " + credential.substring(0, credential.length() - 1));

            System.out.println(con.getResponseCode() + con.getResponseMessage());

            StringBuffer sbf = new StringBuffer();
            sbf.append(con.getResponseCode() + con.getResponseMessage());

            PatientProgram patientProgram = new PatientProgram();
            Patient patient = new Patient();
            Person person = new Person();
            person.setUuid("6353499f-e039-4147-9b4d-5c20101a9107");

            patientProgram.setPatient(patient);
            patientProgram.setDateEnrolled(new Date());

            PatientState patientState = new PatientState();
            patientState.setDateCreated(new Date());

            Set<PatientState> sets = new TreeSet<PatientState>();
            sets.add(patientState);

            patientProgram.setStates(sets);

            Program program = new Program();
            program.setUuid("26a51046-b88b-11e9-b67c-080027e15975");
            patientProgram.setProgram(program);

            for (BahmniObservation member : groupMembers) {
                LinkedHashMap<String, String> value = (LinkedHashMap<String, String>) member.getValue();
                if (value != null && value.get("name").equals(initialArt)) {
                }
            }

            //url = new URL("https://dev-91.digitalhealthunit.org/openmrs/ws/rest/v1/bahmniprogramenrollment");
            String payload = "{" +
                    "\"patient\": \"6353499f-e039-4147-9b4d-5c20101a9107\"," +
                    "\"program\": \"26a51046-b88b-11e9-b67c-080027e15975\"," +
                    "\"dateEnrolled\": \"2020-07-30T00:00:00+0530\"" +
                    "}";
            StringEntity entity = new StringEntity(payload,
                    ContentType.APPLICATION_JSON);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("https://dev-91.digitalhealthunit.org/openmrs/ws/rest/v1/bahmniprogramenrollment");
            request.setEntity(entity);
            request.setHeader("Connection","keep-alive");
            request.setHeader("Content-Type", "application/json;charset=UTF-8");
            request.setHeader("Disable-WWW-Authenticate", "true");
            request.setHeader("Host","dev-91.digitalhealthunit.org");
            request.setHeader("Authorization",("Basic " + credential.substring(0, credential.length() - 1)));
            sbf.append(entity.toString());
            sbf.append(request);

            HttpResponse response = httpClient.execute(request);
            System.out.println("Calling Enrollment API" + response.getStatusLine().getStatusCode());
            sbf.append("Calling Enrollment API" + response.getStatusLine().getStatusCode()+ response);
            //Context.getService(BahmniProgramWorkflowService.class);
            throw new Exception(sbf.toString());
        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw e;
//        }
//       BahmniProgramWorkflowService service =  Context.getService(BahmniProgramWorkflowService.class);
//       PatientProgram patProgram = service.savePatientProgram(patientProgram);
    }
}