package org.bahmni.module.bahmnipsi.identifier;

public class PrepOiCounter {
    int id;
    int nextSeqValue;

    public PrepOiCounter(int id, int nextSeqValue) {
        this.id = id;
        this.nextSeqValue = nextSeqValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNextSeqValue() {
        return nextSeqValue;
    }

    public void setNextSeqValue(int nextSeqValue) {
        this.nextSeqValue = nextSeqValue;
    }
}
