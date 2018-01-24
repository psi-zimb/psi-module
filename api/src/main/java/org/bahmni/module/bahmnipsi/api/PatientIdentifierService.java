package org.bahmni.module.bahmnipsi.api;

public interface PatientIdentifierService {
    int getCountOfPatients(String identifier);

    int getNextSeqValue();

    int getIdentifierTypeId(String identifierType);

    void incrementSeqValueByOne(int seqValue);
}