package hms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Billing implements Serializable {
    private String billId;
    private String patientId;
    private Date billDate;
    private double totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private List<BillItem> items;
    private double discount;
    private double tax;

    public Billing(String billId, String patientId, Date billDate) {
        this.billId = billId;
        this.patientId = patientId;
        this.billDate = billDate;
        this.totalAmount = 0.0;
        this.paymentStatus = "UNPAID";
        this.items = new ArrayList<>();
        this.discount = 0.0;
        this.tax = 0.0;
    }

    public Billing(String patientId, double totalAmount, String paymentStatus) {
        this.billId = generateBillId();
        this.patientId = patientId;
        this.billDate = new Date();
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.items = new ArrayList<>();
        this.discount = 0.0;
        this.tax = 0.0;
    }

    private String generateBillId() {
        return "BILL-" + System.currentTimeMillis();
    }

    // Method to add an item and update the total amount
    public void addItem(BillItem item) {
        if (item != null) {
            items.add(item);
            calculateTotal();
        }
    }

    // Calculate the total bill amount
    private void calculateTotal() {
        double subtotal = 0.0;
        for (BillItem item : items) {
            subtotal += item.getAmount();
        }

        // Apply discount
        double discountAmount = subtotal * (discount / 100.0);

        // Apply tax
        double taxAmount = subtotal * (tax / 100.0);

        totalAmount = subtotal - discountAmount + taxAmount;
    }

    // Getters and setters
    public String getBillId() {
        return billId;
    }

    public String getPatientId() {
        return patientId;
    }

    public Date getBillDate() {
        return billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<BillItem> getItems() {
        return new ArrayList<>(items);
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        this.discount = discount;
        calculateTotal();
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        if (tax < 0) {
            throw new IllegalArgumentException("Tax cannot be negative");
        }
        this.tax = tax;
        calculateTotal();
    }

    public String toFileString() {
        return billId + "," + patientId + "," + billDate + "," + totalAmount + "," +
                paymentStatus + "," + paymentMethod + "," + discount + "," + tax;
    }

    @Override
    public String toString() {
        return "Bill ID: " + billId + ", Patient ID: " + patientId +
                ", Date: " + billDate + ", Amount: " + totalAmount +
                ", Status: " + paymentStatus;
    }

    // Inner class for bill items
    public static class BillItem implements Serializable {
        private String description;
        private double amount;
        private int quantity;

        public BillItem(String description, double amount, int quantity) {
            this.description = description;
            this.amount = amount * quantity;
            this.quantity = quantity;
        }

        public String getDescription() {
            return description;
        }

        public double getAmount() {
            return amount;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return quantity > 0 ? amount / quantity : amount;
        }

        @Override
        public String toString() {
            return description + " x " + quantity + " = " + amount;
        }
    }
}

