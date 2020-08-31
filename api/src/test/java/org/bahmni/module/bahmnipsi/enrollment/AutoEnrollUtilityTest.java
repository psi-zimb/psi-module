package org.bahmni.module.bahmnipsi.enrollment;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.identifier.PatientIdentifierSaveCommandImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, AutoEnrollUtility.class})
public class AutoEnrollUtilityTest {

    @Mock
    private PatientService patientService;

    @Mock
    private ProgramWorkflowService bahmniProgramWorkflowService;

    @Mock
    private AdministrationService administrationService;

    private AutoEnrollUtility autoEnrollUtility = new AutoEnrollUtility();

    private Patient patient = PatientTestData.setOiPrepIdentifierToPatient("00-OA-63-2017-P-01368");;
    private String patientUuid = "23ffg-54lkk-lk";
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
    public void shouldReturnPatient(){
        /************ Initialization *************/
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith("Initial ART service2", conceptName, patientUuid);
        PowerMockito.when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);

        /************** Action ************/
        Patient patient = autoEnrollUtility.getPatient(bahmniEncounterTransaction);

        /************* Verification *********/
        assertEquals(patient.getId(),this.patient.getId());
    }

    @Test
    public void shoudlReturnPatientProgramsList(){
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

        PowerMockito.when(bahmniProgramWorkflowService.getPatientPrograms(patient,null,null,null,null,
                null,false)).thenReturn(patientProgramList);
        /************* Action & Verification *********/
        assertNotNull(autoEnrollUtility.getPatientProgramsList(patient));
    }

    @Test
    public void shouldReturnProgramsList(){
        /************ Initialization *************/
        Program program = PatientTestData.setPatientProgramsData();
        List<Program> programs = new ArrayList<>();
        program.setName("ART Program");
        programs.add(program);

        PowerMockito.when(bahmniProgramWorkflowService.getAllPrograms()).thenReturn(programs);

        /************* Action & Verification *********/
        assertNotNull(autoEnrollUtility.getProgramsList());
    }
    @Test
    public void shouldReturnFalseIfProgramIsNotEnrolled(){
        assertEquals(false,autoEnrollUtility.checkProgramAlreadyEnrolled("ART Program",new ArrayList<PatientProgram>()));
    }

    @Test
    public void shouldReturnNullWhenSystemDefinedProgramsNotMatchingWithPassedProgramName(){
        /************ Initialization *************/
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith("Initial ART service2", conceptName, patientUuid);
        assertNull(autoEnrollUtility.preparePatientProgramEntity(bahmniEncounterTransaction,"ART Program",new ArrayList<Program>()));
    }

}