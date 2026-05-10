package hms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicalRecord implements Serializable {
    private String recordId;
    private String patientId;
    private String doctorId;
    private Date recordDate;
    private String diagnosis;
    private String treatment;
    private List<Prescription> prescriptions;
    private List<String> testResults;
    private String notes;

    public MedicalRecord(String recordId, String patientId, String doctorId, Date recordDate) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.recordDate = recordDate;
        this.prescriptions = new ArrayList<>();
        this.testResults = new ArrayList<>();
    }

    // Getters and setters
    public String getRecordId() {
        return recordId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public List<Prescription> getPrescriptions() {
        return new ArrayList<>(prescriptions);
    }

    public void addPrescription(Prescription prescription) {
        if (prescription != null) {
            this.prescriptions.add(prescription);
        }
    }

    public List<String> getTestResults() {
        return new ArrayList<>(testResults);
    }

    public void addTestResult(String testResult) {
        if (testResult != null && !testResult.isEmpty()) {
            this.testResults.add(testResult);
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(recordId).append(",")
                .append(patientId).append(",")
                .append(doctorId).append(",")
                .append(recordDate).append(",")
                .append(diagnosis).append(",")
                .append(treatment).append(",")
                .append(notes);

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Record ID: " + recordId + ", Date: " + recordDate +
                ", Diagnosis: " + diagnosis;
    }
}

