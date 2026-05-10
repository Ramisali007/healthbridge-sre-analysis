# 🏥 Hospital Management System

A comprehensive Java Swing-based Hospital Management System with file-based data storage, designed for efficient management of hospital operations including patient records, doctor schedules, pharmacy inventory, appointments, and billing.

**Developed by:**  
- **Zain Aftab FA24-BSE-155**  
- **Musfirah Qadeer FA24-BSE-101**  
- **Zuhaa Wasim FA24-BSE-119**
---
## 📋 Table of Contents

- [Features](#-features)
- [System Architecture](#-system-architecture)
- [Technologies Used](#-technologies-used)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Screenshots](#-screenshots)
- [API Documentation](#-api-documentation)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)


## ✨ Features

### 🔐 User Management

- **Multi-role Authentication**: Admin, Doctor, Nurse, Receptionist, Pharmacist
- **Secure Login System**: Username/password authentication
- **User Profile Management**: Add, edit, delete users
- **Role-based Access Control**: Different permissions for different roles


### 👥 Patient Management

- **Patient Registration**: Complete patient information management
- **Medical History**: Track patient medical records
- **Search & Filter**: Quick patient lookup functionality
- **Patient Reports**: Generate comprehensive patient reports


### 👨‍⚕️ Doctor Management

- **Doctor Profiles**: Manage doctor information and specializations
- **Schedule Management**: Track doctor availability
- **Consultation Fees**: Manage pricing information
- **Specialization Tracking**: Organize doctors by medical specialties


### 📅 Appointment System

- **Appointment Booking**: Schedule patient-doctor appointments
- **Time Slot Management**: Prevent double booking
- **Status Tracking**: Monitor appointment status (Scheduled, Completed, Cancelled)
- **Appointment History**: View past and upcoming appointments


### 💊 Pharmacy Management

- **Medicine Inventory**: Track medicine stock levels
- **Category Management**: Organize medicines by categories
- **Prescription Dispensing**: Manage medicine distribution
- **Low Stock Alerts**: Monitor inventory levels
- **Price Management**: Track medicine pricing


### 💰 Billing System

- **Invoice Generation**: Create detailed bills for services
- **Payment Tracking**: Monitor payment status
- **Multiple Bill Items**: Support for various services and medicines
- **Discount & Tax Calculation**: Flexible pricing options
- **PDF Bill Generation**: Professional invoice printing


### 📊 Reporting System

- **Patient Reports**: Comprehensive patient information
- **Billing Reports**: Financial transaction summaries
- **Inventory Reports**: Medicine stock status
- **PDF Export**: Professional report generation


## 🏗️ System Architecture

```plaintext
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Login Frame │ │ Dashboard   │ │ Management  │           │
│  │             │ │ Frame       │ │ Panels      │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Patient     │ │ Doctor      │ │ Medicine    │           │
│  │ Service     │ │ Service     │ │ Service     │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Data Access Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Patient     │ │ Doctor      │ │ Medicine    │           │
│  │ DAO         │ │ DAO         │ │ DAO         │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Data Storage Layer                       │
│           ┌─────────────┐ ┌─────────────┐                  │
│           │ Text Files  │ │ PDF Reports │                  │
│           │ (.txt)      │ │ (.pdf)      │                  │
│           └─────────────┘ └─────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Technologies Used

- **Java 8+**: Core programming language
- **Swing**: GUI framework for desktop application
- **File I/O**: Text file-based data persistence
- **iText PDF**: PDF generation for reports and bills
- **Maven**: Dependency management (optional)


## 📋 Prerequisites

Before running this application, make sure you have:

- **Java Development Kit (JDK) 8 or higher**
- **IDE** (IntelliJ IDEA, Eclipse, or NetBeans)
- **iText PDF Library** (included in project)


## 🚀 Installation

### Option 1: Clone Repository

```shellscript
# Clone the repository
git clone https://github.com/yourusername/hospital-management-system.git

# Navigate to project directory
cd hospital-management-system

# Compile the project
javac -cp ".:lib/*" src/hms/Main.java

# Run the application
java -cp ".:lib/*:src" hms.Main
```

### Option 2: Download ZIP

1. Download the project ZIP file
2. Extract to your desired location
3. Open in your preferred Java IDE
4. Add iText PDF library to classpath
5. Run `Main.java`


### Option 3: IDE Setup

1. **IntelliJ IDEA**:

1. File → Open → Select project folder
2. Add iText library to dependencies
3. Run Main class



2. **Eclipse**:

1. File → Import → Existing Projects into Workspace
2. Add iText JAR to Build Path
3. Run Main class





## 💻 Usage

### Default Login Credentials

```plaintext
Username: admin
Password: admin
Role: Administrator
```

### Getting Started

1. **Launch Application**: Run `Main.java`
2. **Login**: Use default credentials or create new users
3. **Navigate Dashboard**: Access different modules from the main menu
4. **Manage Data**: Add, edit, delete records as needed
5. **Generate Reports**: Create PDF reports for various entities


### Key Workflows

#### Adding a New Patient

1. Navigate to **Patient Management**
2. Click **Add Patient**
3. Fill in patient details
4. Click **Save**
5. Patient is added to the system


#### Booking an Appointment

1. Go to **Appointment Management**
2. Click **Book Appointment**
3. Select patient and doctor
4. Choose date and time
5. Confirm booking


#### Dispensing Medicine

1. Access **Pharmacy Management**
2. Click **Dispense Medicine**
3. Select patient and medicines
4. Set dosage and instructions
5. Complete dispensing


#### Generating Bills

1. Open **Billing Management**
2. Click **Generate Bill**
3. Select patient and add services
4. Apply discounts/taxes if needed
5. Generate PDF invoice


## 📁 Project Structure

```plaintext
HMSOOPProject/
├── src/
│   └── hms/
│       ├── dao/                    # Data Access Objects
│       │   ├── FileBasedDAO.java
│       │   ├── PatientDAO.java
│       │   ├── DoctorDAO.java
│       │   ├── MedicineDAO.java
│       │   └── UserDAO.java
│       ├── service/                # Business Logic Layer
│       │   ├── PatientService.java
│       │   ├── DoctorService.java
│       │   ├── MedicineService.java
│       │   └── UserService.java
│       ├── model/                  # Data Models
│       │   ├── Patient.java
│       │   ├── Doctor.java
│       │   ├── Medicine.java
│       │   ├── Appointment.java
│       │   ├── Billing.java
│       │   └── User.java
│       ├── ui/                     # User Interface
│       │   ├── LoginFrame.java
│       │   ├── DashboardFrame.java
│       │   ├── PatientManagementPanel.java
│       │   ├── DoctorManagementPanel.java
│       │   ├── PharmacyManagementPanel.java
│       │   ├── AppointmentManagementPanel.java
│       │   ├── BillingManagementPanel.java
│       │   └── ReportsPanel.java
│       ├── util/                   # Utility Classes
│       │   ├── ValidationUtils.java
│       │   ├── PDFGenerator.java
│       │   └── PasswordUtils.java
│       ├── interfaces/             # Interface Definitions
│       │   ├── DataAccessObject.java
│       │   ├── ManagementService.java
│       │   └── ReportGenerator.java
│       ├── exception/              # Custom Exceptions
│       │   ├── AuthenticationException.java
│       │   ├── DataAccessException.java
│       │   └── ValidationException.java
│       └── Main.java              # Application Entry Point
├── data/                          # Data Storage
│   ├── patients.txt
│   ├── doctors.txt
│   ├── medicines.txt
│   └── users.txt
├── reports/                       # Generated Reports
├── lib/                          # External Libraries
│   └── itextpdf-5.5.13.jar
├── logo.png                      # Application Logo
└── README.md
```

## 📚 API Documentation

### Core Classes

#### Patient Model

```java
public class Patient extends Person {
    private String bloodGroup;
    private String allergies;
    private String disease;
    private Date registrationDate;
    private List<MedicalRecord> medicalRecords;
}
```

#### Medicine Model

```java
public class Medicine {
    private String medicineId;
    private String name;
    private String manufacturer;
    private String category;
    private double price;
    private int quantity;
}
```

#### Service Layer Methods

```java
// PatientService
public boolean add(Patient patient)
public Patient getById(String id)
public List<Patient> getAll()
public boolean update(Patient patient)
public boolean delete(String id)
public List<Patient> search(String query)
```

### Data Access Layer

All DAO classes implement the `DataAccessObject<T, ID>` interface:

```java
public interface DataAccessObject<T, ID> {
    boolean save(T entity);
    T findById(ID id);
    List<T> findAll();
    List<T> findByProperty(String propertyName, Object value);
    boolean update(T entity);
    boolean delete(ID id);
    boolean exists(ID id);
    long count();
}
```

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**

```shellscript
git checkout -b feature/AmazingFeature
```


3. **Commit your changes**

```shellscript
git commit -m 'Add some AmazingFeature'
```


4. **Push to the branch**

```shellscript
git push origin feature/AmazingFeature
```


5. **Open a Pull Request**


### Contribution Guidelines

- Follow Java naming conventions
- Add comments for complex logic
- Include unit tests for new features
- Update documentation as needed
- Ensure code compiles without warnings


## 🐛 Known Issues

- File locking may occur with concurrent access
- Large datasets may impact performance
- PDF generation requires proper font installation


## 🔮 Future Enhancements

- Database integration (MySQL/PostgreSQL)
- Web-based interface
- Mobile application
- Cloud deployment support
- Advanced reporting with charts
- Integration with external systems
- Multi-language support
- Backup and restore functionality


## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```plaintext
MIT License

Copyright (c) 2025 Hospital Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📞 Contact

**Project Maintainer**: Zain Aftab

- **Email**: [zainaftab589@gmail.com](mailto:your.email@example.com)
- **GitHub**: [@ZainAftab-dev](https://github.com/yourusername)


**Project Link**: [https://github.com/ZainAftab-dev/hospital-management-system](https://github.com/yourusername/hospital-management-system)

---

## 🙏 Acknowledgments

- Thanks to the Java Swing community for excellent documentation
- iText team for the PDF generation library
- All contributors who helped improve this project
- Healthcare professionals who provided domain expertise


---

## ⭐ Show Your Support

If this project helped you, please give it a ⭐ on GitHub!

---

**Made with ❤️ for the healthcare community**
