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
    public void shouldGetNextValue() {
        int expectedOutput = 2;

        when(patientIdentifierDAO.getNextSeqValue("INIT_ART_SERVICE")).thenReturn(2);

        int actualOutput = impl.getNextSeqValue("INIT_ART_SERVICE");

        verify(patientIdentifierDAO, times(1)).getNextSeqValue("INIT_ART_SERVICE");
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
        doNothing().when(patientIdentifierDAO).incrementSeqValueByOne(seqValue, "INIT_ART_SERVICE");

        impl.incrementSeqValueByOne(seqValue, "INIT_ART_SERVICE");
        verify(patientIdentifierDAO, times(1)).incrementSeqValueByOne(seqValue, "INIT_ART_SERVICE");
    }

}
