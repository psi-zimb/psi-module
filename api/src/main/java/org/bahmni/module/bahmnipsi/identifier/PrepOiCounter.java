package org.bahmni.module.bahmnipsi.identifier;

public class PrepOiCounter {
    int id;
    int nextSeqValue;
    String seqType;

    public PrepOiCounter(int id, String seqType, int nextSeqValue) {
        this.id = id;
        this.seqType = seqType;
        this.nextSeqValue = nextSeqValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeqType() {
        return seqType;
    }

    public void setSeqType(String seqType) {
        this.seqType = seqType;
    }

    public int getNextSeqValue() {
        return nextSeqValue;
    }

    public void setNextSeqValue(int nextSeqValue) {
        this.nextSeqValue = nextSeqValue;
    }
}
