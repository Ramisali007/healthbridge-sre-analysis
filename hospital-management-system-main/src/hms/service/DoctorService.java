package hms.service;

import hms.dao.DoctorDAO;
import hms.interfaces.ManagementService;
import hms.model.Doctor;
import java.util.List;

public class DoctorService implements ManagementService<Doctor, String> {

    private DoctorDAO doctorDAO;

    public DoctorService() {
        doctorDAO = new DoctorDAO();
    }

    @Override
    public boolean add(Doctor doctor) {
        // Check if doctor ID already exists
        if (doctorDAO.exists(doctor.getId())) {
            return false;
        }

        return doctorDAO.save(doctor);
    }

    @Override
    public Doctor getById(String id) {
        return doctorDAO.findById(id);
    }

    @Override
    public List<Doctor> getAll() {
        return doctorDAO.findAll();
    }

    @Override
    public boolean update(Doctor doctor) {
        if (!doctorDAO.exists(doctor.getId())) {
            return false;
        }

        return doctorDAO.update(doctor);
    }

    @Override
    public boolean delete(String id) {
        return doctorDAO.delete(id);
    }

    @Override
    public List<Doctor> search(String query) {
        return doctorDAO.searchDoctors(query);
    }

    // Additional methods
    public List<Doctor> findBySpecialization(String specialization) {
        return doctorDAO.findBySpecialization(specialization);
    }

    // Generate a unique doctor ID
    public String generateDoctorId() {
        // Format: D + timestamp (simple approach)
        return "D" + System.currentTimeMillis();
    }
}
