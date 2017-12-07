package org.bahmni.module.bahmnipsi;

import org.openmrs.*;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.*;

public class PatientTestData {

    public static Patient setUpPatientData() {
        Patient patient = new Patient();

        PersonName personName = new PersonName();
        personName.setFamilyName("Doe");

        Date birthDate = new Date(1078884319);

        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
        patientIdentifierType.setName("UIC");
        PatientIdentifier patientIdentifier = new PatientIdentifier("100002", patientIdentifierType, new Location());
        HashSet<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
        patientIdentifiers.add(patientIdentifier);

        PersonAttributeType personAttributeTypeMother = new PersonAttributeType();
        PersonAttributeType personAttributeTypeDistrict = new PersonAttributeType();
        personAttributeTypeMother.setName("Mother's name");
        personAttributeTypeDistrict.setName("District of Birth");
        PersonAttribute personMotherNameAttribute = new PersonAttribute(personAttributeTypeMother, "jaen Doe");
        PersonAttribute personDistrictAttribute = new PersonAttribute(personAttributeTypeDistrict, "510");
        HashSet<PersonAttribute> personAttributes = new HashSet<PersonAttribute>();
        personAttributes.add(personMotherNameAttribute);
        personAttributes.add(personDistrictAttribute);

        patient.addName(personName);
        patient.setGender("M");
        patient.setBirthdate(birthDate);
        patient.setIdentifiers(patientIdentifiers);
        patient.setAttributes(personAttributes);
        return patient;
    }

    public static Patient setOiPrepIdentifierToPatient() {
        Patient patient = PatientTestData.setUpPatientData();
        HashSet<PatientIdentifier> patientIdentifiers = getPatientIdentifiers("100005");
        patient.setIdentifiers(patientIdentifiers);

        return patient;
    }

    public static Patient setOiPrepIdentifierToPatient(String identifier) {
        Patient patient = PatientTestData.setUpPatientData();
        HashSet<PatientIdentifier> patientIdentifiers = getPatientIdentifiers(identifier);
        patient.setIdentifiers(patientIdentifiers);

        return patient;
    }

    private static HashSet<PatientIdentifier> getPatientIdentifiers(String identifier) {
        String identifierType = "PREP/OI Identifier";
        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
        patientIdentifierType.setName(identifierType);
        PatientIdentifier patientIdentifier = new PatientIdentifier(identifier, patientIdentifierType, new Location());
        HashSet<PatientIdentifier> patientIdentifiers = new HashSet<>();
        patientIdentifiers.add(patientIdentifier);
        return patientIdentifiers;
    }


    public static BahmniEncounterTransaction setUpEncounterTransactionDataWith(String visitType, String conceptName, String patientUuid) {
        Collection<BahmniObservation> observations = createBahmniObservationsWith(visitType, conceptName);

        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();

        bahmniEncounterTransaction.setPatientUuid(patientUuid);
        bahmniEncounterTransaction.setObservations(observations);

        return bahmniEncounterTransaction;
    }

    private static Collection<BahmniObservation> createBahmniObservationsWith(String visitType, String conceptName) {
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        LinkedHashMap<String, String> object = new LinkedHashMap<>();
        object.put("name", visitType);
        BahmniObservation groupMember = new BahmniObservation();
        BahmniObservation obs = new BahmniObservation();

        concept.setName(conceptName);
        groupMember.setConcept(concept);
        groupMember.setValue(object);
        Collection<BahmniObservation> groupMembersCollection = Arrays.asList(groupMember);
        obs.setGroupMembers(groupMembersCollection);
        return Arrays.asList(obs);
    }
}
