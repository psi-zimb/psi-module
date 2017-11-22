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
}
