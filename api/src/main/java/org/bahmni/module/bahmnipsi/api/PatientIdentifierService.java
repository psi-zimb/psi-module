package org.bahmni.module.bahmnipsi.api;

public interface PatientIdentifierService {

    int getNextSeqValue(String sequenceType);

    int getIdentifierTypeId(String identifierType);

    void incrementSeqValueByOne(int seqValue, String sequenceType);

    int getLastSeqValue();
}