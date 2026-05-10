package hms.service;

import hms.dao.PatientDAO;
import hms.interfaces.ManagementService;
import hms.model.Patient;
import java.util.List;

public class PatientService implements ManagementService<Patient, String> {

    private PatientDAO patientDAO;

    public PatientService() {
        patientDAO = new PatientDAO();
    }

    @Override
    public boolean add(Patient patient) {
        // Check if patient ID already exists
        if (patientDAO.exists(patient.getId())) {
            return false;
        }

        return patientDAO.save(patient);
    }

    @Override
    public Patient getById(String id) {
        return patientDAO.findById(id);
    }

    @Override
    public List<Patient> getAll() {
        return patientDAO.findAll();
    }

    @Override
    public boolean update(Patient patient) {
        if (!patientDAO.exists(patient.getId())) {
            return false;
        }

        return patientDAO.update(patient);
    }

    @Override
    public boolean delete(String id) {
        return patientDAO.delete(id);
    }

    @Override
    public List<Patient> search(String query) {
        return patientDAO.searchPatients(query);
    }

    // Additional methods
    public List<Patient> findByDisease(String disease) {
        return patientDAO.findByProperty("disease", disease);
    }

    // Generate a unique patient ID
    public String generatePatientId() {
        // Format: P + timestamp (simple approach)
        return "P" + System.currentTimeMillis();
    }
}

