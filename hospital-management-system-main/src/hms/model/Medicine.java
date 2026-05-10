package hms.model;

import java.io.Serializable;

public class Medicine implements Serializable {
    private String medicineId;
    private String name;
    private String manufacturer;
    private String category;
    private double price;
    private int quantity;
    private String dosage; // For prescriptions
    private String frequency; // For prescriptions
    private String duration; // For prescriptions
    private String description;

    // Main constructor for inventory
    public Medicine(String medicineId, String name, String manufacturer, double price, int quantity) {
        if (medicineId == null || medicineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be null or empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        this.medicineId = medicineId.trim();
        this.name = name.trim();
        this.manufacturer = manufacturer != null ? manufacturer.trim() : "";
        this.price = price;
        this.quantity = quantity;
        this.category = ""; // Initialize category
        this.description = ""; // Initialize description
    }

    // For prescription usage
    public Medicine(String medicineId, String name, String dosage, String frequency, String duration) {
        if (medicineId == null || medicineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be null or empty");
        }

        this.medicineId = medicineId.trim();
        this.name = name.trim();
        this.dosage = dosage != null ? dosage.trim() : "";
        this.frequency = frequency != null ? frequency.trim() : "";
        this.duration = duration != null ? duration.trim() : "";
        this.manufacturer = "";
        this.category = "";
        this.description = "";
        this.price = 0.0;
        this.quantity = 0;
    }

    // Getters and setters with validation
    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        if (medicineId == null || medicineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine ID cannot be null or empty");
        }
        this.medicineId = medicineId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer != null ? manufacturer.trim() : "";
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category != null ? category.trim() : "";
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage != null ? dosage.trim() : "";
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency != null ? frequency.trim() : "";
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration != null ? duration.trim() : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    public String toFileString() {
        return medicineId + "," +
                (name != null ? name.replaceAll(",", ";") : "") + "," +
                (manufacturer != null ? manufacturer.replaceAll(",", ";") : "") + "," +
                (category != null ? category.replaceAll(",", ";") : "") + "," +
                price + "," +
                quantity + "," +
                (description != null ? description.replaceAll(",", ";") : "");
    }

    // For prescription format
    public String toPrescriptionString() {
        return (name != null ? name : "") + "," +
                (dosage != null ? dosage : "") + "," +
                (frequency != null ? frequency : "") + "," +
                (duration != null ? duration : "");
    }

    @Override
    public String toString() {
        return "Medicine: " + name + ", Manufacturer: " + manufacturer +
                ", Price: " + price + ", Quantity: " + quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Medicine medicine = (Medicine) obj;
        return medicineId.equals(medicine.medicineId);
    }

    @Override
    public int hashCode() {
        return medicineId.hashCode();
    }
}
