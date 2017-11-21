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
}
