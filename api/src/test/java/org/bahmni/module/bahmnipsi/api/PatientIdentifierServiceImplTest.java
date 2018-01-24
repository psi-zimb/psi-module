package org.bahmni.module.bahmnipsi.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class PatientIdentifierServiceImplTest {
    @Mock
    private PatientIdentifierDAO patientIdentifierDAO;

    PatientIdentifierServiceImpl impl;

    @Before
    public void setUp() {
        impl = new PatientIdentifierServiceImpl();
        impl.setPatientIdentifierDAO(patientIdentifierDAO);
    }

    @Test
    public void shouldCallGetCountOfPatientsOfPatientIdentifierDAO() {
        int expectedOutput = 1;
        String identifier = "test id";

        when(patientIdentifierDAO.getCountOfPatients(identifier)).thenReturn(expectedOutput);

        int actualOutput = impl.getCountOfPatients(identifier);

        verify(patientIdentifierDAO, times(1)).getCountOfPatients(identifier);
        Assert.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void shouldGetNextValue() {
        int expectedOutput = 2;

        when(patientIdentifierDAO.getNextSeqValue()).thenReturn(2);

        int actualOutput = impl.getNextSeqValue();

        verify(patientIdentifierDAO, times(1)).getNextSeqValue();
        Assert.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void shouldGetIdOfTheGivenIdentifierType() {
        int expected = 5;
        String identifierType = "Prep/Oi identifier";

        when(patientIdentifierDAO.getIdentifierTypeId(identifierType)).thenReturn(expected);

        int actual = impl.getIdentifierTypeId(identifierType);

        verify(patientIdentifierDAO, times(1)).getIdentifierTypeId(identifierType);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldCallDAOIncrementMethod() {
        int seqValue = 2;
        doNothing().when(patientIdentifierDAO).incrementSeqValueByOne(seqValue);

        impl.incrementSeqValueByOne(seqValue);
        verify(patientIdentifierDAO, times(1)).incrementSeqValueByOne(seqValue);
    }

}
