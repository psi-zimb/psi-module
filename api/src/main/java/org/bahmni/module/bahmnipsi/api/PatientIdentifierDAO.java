package org.bahmni.module.bahmnipsi.api;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.PatientIdentifier;

import java.util.List;

public class PatientIdentifierDAO {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public int getCountOfPatients(String id) {
        String regex = id+"%";
        String sql = "select identifier from PatientIdentifier where identifier like :identifierRegex";

        Session session = sessionFactory.getCurrentSession();
        List<PatientIdentifier> patientIdentifiers =  session.createQuery(sql).setParameter("identifierRegex", regex).list();
        return patientIdentifiers.size();
    }

    public int getNextSeqValue() {
        String sql = "select next_seq_value from prep_oi_counter";
        Session session = sessionFactory.getCurrentSession();
        Object nextSeqValue = session.createSQLQuery(sql).uniqueResult();

        return Integer.parseInt(nextSeqValue.toString());
    }

    public int getIdentifierTypeId(String identifierType) {
        String sql = "select patientIdentifierTypeId from PatientIdentifierType WHERE name = :identifierType";

        Session session = sessionFactory.getCurrentSession();
        Object identifierTypeId = session.createQuery(sql).setParameter("identifierType", identifierType).uniqueResult();

        return Integer.parseInt(identifierTypeId.toString());
    }

    public void incrementSeqValueByOne(int seqValue) {
        String sql = "update prep_oi_counter set next_seq_value = :nextValue";

        Session session = sessionFactory.getCurrentSession();
        session.createSQLQuery(sql).setParameter("nextValue", seqValue + 1).executeUpdate();
    }
}
