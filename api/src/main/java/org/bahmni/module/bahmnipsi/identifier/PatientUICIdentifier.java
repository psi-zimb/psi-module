package org.bahmni.module.bahmnipsi.identifier;

import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.PatientIdentifierType.LocationBehavior.NOT_USED;

public class PatientUICIdentifier {

    private String identifierType = "UIC";
    private String mothersName = "Mother's name";
    private String district = "District of Birth";
    private String areYouTwin = "Are you a twin?";
    private String areYouFirstBorn = "If yes, are you the firstborn?";
    private final int requiredFieldsMinLength = 2;
    private String yes = "Yes";
    private String no = "No";
    private String twinOne = "T1";
    private String twinTwo = "T2";
    private final int requiredFieldsLengthWithTwins = 6;

    public void updateUICIdentifier(Patient patient){
        List<String> requiredFields = getRequiredFields(patient);
        String identifier = getIdentifier(requiredFields);

        PatientIdentifier patientIdentifier = patient.getPatientIdentifier(identifierType);

        if (patientIdentifier == null) {
            patientIdentifier = createUICPatientIdentifier();
            patient.addIdentifier(patientIdentifier);
        }
        patientIdentifier.setIdentifier(identifier);
    }

    private PatientIdentifier createUICPatientIdentifier() {
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        int identifierTypeId = Context.getService(PatientIdentifierService.class).getIdentifierTypeId(identifierType);
        PatientIdentifierType patientIdentifierType = new PatientIdentifierType(identifierTypeId);
        patientIdentifierType.setName(identifierType);
        patientIdentifierType.setLocationBehavior(NOT_USED);
        patientIdentifier.setIdentifierType(patientIdentifierType);
        return patientIdentifier;
    }

    private List<String> getRequiredFields(Patient patient){
        String patientSurname = patient.getFamilyName();
        String mothersName = patient.getAttribute(this.mothersName) != null ? patient.getAttribute(this.mothersName).getValue() : null;
        String mothersFirstName = mothersName != null? mothersName.split(" ")[0] : null;
        String districtName = getAttributeValue(district, patient);
        String gender = patient.getGender();
        Date birthDate = patient.getBirthdate();
        String formattedBirthDate = birthDate != null ?new SimpleDateFormat("ddMMyy").format(birthDate) : null;
        String twinValue = getAttributeValue(areYouTwin, patient);
        String firstBornValue = getAttributeValue(areYouFirstBorn, patient);

        if(patientSurname !=null) {
            if(mothersFirstName != null) {
                if(districtName != null) {
                    if(birthDate != null) {
                        if(gender != null) {
                            mothersFirstName = mothersFirstName.trim();
                            patientSurname = patientSurname.trim();
                            districtName = districtName.trim();

                            if (mothersFirstName.length() >= requiredFieldsMinLength) {
                                if (patientSurname.length() >= requiredFieldsMinLength) {
                                    if (districtName.length() >= requiredFieldsMinLength) {
                                        if ((twinValue == null && firstBornValue == null) || (no.equals(twinValue) && firstBornValue == null)) {
                                            return Arrays.asList(mothersFirstName, patientSurname, districtName, formattedBirthDate, gender);
                                        } else if (yes.equals(twinValue) && yes.equals(firstBornValue)) {
                                            return Arrays.asList(mothersFirstName, patientSurname, districtName, formattedBirthDate, gender, twinOne);
                                        } else if (yes.equals(twinValue) && no.equals(firstBornValue)) {
                                            return Arrays.asList(mothersFirstName, patientSurname, districtName, formattedBirthDate, gender, twinTwo);
                                        }
                                        throw new RuntimeException("Please Answer both " + areYouTwin + " and " + areYouFirstBorn + " or neither.");
                                    }
                                    throw new RuntimeException("District of Birth field should have two characters at least");
                                }
                                throw new RuntimeException("Patient Last Name field should have two characters at least");
                            }
                            throw new RuntimeException("Mother's First Name field should have two characters at least");
                        }
                        throw new RuntimeException("Gender field should not be empty");
                    }
                    throw new RuntimeException("Date of Birth field should not be empty");
                }
                throw new RuntimeException("District of Birth field should not be empty");
            }
            throw new RuntimeException("Mother's First Name field should not be empty");
        }
        throw new RuntimeException("Patient Last Name field should not be empty");
    }

    private String getAttributeValue(String attribute, Patient patient) {
        PersonAttribute personAttribute = patient.getAttribute(attribute);
        if(personAttribute != null) {
            Concept concept = Context.getConceptService().getConcept(personAttribute.getValue());
            return concept.getName().getName();
        }
        return null;
    }

    private String getIdentifier(List<String> fields) {
        List<String> stringsToFormat = Arrays.asList(fields.get(0), fields.get(1), fields.get(2));
        String birthDate = fields.get(3);
        String gender = fields.get(4);
        String twin = fields.size() == requiredFieldsLengthWithTwins ? fields.get(5) : "";

        return getStringFromLastTwoLettersOfEachElement(stringsToFormat) + birthDate + gender + twin;
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
