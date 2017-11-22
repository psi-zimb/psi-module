package org.bahmni.module.bahmnipsi.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PatientIdentifierServiceImplTest {
    @Mock
    private PatientIdentifierDAO patientIdentifierDAO;

    @Test
    public void shouldCallGetCountOfPatientsOfPatientIdentifierDAO() {
        int expectedOutput = 1;
        String identifier = "test id";

        PatientIdentifierServiceImpl impl = new PatientIdentifierServiceImpl();
        impl.setPatientIdentifierDAO(patientIdentifierDAO);

        when(patientIdentifierDAO.getCountOfPatients(identifier)).thenReturn(expectedOutput);

        int actualOutput = impl.getCountOfPatients(identifier);

        Assert.assertEquals(expectedOutput, actualOutput);
        verify(patientIdentifierDAO, times(1)).getCountOfPatients(identifier);
    }
}
