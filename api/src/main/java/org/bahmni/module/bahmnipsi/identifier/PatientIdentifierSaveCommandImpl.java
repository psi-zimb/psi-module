package org.bahmni.module.bahmnipsi.identifier;

import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

@Component
public class PatientIdentifierSaveCommandImpl implements EncounterDataPreSaveCommand {
    private final String reasonForVisit = "Reason for visit";
    private final String initialArt = "Initial ART service";
    private final String prepInitial = "PrEP Initial";
    private static final int TWO = 2;

    private PatientOiPrepIdentifier patientOiPrepIdentifier;

    @Autowired
    public PatientIdentifierSaveCommandImpl(PatientOiPrepIdentifier patientOiPrepIdentifier) {
        this.patientOiPrepIdentifier = patientOiPrepIdentifier;
    }

    @Override
    public BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction) {
        String patientUuid = bahmniEncounterTransaction.getPatientUuid();
        Collection<BahmniObservation> groupMembers = getVisitTypeObs(bahmniEncounterTransaction);

        String requiredObs = checkForArtPrepServiceObs(groupMembers);

        if(!requiredObs.isEmpty()) {
            if (requiredObs.equalsIgnoreCase(initialArt)) {
                try {
                    patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, "A", requiredObs);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (requiredObs.equalsIgnoreCase(prepInitial)) {
                try {
                    patientOiPrepIdentifier.updateOiPrepIdentifier(patientUuid, "P", requiredObs);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bahmniEncounterTransaction;
    }

    private String checkForArtPrepServiceObs(Collection<BahmniObservation> groupMembers) {
        int artPrepServiceCount = 0;
        String requiredObs = "";
        for (BahmniObservation member : groupMembers) {
            LinkedHashMap<String, String> value = (LinkedHashMap<String, String>) member.getValue();
            if (value != null && value.get("name").equals(initialArt)) {
                artPrepServiceCount++;
                requiredObs = initialArt;
            } else if(value != null && value.get("name").equals(prepInitial)) {
                artPrepServiceCount++;
                requiredObs = prepInitial;
            }
            if(artPrepServiceCount == TWO) {
                throw new RuntimeException("Both Initial Art Service and Prep Initial can not be selected at time. Please unselect one");
            }
        }

        return requiredObs;
    }

    private Collection<BahmniObservation> getVisitTypeObs(BahmniEncounterTransaction bahmniEncounterTransaction) {
        Collection<BahmniObservation> observations = bahmniEncounterTransaction.getObservations();
        for (BahmniObservation obs : observations) {
            Collection<BahmniObservation> groupMembers = obs.getGroupMembers();
            for (BahmniObservation member : groupMembers) {
                String visitType = member.getConcept().getName();

                if (visitType.equalsIgnoreCase(reasonForVisit)) {
                    return groupMembers;
                }
            }
        }

        return Collections.emptyList();
    }

    public void setPatientOiPrepIdentifier(PatientOiPrepIdentifier patientOiPrepIdentifier) {
        this.patientOiPrepIdentifier = patientOiPrepIdentifier;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}