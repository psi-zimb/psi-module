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
import org.openmrs.PatientIdentifier;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Before
    public void setUp() {
        patientIdentifierDAO = new PatientIdentifierDAO();
        identifier = "test id";
        regex = "test id%";
        patientIdentifierDAO.setSessionFactory(sessionFactory);
    }

    @Test
    public void shouldReturnZeroWhenThereAreNoPatientsWithTheGivenIdentifier() {
        String sql = "select identifier from PatientIdentifier where identifier like :identifierRegex";

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(sql)).thenReturn(query);
        when(query.setParameter("identifierRegex", regex)).thenReturn(query);

        int count = patientIdentifierDAO.getCountOfPatients(identifier);

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createQuery(sql);
        verify(query, times(1)).setParameter("identifierRegex", regex);
        verify(query, times(1)).list();
        Assert.assertEquals(0, count);
    }

    @Test
    public void shouldReturnPatientsCountWhenThereArePatients() {
        String sql = "select identifier from PatientIdentifier where identifier like :identifierRegex";
        PatientIdentifier patientIdentifier1 = new PatientIdentifier();
        PatientIdentifier patientIdentifier2 = new PatientIdentifier();
        List<PatientIdentifier> patientIdentifiers = Arrays.asList(patientIdentifier1, patientIdentifier2);

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(sql)).thenReturn(query);
        when(query.setParameter("identifierRegex", regex)).thenReturn(query);
        when(query.list()).thenReturn(patientIdentifiers);

        int count = patientIdentifierDAO.getCountOfPatients(identifier);

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createQuery(sql);
        verify(query, times(1)).setParameter("identifierRegex", regex);
        verify(query, times(1)).list();
        Assert.assertEquals(2, count);
    }

    @Test
    public void shouldGetNextSeqValue() {
        String sql = "select next_seq_value from prep_oi_counter";
        Integer nextVal = new Integer(3);

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createSQLQuery(sql)).thenReturn(sqlQuery);
        when(sqlQuery.uniqueResult()).thenReturn(nextVal);

        int expectedVal = patientIdentifierDAO.getNextSeqValue();

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createSQLQuery(sql);
        verify(sqlQuery, times(1)).uniqueResult();
        Assert.assertEquals(3, expectedVal);
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
        String sql = "update prep_oi_counter set next_seq_value = :nextValue";
        int currentValue = 4;
        int nextVal = 5;

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createSQLQuery(sql)).thenReturn(sqlQuery);
        when(sqlQuery.setParameter("nextValue", nextVal)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(nextVal);

        patientIdentifierDAO.incrementSeqValueByOne(currentValue);

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createSQLQuery(sql);
        verify(sqlQuery, times(1)).setParameter("nextValue", nextVal);
        verify(query, times(1)).executeUpdate();
    }
}
