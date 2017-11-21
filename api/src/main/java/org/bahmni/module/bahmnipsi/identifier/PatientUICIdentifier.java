package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientUICIdentifier {

    PatientIdentifierService patientIdentifierService;

    private String identifierType = "UIC";
    private String mothersName = "Mother's name";
    private String district = "District of Birth";

    public void setPatientIdentifierService(PatientIdentifierService patientIdentifierService) {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void updateUICIdentifier(Patient patient) {
        String patientSurname = patient.getMiddleName();
        String nameOfMother = (patient.getAttribute(mothersName).getValue().split(" "))[0];
        PersonAttribute districtAttribute = patient.getAttribute(district);
        Concept concept = Context.getConceptService().getConcept(districtAttribute.getValue());
        String districtName = concept.getName().getName();
        String gender = patient.getGender();

        Date birthDate = patient.getBirthdate();
        String formattedDate = new SimpleDateFormat("ddMMyy").format(birthDate);

        List<String> stringsToFormat = new ArrayList<>();
        stringsToFormat.add(nameOfMother);
        stringsToFormat.add(patientSurname);
        stringsToFormat.add(districtName);
        String id = getAsOneString(stringsToFormat) + formattedDate + gender;

        int count = Context.getService(PatientIdentifierService.class).getCountOfPatients(id);

        PatientIdentifier identifier = patient.getPatientIdentifier(identifierType);
        identifier.setIdentifier(id+count);
    }

    private String getAsOneString(List<String> strings) {
        String str = "";

        for(String name:strings) {
            int nameLength = name.length();
            String lastTwoLetters = name.substring(nameLength - 2, nameLength);
            String lettersInCap = lastTwoLetters.toUpperCase();
            if (lettersInCap.charAt(0) < lettersInCap.charAt(1)) {
                str = str.concat("" + lettersInCap.charAt(1) + lettersInCap.charAt(0));
            } else {
                str = str.concat(lettersInCap);
            }
        }

        return str;
    }
}