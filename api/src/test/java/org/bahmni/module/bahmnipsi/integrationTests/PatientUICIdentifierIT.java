package org.bahmni.module.bahmnipsi.integrationTests;

import org.bahmni.module.bahmnipsi.PatientTestData;
import org.bahmni.module.bahmnipsi.identifier.PatientUICIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Patient;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PatientUICIdentifierIT extends BaseModuleWebContextSensitiveTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private String areYouTwin = "Are you a twin?";
    private String areYouFirstBorn = "If yes, are you the firstborn?";

    PatientUICIdentifier patientUICIdentifier = new PatientUICIdentifier();

    @Test
    public void shouldUpdateIdentifier() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOEYD130170M", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldUpdateIdentifierWithSuffixT1IfThePatientIsTwinAndFirstBorn() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouTwin, "2146"));
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouFirstBorn, "2146"));

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOEYD130170MT1", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldUpdateIdentifierWithSuffixT2IfThePatientIsTwinAndFirstBornNo() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouTwin, "2146"));
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouFirstBorn, "2147"));

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOEYD130170MT2", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldThrowErrorIfThePatientIsTwinAndFirstBornIsNull() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouTwin, "2146"));

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowErrorIfThePatientIsNotATwinAndFirstBornIsYes() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouTwin, "2147"));
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouFirstBorn, "2146"));

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowErrorIfThePatientIsNotATwinAndFirstBornIsNo() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouTwin, "2147"));
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouFirstBorn, "2147"));

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldNotHaveTAsSuffixIfThePatientIsNotATwinAndFirstBornIsNull() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouTwin, "2147"));

        patientUICIdentifier.updateUICIdentifier(patient);

        assertEquals("ENOEYD130170M", patient.getPatientIdentifier("UIC").getIdentifier());
    }

    @Test
    public void shouldThrowErrorIfThePatientIsATwinIsNullAndFirstBornIsYes() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouFirstBorn, "2146"));

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }

    @Test
    public void shouldThrowErrorIfThePatientIsATwinIsNullAndFirstBornIsNo() throws Exception{
        executeDataSet("PatientIdentifier.xml");
        Patient patient = PatientTestData.setUpPatientData();
        patient.addAttribute(PatientTestData.getPersonAttribute(areYouFirstBorn, "2147"));

        exception.expect(RuntimeException.class);
        exception.expectMessage("Please Answer both "+areYouTwin+" and "+areYouFirstBorn+" or neither.");

        patientUICIdentifier.updateUICIdentifier(patient);
    }
}
