package org.bahmni.module.bahmnipsi.api;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class PatientIdentifierDAOTest {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Mock
    private SQLQuery sqlQuery;

    private PatientIdentifierDAO patientIdentifierDAO;
    private String identifier, regex;
    private String initArtService = "INIT_ART_SERVICE";;

    @Before
    public void setUp() {
        patientIdentifierDAO = new PatientIdentifierDAO();
        identifier = "test id";
        regex = "test id%";
        patientIdentifierDAO.setSessionFactory(sessionFactory);
    }

    @Test
    public void shouldGetNextSeqValue() {
        String sql = "select next_seq_value from prep_oi_counter where seq_type = :sequenceType";
        Integer nextVal = new Integer(3);

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createSQLQuery(sql)).thenReturn(sqlQuery);
        when(sqlQuery.setParameter("sequenceType", initArtService)).thenReturn(sqlQuery);
        when(sqlQuery.uniqueResult()).thenReturn(nextVal);

        int expectedVal = patientIdentifierDAO.getNextSeqValue(initArtService);

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createSQLQuery(sql);
        verify(sqlQuery, times(1)).setParameter("sequenceType", initArtService);
        verify(sqlQuery, times(1)).uniqueResult();
        Assert.assertEquals(3, expectedVal);
    }

    @Test
    public void shouldInitializeSequenceIfNotPresent() {
        String sql = "select next_seq_value from prep_oi_counter where seq_type = :sequenceType";
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createSQLQuery(sql)).thenReturn(sqlQuery);
        when(sqlQuery.setParameter("sequenceType", initArtService)).thenReturn(sqlQuery);
        when(sqlQuery.uniqueResult()).thenReturn(null);

        String insertSql = "insert into prep_oi_counter(seq_type, next_seq_value) values(:sequenceType, :nextSeqValue)";
        when(session.createSQLQuery(insertSql)).thenReturn(sqlQuery);
        when(sqlQuery.setParameter("sequenceType", initArtService)).thenReturn(sqlQuery);
        when(sqlQuery.setParameter("nextSeqValue", new Integer(0))).thenReturn(sqlQuery);
        when(sqlQuery.uniqueResult()).thenReturn(0);

        int expectedVal = patientIdentifierDAO.getNextSeqValue(initArtService);

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createSQLQuery(sql);
        verify(sqlQuery, times(1)).setParameter("sequenceType", initArtService);
        verify(sqlQuery, times(1)).uniqueResult();


        Assert.assertEquals(0, expectedVal);
    }

    @Test
    public void shouldGetIdentifierTypeId() {
        String sql = "select patientIdentifierTypeId from PatientIdentifierType WHERE name = :identifierType";
        String identifierType = "Prep/Oi";

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(sql)).thenReturn(query);
        when(query.setParameter("identifierType", identifierType)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new String("1"));

        int actualResults = patientIdentifierDAO.getIdentifierTypeId(identifierType);

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createQuery(sql);
        verify(query, times(1)).setParameter("identifierType", identifierType);
        verify(query, times(1)).uniqueResult();
        Assert.assertEquals(1, actualResults);
    }

    @Test
    public void shouldIncrementValueByOne() {
        String sql = "update prep_oi_counter set next_seq_value = :nextValue where seq_type = :sequenceType";
        int currentValue = 4;
        int nextVal = 5;

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createSQLQuery(sql)).thenReturn(sqlQuery);
        when(sqlQuery.setParameter("nextValue", nextVal)).thenReturn(query);
        when(query.setParameter("sequenceType", initArtService)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(nextVal);

        patientIdentifierDAO.incrementSeqValueByOne(currentValue, initArtService);

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createSQLQuery(sql);
        verify(sqlQuery, times(1)).setParameter("nextValue", nextVal);
        verify(query, times(1)).setParameter("sequenceType", initArtService);
        verify(query, times(1)).executeUpdate();
    }
}