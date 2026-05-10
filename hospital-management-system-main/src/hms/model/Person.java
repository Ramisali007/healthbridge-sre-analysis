package hms.model;

import java.io.Serializable;

// Base class for all person entities in the system
public abstract class Person implements Serializable {
    protected String id;
    protected String name;
    protected String contact;
    protected String email;
    protected String address;
    protected int age;
    protected String gender;

    // Constructor with required fields
    public Person(String id, String name, int age, String contact) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.gender = ""; // Default value
        this.email = ""; // Default value
        this.address = ""; // Default value
    }

    // Full constructor
    public Person(String id, String name, int age, String contact, String email, String address, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.gender = gender;
    }

    // Getters and setters with validation
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120");
        }
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact information cannot be empty");
        }
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Age: " + age +
                ", Contact: " + contact + ", Gender: " + gender;
    }
}

