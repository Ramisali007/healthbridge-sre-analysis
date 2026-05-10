package hms.model;

public class Nurse extends Person {
    private String department;
    private String shift;
    private String qualification;
    private double salary;

    // Constructor
    public Nurse(String id, String name, int age, String contact, String department, String shift) {
        super(id, name, age, contact);
        this.department = department;
        this.shift = shift;
    }

    // Full constructor
    public Nurse(String id, String name, int age, String contact, String email, String address,
                 String gender, String department, String shift, String qualification, double salary) {
        super(id, name, age, contact, email, address, gender);
        this.department = department;
        this.shift = shift;
        this.qualification = qualification;
        this.salary = salary;
    }

    // Getters and setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.salary = salary;
    }

    public String toFileString() {
        return id + "," + name + "," + age + "," + contact + "," + email + "," +
                address + "," + gender + "," + department + "," + shift + "," +
                qualification + "," + salary;
    }

    @Override
    public String toString() {
        return super.toString() + ", Department: " + department + ", Shift: " + shift;
    }
}
