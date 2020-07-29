package org.bahmni.module.bahmnipsi.identifier;

//import org.bahmni.module.bahmnicore.dao.BahmniProgramWorkflowDAO;
//import org.bahmni.module.bahmnicore.dao.impl.BahmniHibernateProgramWorkflowDAOImpl;
//import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
//import org.bahmni.module.bahmnicore.service.impl.BahmniProgramWorkflowServiceImpl;
import org.bahmni.module.bahmnipsi.PatientTestData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
//import org.openmrs.module.episodes.service.EpisodeService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, PatientIdentifierSaveCommandImpl.class})
public class PatientIdentifierSaveCommandImplTest {
    @Mock
    private PatientOiPrepIdentifier patientOiPrepIdentifier;

    @Mock
    private PatientService patientService;

   // private BahmniProgramWorkflowService bahmniProgramWorkflowService;

//    @Mock
//    private EpisodeService episodeService;
//
//    @Mock
//    private BahmniProgramWorkflowDAO bahmniProgramWorkflowDAO;
//
//    @Mock
//    private BahmniHibernateProgramWorkflowDAOImpl bahmniHibernateProgramWorkflowDAOImpl;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private PatientIdentifierSaveCommandImpl patientIdentifierSaveCommandImpl = new PatientIdentifierSaveCommandImpl(patientOiPrepIdentifier);
    private String patientUuid = "23ffg-54lkk-lk";
    private Patient patient;
    private String initialArtService = "Initial ART service";
    private String prepInitial = "PrEP Initial";
    private String conceptName = "Reason for visit";

    @Before
    public void setUp() {
        patient = PatientTestData.setUpPatientData();
        //bahmniProgramWorkflowService = new BahmniProgramWorkflowServiceImpl(bahmniProgramWorkflowDAO,episodeService);
        mockStatic(Context.class);
        PowerMockito.when(Context.getPatientService()).thenReturn(patientService);
        PowerMockito.when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        //PowerMockito.when(Context.getService(BahmniProgramWorkflowService.class)).thenReturn(bahmniProgramWorkflowService);
       // PowerMockito.when(patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid,"A","INIT_ART_SERVICE")).thenReturn(true);
    }

    @Test
    public void shouldCallUpdateOiPrepIdentifierWithAffixAWhenServiceIsInitialArt() throws Exception {
        patientIdentifierSaveCommandImpl.setPatientOiPrepIdentifier(patientOiPrepIdentifier);
        doNothing().when(patientOiPrepIdentifier).updateOiPrepIdentifier(patientUuid, "A", "INIT_ART_SERVICE");
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(initialArtService,conceptName, patientUuid);
        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);

        verify(patientOiPrepIdentifier, times(1)).updateOiPrepIdentifier(patientUuid, "A", "INIT_ART_SERVICE");
    }

    @Test
    public void shouldCallUpdateOiPrepIdentifierWithAffixPWhenServiceIsPrepInitial() throws Exception {
        patientIdentifierSaveCommandImpl.setPatientOiPrepIdentifier(patientOiPrepIdentifier);
        doNothing().when(patientOiPrepIdentifier).updateOiPrepIdentifier(patientUuid, "P", "PrEP_INIT");
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(prepInitial, conceptName, patientUuid);
        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);

        verify(patientOiPrepIdentifier, times(1)).updateOiPrepIdentifier(patientUuid, "PR", "PrEP_INIT");
    }

    @Test
    public void shouldThrowExceptionWhenOiPrepIdentifierIsNotUpdatedWithInitialArtVisit() throws Exception {
        patientIdentifierSaveCommandImpl.setPatientOiPrepIdentifier(patientOiPrepIdentifier);
        exception.expect(RuntimeException.class);

        doThrow(RuntimeException.class).when(patientOiPrepIdentifier).updateOiPrepIdentifier(patientUuid, "A", "INIT_ART_SERVICE");
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(initialArtService, conceptName, patientUuid);
        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);

        verify(patientOiPrepIdentifier, times(1)).updateOiPrepIdentifier(patientUuid, "A", "INIT_ART_SERVICE");
    }

    @Test
    public void shouldThrowExceptionWhenOiPrepIdentifierIsNotUpdatedWithPrepInitial() throws Exception {
        patientIdentifierSaveCommandImpl.setPatientOiPrepIdentifier(patientOiPrepIdentifier);
        exception.expect(RuntimeException.class);

        doThrow(RuntimeException.class).when(patientOiPrepIdentifier).updateOiPrepIdentifier(patientUuid, "PR", "PrEP_INIT");
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(prepInitial, conceptName, patientUuid);
        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);

        verify(patientOiPrepIdentifier, times(1)).updateOiPrepIdentifier(patientUuid, "PR", "PrEP_INIT");
    }

    @Test
    public void shouldThrowErrorWhenBothPrepAndArtServicesSelected() {
        String identifier = "00-OA-63-2017-P-01368";
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);
        PowerMockito.when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);

        String conceptName = "Reason for visit";
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        LinkedHashMap<String, String> object1 = new LinkedHashMap<>();
        LinkedHashMap<String, String> object2 = new LinkedHashMap<>();
        object1.put("name", initialArtService);
        object2.put("name", prepInitial);
        BahmniObservation groupMember1 = new BahmniObservation();
        BahmniObservation groupMember2 = new BahmniObservation();
        BahmniObservation obs = new BahmniObservation();

        concept.setName(conceptName);
        groupMember1.setConcept(concept);
        groupMember1.setValue(object1);
        groupMember2.setConcept(concept);
        groupMember2.setValue(object2);
        Collection<BahmniObservation> groupMembersCollection = Arrays.asList(groupMember1, groupMember2);
        obs.setGroupMembers(groupMembersCollection);

        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();

        bahmniEncounterTransaction.setPatientUuid(patientUuid);
        bahmniEncounterTransaction.setObservations(Arrays.asList(obs));

        exception.expect(RuntimeException.class);
        exception.expectMessage("Both Initial Art Service and Prep Initial can not be selected at time. Please unselect one");

        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldNotCallUpdateOiPrepIdentifierIfRequiredServiceIsNotSelected() throws Exception {
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith("", conceptName, patientUuid);

        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);

        verify(patientOiPrepIdentifier, times(0)).updateOiPrepIdentifier(patientUuid, "A", "INIT_ART_SERVICE");
        verify(patientOiPrepIdentifier, times(0)).updateOiPrepIdentifier(patientUuid, "P", "INIT_ART_SERVICE");
    }

    @Test
    public void shouldNotDoAnythingIfReasonForVisitTypeIsNotSelected() throws Exception {
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith("", "", patientUuid);

        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);

        verify(patientOiPrepIdentifier, times(0)).updateOiPrepIdentifier(patientUuid, "A", "INIT_ART_SERVICE");
        verify(patientOiPrepIdentifier, times(0)).updateOiPrepIdentifier(patientUuid, "P", "INIT_ART_SERVICE");

    }

    @Test
    public void shouldNotCallEnrollIfVisitTypeIsOtherThanInitialART() throws Exception {
        String identifier = "00-OA-63-2017-P-01368";
        PatientProgram patientProgram = new PatientProgram();
        Patient patient = new Patient();
        Person person = new Person();
        person.setUuid("d44c703a-e526-4395-ba4f-af7ddf0d2155");

        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());

        Program program = new Program();
        program.setUuid("26a51046-b88b-11e9-b67c-080027e15975");
        patientProgram.setProgram(program);
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);

        PowerMockito.when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        //when(bahmniProgramWorkflowDAO.savePatientProgram(patientProgram)).thenReturn(patientProgram);

        String conceptName = "Reason for visit";
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        LinkedHashMap<String, String> object1 = new LinkedHashMap<>();
        object1.put("name", "Initial ART service1");
        BahmniObservation groupMember1 = new BahmniObservation();
        BahmniObservation obs = new BahmniObservation();

        concept.setName(conceptName);
        groupMember1.setConcept(concept);
        groupMember1.setValue(object1);
        Collection<BahmniObservation> groupMembersCollection = Arrays.asList(groupMember1);
        obs.setGroupMembers(groupMembersCollection);

        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();

        bahmniEncounterTransaction.setPatientUuid(patientUuid);
        bahmniEncounterTransaction.setObservations(Arrays.asList(obs));

        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldCallEnrollIfVisitTypeIsOtherThanInitialART() throws Exception {
        String identifier = "00-OA-63-2017-P-01368";
        PatientProgram patientProgram = new PatientProgram();
        Patient patient = new Patient();
        Person person = new Person();
        person.setUuid("d44c703a-e526-4395-ba4f-af7ddf0d2155");

        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());

        Program program = new Program();
        program.setUuid("26a51046-b88b-11e9-b67c-080027e15975");
        patientProgram.setProgram(program);
        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);

        PowerMockito.when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        //when(bahmniProgramWorkflowDAO.savePatientProgram(patientProgram)).thenReturn(patientProgram);

        String conceptName = "Reason for visit";
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        LinkedHashMap<String, String> object1 = new LinkedHashMap<>();
        object1.put("name", "Initial ART service");
        BahmniObservation groupMember1 = new BahmniObservation();
        BahmniObservation obs = new BahmniObservation();

        concept.setName(conceptName);
        groupMember1.setConcept(concept);
        groupMember1.setValue(object1);
        Collection<BahmniObservation> groupMembersCollection = Arrays.asList(groupMember1);
        obs.setGroupMembers(groupMembersCollection);

        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();

        bahmniEncounterTransaction.setPatientUuid(patientUuid);
        bahmniEncounterTransaction.setObservations(Arrays.asList(obs));
        patientIdentifierSaveCommandImpl.setPatientOiPrepIdentifier(patientOiPrepIdentifier);
        patientIdentifierSaveCommandImpl.update(bahmniEncounterTransaction);
    }
}