package hms.dao;

import hms.model.Doctor;
import java.util.List;

public class DoctorDAO extends FileBasedDAO<Doctor, String> {

    public DoctorDAO() {
        super("data/doctors.txt");
    }

    @Override
    protected Doctor parseEntity(String line) {
        try {
            String[] data = line.split(",");
            // Handle basic constructor
            if (data.length == 6) {
                return new Doctor(
                        data[0], // id
                        data[1], // name
                        Integer.parseInt(data[2]), // age
                        data[3], // contact
                        data[4], // specialization
                        data[5]  // availability
                );
            }
            // Handle full constructor
            else if (data.length >= 11) {
                return new Doctor(
                        data[0], // id
                        data[1], // name
                        Integer.parseInt(data[2]), // age
                        data[3], // contact
                        data[4], // email
                        data[5], // address
                        data[6], // gender
                        data[7], // specialization
                        data[8], // qualification
                        data[9], // availability
                        Double.parseDouble(data[10]) // consultationFee
                );
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String entityToFileString(Doctor doctor) {
        return doctor.toFileString();
    }

    @Override
    protected String getIdFromEntity(Doctor doctor) {
        return doctor.getId();
    }

    @Override
    protected boolean matchesProperty(Doctor doctor, String propertyName, Object value) {
        if (value == null) return false;

        switch (propertyName.toLowerCase()) {
            case "id":
                return doctor.getId().equals(value.toString());
            case "name":
                return doctor.getName().toLowerCase().contains(value.toString().toLowerCase());
            case "specialization":
                return doctor.getSpecialization() != null &&
                        doctor.getSpecialization().toLowerCase().contains(value.toString().toLowerCase());
            case "availability":
                return doctor.getAvailability() != null &&
                        doctor.getAvailability().toLowerCase().contains(value.toString().toLowerCase());
            default:
                return false;
        }
    }

    // Additional methods specific to Doctor
    public List<Doctor> findBySpecialization(String specialization) {
        return findByProperty("specialization", specialization);
    }

    public List<Doctor> searchDoctors(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = query.toLowerCase();
        return findByPredicate(doctor ->
                (doctor.getId() != null && doctor.getId().toLowerCase().contains(searchTerm)) ||
                        (doctor.getName() != null && doctor.getName().toLowerCase().contains(searchTerm)) ||
                        (doctor.getSpecialization() != null && doctor.getSpecialization().toLowerCase().contains(searchTerm))
        );
    }
}
