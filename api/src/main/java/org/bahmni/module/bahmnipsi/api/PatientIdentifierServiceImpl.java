package org.bahmni.module.bahmnipsi.api;

public class PatientIdentifierServiceImpl implements PatientIdentifierService {

    PatientIdentifierDAO patientIdentifierDAO;

    public void setPatientIdentifierDAO(PatientIdentifierDAO patientIdentifierDAO) {
        this.patientIdentifierDAO = patientIdentifierDAO;
    }

    @Override
    public int getCountOfPatients(String identifier) {
        return patientIdentifierDAO.getCountOfPatients(identifier);
    }

    @Override
    public int getNextSeqValue() {
        return patientIdentifierDAO.getNextSeqValue();
    }

    @Override
    public int getIdentifierTypeId(String identifierType) {
        return patientIdentifierDAO.getIdentifierTypeId(identifierType);
    }

    @Override
    public void incrementSeqValueByOne(int seqValue) {
        patientIdentifierDAO.incrementSeqValueByOne(seqValue);
    }
}
