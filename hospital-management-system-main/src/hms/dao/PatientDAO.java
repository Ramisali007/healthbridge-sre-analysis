package hms.dao;

import hms.model.Patient;
import java.util.List;

public class PatientDAO extends FileBasedDAO<Patient, String> {

    public PatientDAO() {
        super("data/patients.txt");
    }

    @Override
    protected Patient parseEntity(String line) {
        try {
            String[] data = line.split(",");
            // Handle basic constructor with minimal fields
            if (data.length == 5) {
                return new Patient(
                        data[0], // id
                        data[1], // name
                        Integer.parseInt(data[2]), // age
                        data[3], // contact
                        data[4]  // disease
                );
            }
            // Handle full constructor with all fields
            else if (data.length >= 10) {
                return new Patient(
                        data[0], // id
                        data[1], // name
                        Integer.parseInt(data[2]), // age
                        data[3], // contact
                        data[4], // email
                        data[5], // address
                        data[6], // gender
                        data[7], // bloodGroup
                        data[8], // allergies
                        data[9]  // disease
                );
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String entityToFileString(Patient patient) {
        return patient.toFileString();
    }

    @Override
    protected String getIdFromEntity(Patient patient) {
        return patient.getId();
    }

    @Override
    protected boolean matchesProperty(Patient patient, String propertyName, Object value) {
        if (value == null) return false;

        switch (propertyName.toLowerCase()) {
            case "id":
                return patient.getId().equals(value.toString());
            case "name":
                return patient.getName().toLowerCase().contains(value.toString().toLowerCase());
            case "age":
                if (value instanceof Integer) {
                    return patient.getAge() == (Integer) value;
                }
                try {
                    return patient.getAge() == Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    return false;
                }
            case "disease":
                return patient.getDisease() != null &&
                        patient.getDisease().toLowerCase().contains(value.toString().toLowerCase());
            case "contact":
                return patient.getContact() != null &&
                        patient.getContact().contains(value.toString());
            default:
                return false;
        }
    }

    // Additional methods specific to Patient
    public List<Patient> findByDisease(String disease) {
        return findByProperty("disease", disease);
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = query.toLowerCase();
        return findByPredicate(patient ->
                (patient.getId() != null && patient.getId().toLowerCase().contains(searchTerm)) ||
                        (patient.getName() != null && patient.getName().toLowerCase().contains(searchTerm)) ||
                        (patient.getDisease() != null && patient.getDisease().toLowerCase().contains(searchTerm)) ||
                        (patient.getContact() != null && patient.getContact().contains(searchTerm))
        );
    }
}
