package hms.model;

import java.io.Serializable;
import java.util.Date;

public class Appointment implements Serializable {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private Date appointmentDate;
    private String timeSlot;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    private String description;
    private double fee;
    private boolean isPaid;

    public Appointment(String appointmentId, String patientId, String doctorId, Date appointmentDate, String timeSlot) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.status = "SCHEDULED";
        this.description = "";
        this.fee = 0.0;
        this.isPaid = false;
    }

    public Appointment(String appointmentId, String patientId, String doctorId, String dateString, String timeSlot, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        // Parse date from string in real implementation
        this.appointmentDate = new Date();
        this.timeSlot = timeSlot;
        this.status = status;
        this.description = "";
        this.fee = 0.0;
        this.isPaid = false;
    }

    // Getters and setters
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String toFileString() {
        // Format date to string in real implementation
        return appointmentId + "," + patientId + "," + doctorId + "," + appointmentDate + "," +
                timeSlot + "," + status + "," + description + "," + fee + "," + isPaid;
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentId + ", Patient ID: " + patientId +
                ", Doctor ID: " + doctorId + ", Date: " + appointmentDate +
                ", Status: " + status;
    }
}
