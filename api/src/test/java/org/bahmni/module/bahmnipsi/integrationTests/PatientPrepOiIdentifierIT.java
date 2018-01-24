package org.bahmni.module.bahmnipsi.integrationTests;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.identifier.PatientIdentifierSaveCommandImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PatientPrepOiIdentifierIT extends BaseModuleWebContextSensitiveTest {
    @Autowired
    PatientIdentifierSaveCommandImpl patientIdentifierSaveCommand;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private String identifierType = "PREP/OI Identifier";
    private String initialArtService = "Initial ART service";
    private String prepInitial = "PrEP Initial";
    private String conceptName = "Reason for visit";
    int year = Year.now().getValue();

    @Test
    public void shouldUpdatePrepOiIdentifierForPrepInitial() throws Exception {
        executeDataSet("PatientPrepOiIdentifier.xml");
        String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
        String expectedIdentifier = "02-0A-05-"+year+"-P-00010";

        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(prepInitial, conceptName, patientUuid);

        patientIdentifierSaveCommand.update(bahmniEncounterTransaction);

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        Integer identifierTypeId = Context.getPatientService().getPatientIdentifierTypeByName(identifierType).getId();
        String actualIdentifier = patient.getPatientIdentifier(identifierTypeId).getIdentifier();

        Assert.assertEquals(expectedIdentifier, actualIdentifier);
    }

    @Test
    public void shouldUpdatePrepOiIdentifierForInitialArt() throws Exception {
        executeDataSet("PatientPrepOiIdentifier.xml");
        String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
        String expectedIdentifier = "02-0A-05-"+year+"-A-00010";
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(initialArtService, conceptName, patientUuid);

        patientIdentifierSaveCommand.update(bahmniEncounterTransaction);

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        Integer identifierTypeId = Context.getPatientService().getPatientIdentifierTypeByName(identifierType).getId();
        String actualIdentifier = patient.getPatientIdentifier(identifierTypeId).getIdentifier();

        Assert.assertEquals(expectedIdentifier, actualIdentifier);
    }

    @Test
    public void shouldBeAbleToChangeIdentifierPToA() throws Exception {
        executeDataSet("PatientIdentifier.xml");
        String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        PatientIdentifier patientIdentifier = patient.getPatientIdentifier(identifierType);
        patientIdentifier.setIdentifier("02-0A-05-"+year+"-P-00078");
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(initialArtService, conceptName, patientUuid);

        patientIdentifierSaveCommand.update(bahmniEncounterTransaction);

        String actualIdentifier = patient.getPatientIdentifier(identifierType).getIdentifier();
        String expectedIdentifier = "02-0A-05-"+year+"-A-00078";

        Assert.assertEquals(expectedIdentifier, actualIdentifier);
    }

    @Test
    public void shouldNotAbleToChangeIdentifierFromAToP() throws Exception {
        executeDataSet("PatientIdentifier.xml");
        String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        PatientIdentifier patientIdentifier = patient.getPatientIdentifier(identifierType);
        patientIdentifier.setIdentifier("02-0A-05-"+year+"-A-00078");
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith(prepInitial, conceptName, patientUuid);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Can not change visit type from Initial Art Service to Prep Initial");

        patientIdentifierSaveCommand.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldNotUpdatePrepIdentifierWhenThereIsNoObs() throws Exception {
        executeDataSet("PatientIdentifier.xml");
        String patientUuid = "256ccf6d-6b41-455c-9be2-51ff4386ae76";
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith("", "", patientUuid);

        patientIdentifierSaveCommand.update(bahmniEncounterTransaction);
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        Assert.assertEquals(null, patient.getPatientIdentifier(identifierType));
    }

    @Test
    public void shouldNotUpdatePrepIdentifierWhenThereIsNoRequiredObsInReasonForVisit() throws Exception {
        executeDataSet("PatientIdentifier.xml");
        String patientUuid = "256ccf6d-6b41-455c-9be2-51ff4386ae76";
        BahmniEncounterTransaction bahmniEncounterTransaction = PatientTestData.setUpEncounterTransactionDataWith("Basic 1", conceptName, patientUuid);

        patientIdentifierSaveCommand.update(bahmniEncounterTransaction);
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        Assert.assertEquals(null, patient.getPatientIdentifier(identifierType));
    }

    @Test
    public void shouldThrowErrorWhenBothPrepAndArtServicesSelected() throws Exception {
        executeDataSet("PatientIdentifier.xml");
        String patientUuid = "256ccf6d-6b41-455c-9be2-51ff4386ae76";

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

        patientIdentifierSaveCommand.update(bahmniEncounterTransaction);
    }
}
