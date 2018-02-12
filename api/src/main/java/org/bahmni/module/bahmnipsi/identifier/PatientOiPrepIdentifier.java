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

    public void updateOiPrepIdentifier(String patientUuid, String affix) throws Exception {
        PatientIdentifier patientIdentifier = getPatientIdentifier(patientUuid);

        if(patientIdentifier == null) {
            updateIdentifierByUsing(patientUuid, affix);
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

    private void updateIdentifierByUsing(String patientUuid, String affix) throws Exception {
        List<String> requiredFields = getRequiredFields(patientUuid);
        int nextSeqValue = getNextSeqValue();
        String seqValueWithFiveChars = String.format("%0"+ oiPrepIdentifierSuffixLength +"d", nextSeqValue);
        PatientIdentifier patientIdentifier = createIdentifier();
        addIdentifier(patientUuid, patientIdentifier);
        setIdentifier(patientIdentifier, requiredFields, affix, seqValueWithFiveChars);
        incrementNextSeqValueByOne(nextSeqValue);
    }

    private List<String> getRequiredFields(String patientUuid) {
        Person personByUuid = Context.getPersonService().getPersonByUuid(patientUuid);
        PersonAddress personAddress = personByUuid.getPersonAddress();

        String provinceCode = getCode(personAddress.getStateProvince());
        String districtCode = getCode(personAddress.getCityVillage());
        String facilityCode = getCode(personAddress.getPostalCode());
        int year = Year.now().getValue();

        if(provinceCode != null && districtCode != null && facilityCode != null) {
            if(provinceCode.length() == codesLength && districtCode.length() == codesLength && facilityCode.length() == codesLength) {
                return Arrays.asList(provinceCode, districtCode, facilityCode, year + "");
            } else {
                throw new RuntimeException("Province, District and Facility code lengths must be 2");
            }
        } else {
            throw new RuntimeException("Province, District and Facility should not be empty on the Registration first page to generate Prep/Oi Identifier.");
        }
    }

    private String getCode(String region) {
        return StringUtils.substringBetween(region, "[", "]");
    }

    private int getNextSeqValue() throws Exception {
        try {
            return Context.getService(PatientIdentifierService.class).getNextSeqValue();
        } catch (Exception e) {
            throw new RuntimeException("Could not able to get next Sequence Value of the Prep/Oi Identifier");
        }
    }

    private void setIdentifier(PatientIdentifier patientIdentifier, List<String> requiredFields, String affix, String nextSeqValue) {
        patientIdentifier.setIdentifier(requiredFields.get(0)+"-"+requiredFields.get(1)+"-"+requiredFields.get(2)+"-"+requiredFields.get(3)+"-"+affix+"-"+nextSeqValue);
    }

    private void incrementNextSeqValueByOne(int currentSeqValue) {
        Context.getService(PatientIdentifierService.class).incrementSeqValueByOne(currentSeqValue);
    }

    private PatientIdentifier getPatientIdentifier(String patientUuid) {
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        return patient.getPatientIdentifier(identifierType);
    }

    private PatientIdentifier createIdentifier() {
        int identifierTypeId = Context.getService(PatientIdentifierService.class).getIdentifierTypeId(this.identifierType);
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        PatientIdentifierType identifierType = new PatientIdentifierType(identifierTypeId);
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
