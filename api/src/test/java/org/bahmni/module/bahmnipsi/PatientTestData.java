package org.bahmni.module.bahmnipsi;

import org.openmrs.*;

import java.util.Date;
import java.util.HashSet;

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
}
