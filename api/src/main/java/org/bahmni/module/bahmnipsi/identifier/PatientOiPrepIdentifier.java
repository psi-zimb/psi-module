package org.bahmni.module.bahmnipsi.identifier;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnipsi.api.PatientIdentifierService;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Arrays;
import java.util.List;

@Component
public class PatientOiPrepIdentifier {

    private String identifierType = "PREP/OI Identifier";
    private static final int oiPrepIdentifierSuffixLength = 5;
    private final int affixIndex = 14;
    private final int codesLength = 2;

    public void updateOiPrepIdentifier(String patientUuid, String affix, String sequenceType) throws Exception {
        PatientIdentifier patientIdentifier = getPatientIdentifier(patientUuid);

        if(patientIdentifier == null) {
            updateIdentifierByUsing(patientUuid, affix, sequenceType);
        }  else {
            String oiPrepIdentifier = patientIdentifier.getIdentifier();
            char existedAffix = oiPrepIdentifier.charAt(affixIndex);
            if (existedAffix == 'A' && affix.charAt(0) == 'P') {
                throw new RuntimeException("Can not change visit type from Initial Art Service to Prep Initial");
            } else if (existedAffix == 'P' && affix.charAt(0) == 'A') {
                changeAffixPToA(patientIdentifier, oiPrepIdentifier);
            }
        }
    }

    private void updateIdentifierByUsing(String patientUuid, String affix, String sequenceType) throws Exception {
        List<String> requiredFields = getRequiredFields(patientUuid);
        int nextSeqValue = getNextSeqValue(sequenceType);
        String seqValueWithFiveChars = String.format("%0"+ oiPrepIdentifierSuffixLength +"d", nextSeqValue);
        PatientIdentifier patientIdentifier = createIdentifier();
        addIdentifier(patientUuid, patientIdentifier);
        setIdentifier(patientIdentifier, requiredFields, affix, seqValueWithFiveChars);
        incrementNextSeqValueByOne(nextSeqValue, sequenceType);
    }

    private List<String> getRequiredFields(String patientUuid) {
        Person personByUuid = Context.getPersonService().getPersonByUuid(patientUuid);
        PersonAddress personAddress = personByUuid.getPersonAddress();

        if(personAddress == null) {
            throw new RuntimeException("Please fill address fields");
        }

        String province = personAddress.getStateProvince();
        String district = personAddress.getCityVillage();
        String facility = personAddress.getAddress2();

        String nullFields = getNullFields(province, district, facility);
        if(!nullFields.isEmpty()) {
            throw new RuntimeException(nullFields + " should not be empty on the Registration first page to generate Prep/Oi Identifier.");
        }

        String provinceCode = getCode(province);
        String districtCode = getCode(district);
        String facilityCode = getCode(facility);
        int year = Year.now().getValue();

        nullFields = getNullFields(provinceCode, districtCode, facilityCode);
        if(!nullFields.isEmpty()) {
            throw new RuntimeException("Please enter the " + nullFields + " code in the square brackets example 'MIDLANDS[07]' and code length must be 2");
        }

        if(provinceCode.length() != codesLength) {
            nullFields = " Province";
        }
        if(districtCode.length() != codesLength) {
            nullFields+= nullFields.isEmpty() ? "District" : ", District";
        }
        if(facilityCode.length() != codesLength) {
            nullFields+= nullFields.isEmpty() ? "Facility" : ", Facility";
        }

        if(!nullFields.isEmpty()) {
            throw new RuntimeException(nullFields +" code length must be 2");
        }
        return Arrays.asList(provinceCode, districtCode, facilityCode, year + "");
    }

    private String getNullFields(String province, String district, String facility) {
        String nullFields = "";

        if(province == null) {
            nullFields = "Province";
        }
        if(district == null) {
            nullFields+= nullFields.isEmpty() ? "District" : ", District";
        }
        if(facility == null) {
            nullFields+= nullFields.isEmpty() ? "Facility" : ", Facility";
        }
        return nullFields;
    }

    private String getCode(String region) {
        return StringUtils.substringBetween(region, "[", "]");
    }

    private int getNextSeqValue(String sequenceType) throws Exception {
        try {
            return Context.getService(PatientIdentifierService.class).getNextSeqValue(sequenceType);
        } catch (Exception e) {
            throw new RuntimeException("Could not able to get next Sequence Value of the Prep/Oi Identifier");
        }
    }

    private void setIdentifier(PatientIdentifier patientIdentifier, List<String> requiredFields, String affix, String nextSeqValue) {
        patientIdentifier.setIdentifier(requiredFields.get(0)+"-"+requiredFields.get(1)+"-"+requiredFields.get(2)+"-"+requiredFields.get(3)+"-"+affix+"-"+nextSeqValue);
    }

    private void incrementNextSeqValueByOne(int currentSeqValue, String sequenceType) {
        Context.getService(PatientIdentifierService.class).incrementSeqValueByOne(currentSeqValue, sequenceType);
    }

    private PatientIdentifier getPatientIdentifier(String patientUuid) {
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        return patient.getPatientIdentifier(identifierType);
    }

    private PatientIdentifier createIdentifier() {
        int identifierTypeId = Context.getService(PatientIdentifierService.class).getIdentifierTypeId(this.identifierType);
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        PatientIdentifierType identifierType = new PatientIdentifierType(identifierTypeId);
        identifierType.setName(this.identifierType);
        patientIdentifier.setIdentifierType(identifierType);
        return patientIdentifier;
    }

    private void addIdentifier(String patientUuid, PatientIdentifier patientIdentifier) {
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        patient.addIdentifier(patientIdentifier);
    }

    private void changeAffixPToA(PatientIdentifier patientIdentifier, String oiPrepIdentifier) {
        StringBuffer newIdentifier = new StringBuffer(oiPrepIdentifier);
        newIdentifier.setCharAt(affixIndex, 'A');
        patientIdentifier.setIdentifier(newIdentifier.toString());
    }
}
