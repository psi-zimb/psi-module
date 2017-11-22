package org.bahmni.module.bahmnipsi.api;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.PatientIdentifier;
import org.powermock.modules.junit4.PowerMockRunner;

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

    private PatientIdentifierDAO patientIdentifierDAO;
    private String identifier, regex, sql;

    @Before
    public void setUp() {
        patientIdentifierDAO = new PatientIdentifierDAO();
        identifier = "test id";
        regex = "test id%";
        sql = "select identifier from PatientIdentifier where identifier like :identifierRegex";
        patientIdentifierDAO.setSessionFactory(sessionFactory);
    }

    @Test
    public void shouldReturnZeroWhenThereAreNoPatientsWithTheGivenIdentifier() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(sql)).thenReturn(query);
        when(query.setParameter("identifierRegex", regex)).thenReturn(query);

        int count = patientIdentifierDAO.getCountOfPatients(identifier);
        Assert.assertEquals(0, count);
        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createQuery(sql);
        verify(query, times(1)).setParameter("identifierRegex", regex);
        verify(query, times(1)).list();
    }

    @Test
    public void shouldReturnPatientsCountWhenThereArePatients() {
        PatientIdentifier patientIdentifier1 = new PatientIdentifier();
        PatientIdentifier patientIdentifier2 = new PatientIdentifier();
        List<PatientIdentifier> patientIdentifiers = Arrays.asList(patientIdentifier1, patientIdentifier2);

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(sql)).thenReturn(query);
        when(query.setParameter("identifierRegex", regex)).thenReturn(query);
        when(query.list()).thenReturn(patientIdentifiers);

        int count = patientIdentifierDAO.getCountOfPatients(identifier);

        Assert.assertEquals(2, count);
        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).createQuery(sql);
        verify(query, times(1)).setParameter("identifierRegex", regex);
        verify(query, times(1)).list();
    }

}
