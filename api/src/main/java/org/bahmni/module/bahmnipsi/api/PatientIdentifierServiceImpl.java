package org.bahmni.module.bahmnipsi.api;

public class PatientIdentifierServiceImpl implements PatientIdentifierService {

    PatientIdentifierDAO patientIdentifierDAO;

    public void setPatientIdentifierDAO(PatientIdentifierDAO patientIdentifierDAO) {
        this.patientIdentifierDAO = patientIdentifierDAO;
    }

    @Override
    public int getNextSeqValue(String sequenceType) {
        return patientIdentifierDAO.getNextSeqValue(sequenceType);
    }

    @Override
    public int getIdentifierTypeId(String identifierType) {
        return patientIdentifierDAO.getIdentifierTypeId(identifierType);
    }

    @Override
    public void incrementSeqValueByOne(int seqValue, String sequenceType) {
        patientIdentifierDAO.incrementSeqValueByOne(seqValue, sequenceType);
    }

    @Override
    public int getLastSeqValue() {
        return patientIdentifierDAO.getNextSeqValue();
    }
}