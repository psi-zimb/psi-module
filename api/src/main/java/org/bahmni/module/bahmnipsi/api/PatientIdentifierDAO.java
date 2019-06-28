package org.bahmni.module.bahmnipsi.api;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PatientIdentifierDAO {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public int getNextSeqValue(String sequenceType) {
        String sql = "select next_seq_value from prep_oi_counter where seq_type = :sequenceType";
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(sql).setParameter("sequenceType", sequenceType);
        Object nextSeqValue = query.uniqueResult();
        if (null == nextSeqValue) {
            nextSeqValue = 0;
            initializeSequence(sequenceType, nextSeqValue);
        }
        return Integer.parseInt(nextSeqValue.toString());
    }

    private void initializeSequence(String sequenceType, Object nextSeqValue) {
        String insertSql = "insert into prep_oi_counter(seq_type, next_seq_value) values(:sequenceType, :nextSeqValue)";
        SQLQuery insertQuery = sessionFactory.getCurrentSession().createSQLQuery(insertSql);

        insertQuery
                .setParameter("sequenceType", sequenceType)
                .setParameter("nextSeqValue", nextSeqValue)
                .executeUpdate();
    }

    public int getIdentifierTypeId(String identifierType) {
        String sql = "select patientIdentifierTypeId from PatientIdentifierType WHERE name = :identifierType";

        Session session = sessionFactory.getCurrentSession();
        Object identifierTypeId = session.createQuery(sql).setParameter("identifierType", identifierType).uniqueResult();

        return Integer.parseInt(identifierTypeId.toString());
    }

    public void incrementSeqValueByOne(int seqValue, String sequenceType) {
        String sql = "update prep_oi_counter set next_seq_value = :nextValue where seq_type = :sequenceType";

        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(sql);
        query
                .setParameter("nextValue", seqValue + 1)
                .setParameter("sequenceType", sequenceType)
                .executeUpdate();
    }
}
