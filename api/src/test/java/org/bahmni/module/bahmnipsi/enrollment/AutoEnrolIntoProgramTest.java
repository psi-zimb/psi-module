package org.bahmni.module.bahmnipsi.enrollment;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.identifier.PatientIdentifierSaveCommandImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, PatientIdentifierSaveCommandImpl.class, AutoEnrolIntoProgram.class, AutoEnrollUtility.class})
public class AutoEnrolIntoProgramTest {

    @Mock
    private PatientService patientService;

    @Mock
    private ProgramWorkflowService bahmniProgramWorkflowService;

    @Mock
    private AdministrationService administrationService;

    @Mock
    AutoEnrollUtility autoEnrollUtility;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private AutoEnrolIntoProgram autoEnrolIntoProgram = new AutoEnrolIntoProgram(autoEnrollUtility);

    private Patient patient = PatientTestData.setOiPrepIdentifierToPatient("00-OA-63-2017-P-01368");;
    private String patientUuid = "23ffg-54lkk-lk";
    private String initialArtService = "Initial ART service";
    private String conceptName = "Reason for visit";

    @Before
    public void setUp() {
        patient = PatientTestData.setUpPatientData();
        mockStatic(Context.class);
        PowerMockito.when(Context.getPatientService()).thenReturn(patientService);
        PowerMockito.when(Context.getAdministrationService()).thenReturn(administrationService);
        PowerMockito.when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        PowerMockito.when(Context.getProgramWorkflowService()).thenReturn(bahmniProgramWorkflowService);
    }

    @Test
    public void shouldNotEnrollIfVisitTypeIsNotValid() throws Exception {
        /************ Initialization *************/
        Program program = PatientTestData.setPatientProgramsData();

        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());
        patientProgram.setProgram(program);

        List<Program> programs = new ArrayList<>();
        program.setName("ART Program");
        programs.add(program);

        List<PatientProgram> patientProgramList = new ArrayList<>();
        patientProgramList.add(patientProgram);

        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith("Initial ART service2", conceptName, patientUuid);
        bahmniEncounterTransaction.setPatientId("1234");
        TreeSet<BahmniObservation> bos = (TreeSet)bahmniEncounterTransaction.getObservations();

        autoEnrolIntoProgram.setAutoEnrollUtility(autoEnrollUtility);
        PowerMockito.when(autoEnrollUtility.getPatientProgramsList(patient)).thenReturn(patientProgramList);
        PowerMockito.when(autoEnrollUtility.getProgramsList()).thenReturn(programs);
        PowerMockito.when(autoEnrollUtility.getPatient(bahmniEncounterTransaction)).thenReturn(patient);

        /************** Action ************/
        autoEnrolIntoProgram.autoEnrollIntoProgram(bahmniEncounterTransaction,bos.pollFirst().getGroupMembers());

        /************* Verification *********/
        verify(autoEnrollUtility,times(0)).enrollProgram(anyObject());
    }

    @Test
    public void shouldEnrollIfVisitTypeIsValid() throws Exception {
        /************ Initialization *************/
        Program program = PatientTestData.setPatientProgramsData();

        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());
        patientProgram.setProgram(program);

        List<Program> programs = new ArrayList<>();
        program.setName("ART Program");
        programs.add(program);

        List<PatientProgram> patientProgramList = new ArrayList<>();
        patientProgramList.add(patientProgram);

        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(initialArtService, conceptName, patientUuid);
        bahmniEncounterTransaction.setPatientId("1234");
        TreeSet<BahmniObservation> bos = (TreeSet)bahmniEncounterTransaction.getObservations();


        autoEnrolIntoProgram.setAutoEnrollUtility(autoEnrollUtility);
        PowerMockito.when(autoEnrollUtility.getPatientProgramsList(patient)).thenReturn(patientProgramList);
        PowerMockito.when(autoEnrollUtility.getProgramsList()).thenReturn(programs);
        PowerMockito.when(autoEnrollUtility.getPatient(bahmniEncounterTransaction)).thenReturn(patient);
        PowerMockito.when(autoEnrollUtility.checkProgramAlreadyEnrolled("ART Program",patientProgramList)).thenReturn(false);
        PowerMockito.when(autoEnrollUtility.preparePatientProgramEntity(bahmniEncounterTransaction,"ART Program",programs)).thenReturn(patientProgram);

        /************** Action ************/
        autoEnrolIntoProgram.autoEnrollIntoProgram(bahmniEncounterTransaction, bos.pollFirst().getGroupMembers());

        /************* Verification *********/
        verify(autoEnrollUtility,times(1)).enrollProgram(anyObject());
    }

    @Test
    public void shouldNotEnrollIfVisitTypeIsValidAndProgramNameMappingNotMatchedWithSystemDefinedPrograms() throws Exception {
        /************ Initialization *************/
        Program program = PatientTestData.setPatientProgramsData();

        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());
        patientProgram.setProgram(program);

        List<Program> programs = new ArrayList<>();
        program.setName("ART Program");
        programs.add(program);

        List<PatientProgram> patientProgramList = new ArrayList<>();
        patientProgramList.add(patientProgram);

        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(initialArtService, conceptName, patientUuid);
        bahmniEncounterTransaction.setPatientId("1234");
        TreeSet<BahmniObservation> bos = (TreeSet)bahmniEncounterTransaction.getObservations();

        autoEnrolIntoProgram.setAutoEnrollUtility(autoEnrollUtility);
        PowerMockito.when(autoEnrollUtility.getPatientProgramsList(patient)).thenReturn(patientProgramList);
        PowerMockito.when(autoEnrollUtility.getProgramsList()).thenReturn(programs);
        PowerMockito.when(autoEnrollUtility.getPatient(bahmniEncounterTransaction)).thenReturn(patient);
        PowerMockito.when(autoEnrollUtility.checkProgramAlreadyEnrolled("ART Program",patientProgramList)).thenReturn(false);
        PowerMockito.when(autoEnrollUtility.preparePatientProgramEntity(bahmniEncounterTransaction,"ART Program",programs)).thenReturn(null);

        /************** Action ************/
        autoEnrolIntoProgram.autoEnrollIntoProgram(bahmniEncounterTransaction, bos.pollFirst().getGroupMembers());

        /************* Verification *********/
        verify(autoEnrollUtility,times(0)).enrollProgram(anyObject());
    }

    @Test
    public void shouldEnrollInMultipleProgramsIfMultipleVisitTypesWereSelected() throws Exception {
        /************ Initialization *************/
        Program program = PatientTestData.setPatientProgramsData();
        String identifier = "00-OA-63-2017-P-01368";
        PatientProgram patientProgram = new PatientProgram();
        Patient patient = new Patient();
        Person person = new Person();
        person.setUuid("d44c703a-e526-4395-ba4f-af7ddf0d2155");
        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());
        patientProgram.setProgram(program);

        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);

        List<Program> programs = new ArrayList<>();
        program.setName("ART Program");
        programs.add(program);
        program.setName("VIAC Program");
        programs.add(program);

        List<PatientProgram> patientProgramList = new ArrayList<>();
        patientProgramList.add(patientProgram);
        patientProgramList.add(patientProgram);

        String conceptName = "Reason for visit";
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        LinkedHashMap<String, String> object1 = new LinkedHashMap<>();
        LinkedHashMap<String, String> object2 = new LinkedHashMap<>();
        object1.put("name", initialArtService);
        object2.put("name", "Viac Initial");
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

        PowerMockito.when(autoEnrollUtility.getPatientProgramsList(patient)).thenReturn(patientProgramList);
        PowerMockito.when(autoEnrollUtility.getProgramsList()).thenReturn(programs);
        PowerMockito.when(autoEnrollUtility.getPatient(bahmniEncounterTransaction)).thenReturn(patient);
        PowerMockito.when(autoEnrollUtility.checkProgramAlreadyEnrolled("ART Program",patientProgramList)).thenReturn(false);
        PowerMockito.when(autoEnrollUtility.checkProgramAlreadyEnrolled("VIAC Program",patientProgramList)).thenReturn(false);
        PowerMockito.when(autoEnrollUtility.preparePatientProgramEntity(bahmniEncounterTransaction,"ART Program",programs)).thenReturn(patientProgram);
        PowerMockito.when(autoEnrollUtility.preparePatientProgramEntity(bahmniEncounterTransaction,"VIAC Program",programs)).thenReturn(patientProgram);
        autoEnrolIntoProgram.setAutoEnrollUtility(autoEnrollUtility);

        /************** Action ************/
        autoEnrolIntoProgram.autoEnrollIntoProgram(bahmniEncounterTransaction,obs.getGroupMembers());

        /************* Verification *********/
        verify(autoEnrollUtility,times(2)).enrollProgram(anyObject());

    }

    @Test
    public void shouldNotEnrollIfPatientIsAlreadyEnrolledIntoProgram()
    {
        /************ Initialization *************/
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(initialArtService, conceptName, patientUuid);
        bahmniEncounterTransaction.setPatientId("1234");
        TreeSet<BahmniObservation> bos = (TreeSet)bahmniEncounterTransaction.getObservations();

        Program program = PatientTestData.setPatientProgramsData();
        String identifier = "00-OA-63-2017-P-01368";

        PatientProgram patientProgram = new PatientProgram();
        Patient patient = new Patient();
        Person person = new Person();
        person.setUuid("d44c703a-e526-4395-ba4f-af7ddf0d2155");
        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());
        patientProgram.setProgram(program);

        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);

        List<Program> programs = new ArrayList<>();
        program.setName("ART Program");
        programs.add(program);

        List<PatientProgram> patientProgramList = new ArrayList<>();
        patientProgramList.add(patientProgram);

        PowerMockito.when(autoEnrollUtility.getPatientProgramsList(patient)).thenReturn(patientProgramList);
        PowerMockito.when(autoEnrollUtility.getProgramsList()).thenReturn(programs);
        PowerMockito.when(autoEnrollUtility.getPatient(bahmniEncounterTransaction)).thenReturn(patient);
        PowerMockito.when(autoEnrollUtility.checkProgramAlreadyEnrolled("ART Program",patientProgramList)).thenReturn(true);
        PowerMockito.when(autoEnrollUtility.preparePatientProgramEntity(bahmniEncounterTransaction,"ART Program",programs)).thenReturn(patientProgram);
        autoEnrolIntoProgram.setAutoEnrollUtility(autoEnrollUtility);

        /************** Action ************/
        autoEnrolIntoProgram.autoEnrollIntoProgram(bahmniEncounterTransaction,bos.pollFirst().getGroupMembers());

        /************* Verification *********/
        verify(autoEnrollUtility,times(0)).enrollProgram(anyObject());
    }

    @Test
    public void shouldEnrollIntoUnEnrolledProgramsWhenMultipleVisitTypesWereSelected()
    {
        /************ Initialization *************/
        String identifier = "00-OA-63-2017-P-01368";
        PatientProgram patientProgram = new PatientProgram();
        Patient patient = new Patient();
        Person person = new Person();
        person.setUuid("d44c703a-e526-4395-ba4f-af7ddf0d2155");

        patientProgram.setPatient(patient);
        patientProgram.setDateEnrolled(new Date());

        Program program = PatientTestData.setPatientProgramsData();
        patientProgram.setProgram(program);

        patient = PatientTestData.setOiPrepIdentifierToPatient(identifier);

        List<Program> programs = new ArrayList<>();
        program.setName("ART Program");
        programs.add(program);
        program.setName("VIAC Program");
        programs.add(program);

        List<PatientProgram> patientProgramList = new ArrayList<>();
        patientProgramList.add(patientProgram);
        patientProgramList.add(patientProgram);

        String conceptName = "Reason for visit";
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        LinkedHashMap<String, String> object1 = new LinkedHashMap<>();
        LinkedHashMap<String, String> object2 = new LinkedHashMap<>();
        object1.put("name", initialArtService);
        object2.put("name", "Viac Initial");
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

        PowerMockito.when(autoEnrollUtility.getPatientProgramsList(patient)).thenReturn(patientProgramList);
        PowerMockito.when(autoEnrollUtility.getProgramsList()).thenReturn(programs);
        PowerMockito.when(autoEnrollUtility.getPatient(bahmniEncounterTransaction)).thenReturn(patient);
        PowerMockito.when(autoEnrollUtility.checkProgramAlreadyEnrolled("ART Program",patientProgramList)).thenReturn(false);
        PowerMockito.when(autoEnrollUtility.checkProgramAlreadyEnrolled("VIAC Program",patientProgramList)).thenReturn(true);
        PowerMockito.when(autoEnrollUtility.preparePatientProgramEntity(bahmniEncounterTransaction,"ART Program",programs)).thenReturn(patientProgram);
        autoEnrolIntoProgram.setAutoEnrollUtility(autoEnrollUtility);

        /************** Action ************/
        autoEnrolIntoProgram.autoEnrollIntoProgram(bahmniEncounterTransaction,obs.getGroupMembers());

        /************* Verification *********/
        verify(autoEnrollUtility,times(1)).enrollProgram(anyObject());
    }

}