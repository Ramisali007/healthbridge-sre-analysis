package hms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Prescription implements Serializable {
    private String prescriptionId;
    private String patientId;
    private String doctorId;
    private Date issueDate;
    private List<Medicine> medicines;
    private String notes;
    private boolean dispensed;

    public Prescription(String prescriptionId, String patientId, String doctorId, Date issueDate) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.issueDate = issueDate;
        this.medicines = new ArrayList<>();
        this.dispensed = false;
    }

    // Getters and setters
    public String getPrescriptionId() {
        return prescriptionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public List<Medicine> getMedicines() {
        return new ArrayList<>(medicines);
    }

    public void addMedicine(Medicine medicine) {
        if (medicine != null) {
            this.medicines.add(medicine);
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isDispensed() {
        return dispensed;
    }

    public void setDispensed(boolean dispensed) {
        this.dispensed = dispensed;
    }

    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(prescriptionId).append(",")
                .append(patientId).append(",")
                .append(doctorId).append(",")
                .append(issueDate).append(",")
                .append(notes).append(",")
                .append(dispensed);

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Prescription ID: " + prescriptionId + ", Issue Date: " + issueDate +
                ", Medicines: " + medicines.size();
    }
}
