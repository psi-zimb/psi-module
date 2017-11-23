package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PatientUICIdentifier {

    private PatientIdentifierService patientIdentifierService;

    private String identifierType = "UIC";
    private String mothersName = "Mother's name";
    private String district = "District of Birth";

    public void setPatientIdentifierService(PatientIdentifierService patientIdentifierService) {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void updateUICIdentifier(Patient patient){
        List<String> requiredFields = getRequiredFields(patient);
        String identifier = getIdentifier(requiredFields);

        PatientIdentifier patientIdentifier = patient.getPatientIdentifier(identifierType);
        patientIdentifier.setIdentifier(identifier);
    }

    private List<String> getRequiredFields(Patient patient){
        String patientSurname = patient.getFamilyName();
        String nameOfMother = ((patient.getAttribute(mothersName).getValue().split(" "))[0]);
        PersonAttribute districtAttribute = patient.getAttribute(district);
        Concept concept = Context.getConceptService().getConcept(districtAttribute.getValue());
        String districtName = concept.getName().getName();
        String gender = patient.getGender();
        Date birthDate = patient.getBirthdate();
        String formattedBirthDate = new SimpleDateFormat("ddMMyy").format(birthDate);

        if(patientSurname !=null && nameOfMother != null && districtName != null && birthDate != null && gender != null) {
            nameOfMother = nameOfMother.trim();
            patientSurname = patientSurname.trim();
            districtName = districtName.trim();

            if (nameOfMother.length() > 1 && patientSurname.length() > 1 && districtName.length() > 1) {
                List<String> requiredFields = Arrays.asList(nameOfMother, patientSurname, districtName, formattedBirthDate, gender);
                return requiredFields;
            }
            throw new RuntimeException("Patient family name, Mothers first name fields should have two characters at least");
        }
        throw new RuntimeException("Required fields Patient family name and Mothers first name should not be null");
    }

    private String getIdentifier(List<String> fields) {
        List<String> stringsToFormat = Arrays.asList(fields.get(0), fields.get(1), fields.get(2));
        String birthDate = fields.get(3);
        String gender = fields.get(4);

        String id = getStringFromLastTwoLettersOfEachElement(stringsToFormat) + birthDate + gender;
        int count = Context.getService(PatientIdentifierService.class).getCountOfPatients(id);
        return id + count;
    }

    private String getStringFromLastTwoLettersOfEachElement(List<String> strings) {
        String str = "";

        for(String name:strings) {
            int nameLength = name.length();
            String lastTwoLetters = name.substring(nameLength - 2, nameLength);
            String lettersInCap = lastTwoLetters.toUpperCase();
            str = str.concat(lettersInCap);
        }
        return str;
    }
}