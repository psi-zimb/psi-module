package org.bahmni.module.bahmnipsi.identifier;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.webservices.services.IdentifierSourceServiceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Arrays;
import java.util.List;

@Component
public class PatientOiPrepIdentifier {
    private String identifierSource = "Prep/Oi Identifier";
    private String identifierType = "PREP/OI Identifier";
    private static final int oiPrepIdentifierLength = 5;
    private static final String defaultIdentifier = "Not Assigned";
    private final int affixIndex = 14;
    private final String initialArt = "Initial ART service";
    private final String prepInitial = "PrEP Initial";

    @Autowired
    private IdentifierSourceServiceWrapper identifierSourceServiceWrapper;

    public void setIdentifierToDefaultValue(Patient patient) {
        PatientIdentifier patientIdentifier = patient.getPatientIdentifier(identifierType);
        patientIdentifier.setIdentifier(defaultIdentifier);
    }

    public void updateOiPrepIdentifier(String patientUuid, String affix, String requiredObs) throws Exception {
        PatientIdentifier patientIdentifier = getPatientIdentifier(patientUuid);
        String oiPrepIdentifier = patientIdentifier.getIdentifier();

        if(oiPrepIdentifier.equals(defaultIdentifier)) {
            updateIdentifierByUsing(patientUuid, affix);
        }  else {
            char existedAffix = oiPrepIdentifier.charAt(affixIndex);
            if (existedAffix == 'A') {
                if (requiredObs.equalsIgnoreCase(prepInitial)) {
                    throw new RuntimeException("Can not change visit type from Initial Art Service to Prep Initial");
                }
            } else {
                if (requiredObs.equalsIgnoreCase(initialArt)) {
                    changeAffixPToA(patientIdentifier, oiPrepIdentifier);
                }
            }
        }
    }

    private void updateIdentifierByUsing(String patientUuid, String affix) throws Exception {
        List<String> requiredFields = getRequiredFields(patientUuid);
        int nextSeqValue = getNextSeqValue();
        String seqValueWithFiveChars = String.format("%0"+oiPrepIdentifierLength+"d", nextSeqValue);
        setIdentifier(patientUuid, requiredFields, affix, seqValueWithFiveChars);
        increaseIdentifierNextSeqValueByOne(nextSeqValue);
    }

    private List<String> getRequiredFields(String patientUuid) {
        Person personByUuid = Context.getPersonService().getPersonByUuid(patientUuid);
        PersonAddress personAddress = personByUuid.getPersonAddress();

        String provinceCode = getCode(personAddress.getStateProvince());
        String districtCode = getCode(personAddress.getCityVillage());
        String facilityCode = getCode(personAddress.getPostalCode());
        int year = Year.now().getValue();

        if(provinceCode != null && districtCode != null && facilityCode != null) {
            return Arrays.asList(provinceCode, districtCode, facilityCode, year + "");
        } else {
            throw new RuntimeException("Could not able to get required fields to generate Prep/Oi identifier");
        }
    }

    private String getCode(String region) {
        return StringUtils.substringBetween(region, "[", "]");
    }

    private int getNextSeqValue() throws Exception {
        String nextSeqValue = identifierSourceServiceWrapper.getSequenceValue(identifierSource);
        return Integer.parseInt(nextSeqValue);
    }

    private void setIdentifier(String patientUuid, List<String> requiredFields, String affix, String nextSeqValue) {
        PatientIdentifier patientIdentifier = getPatientIdentifier(patientUuid);
        patientIdentifier.setIdentifier(requiredFields.get(0)+"-"+requiredFields.get(1)+"-"+requiredFields.get(2)+"-"+requiredFields.get(3)+"-"+affix+"-"+nextSeqValue);
    }

    private PatientIdentifier getPatientIdentifier(String patientUuid) {
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        return patient.getPatientIdentifier(identifierType);
    }

    private void changeAffixPToA(PatientIdentifier patientIdentifier, String oiPrepIdentifier) {
        StringBuffer newIdentifier = new StringBuffer(oiPrepIdentifier);
        newIdentifier.setCharAt(affixIndex, 'A');
        patientIdentifier.setIdentifier(newIdentifier.toString());
    }

    public void decreaseIdentifierNextValueByOne() throws Exception {
        String sequenceValue = identifierSourceServiceWrapper.getSequenceValue(identifierSource);
        long nextId = Long.parseLong(sequenceValue);
        identifierSourceServiceWrapper.saveSequenceValue(nextId - 1, identifierSource);
    }

    private void increaseIdentifierNextSeqValueByOne(int nextValue) throws Exception {
        identifierSourceServiceWrapper.saveSequenceValue(nextValue + 1, identifierSource);
    }

    public void setIdentifierSourceServiceWrapper(IdentifierSourceServiceWrapper identifierSourceServiceWrapper) {
        this.identifierSourceServiceWrapper = identifierSourceServiceWrapper;
    }
}
