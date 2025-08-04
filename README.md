
# Pet Store Management System – Four Little Paws
## I. Project Overview

The project **"Pet Store Management Application – Four Little Paws"** aims to develop a software application that facilitates the management of pets, orders, customers, and products in pet stores.

The system helps employees and managers track store operations **more efficiently, accurately, and conveniently** than traditional manual methods. It is designed using **Object-Oriented Programming (OOP)** principles with a user-friendly interface and clearly defined functionalities.

### Objectives

* Optimize store operations and workflows.
* Save time and minimize human errors.
* Improve customer service quality amid increasing demand for pet care services.

---

## II. Main Features

### 1. **Staff Functions**

* Login and password recovery.
* Edit personal account information.
* Create orders:

  * View inventory.
  * Add products to orders.
  * Edit order details.
  * Confirm order completion.
  * Export PDF invoices.
  * Cancel orders.
* View invoice history.
* Logout.

### 2. **Manager Functions**

* Login.
* Account management: search, add, view details.
* Customer management: search, add, view details.
* Pet & product management: search, add, view details.
* Promotion management: search, add, view details.
* View statistics: number of orders, daily/monthly/yearly revenue.
* Logout.

---

## III. Technologies Used

| Category               | Technology    |
| ---------------------- | ------------- |
| IDE                    | IntelliJ IDEA |
| Programming Language   | Java          |
| UI Framework           | JavaFX        |
| Font                   | Inter         |
| Database               | MySQL         |
| DB Connection          | JDBC          |
| Diagram Design         | Draw\.io      |
| Source Code Management | GitHub        |

---

## IV. Project Structure

```
src/
 └── main/
     ├── java/com/store/app/petstore/
     │   ├── Controllers/
     │   │   ├── Admin/              # Controllers for admin dashboard UI actions
     │   │   └── Staff/              # Controllers for staff dashboard UI actions
     │   ├── DAO/                    # Data Access Objects to handle database operations
     │   │   └── StatisticDAO/       # Specialized DAO classes for statistical data
     │   ├── Models/                 # Data models and related logic
     │   │   ├── Entities/           # Database entity classes mapped to tables (e.g., User, Product)
     │   │   ├── Records/            # Java record types for lightweight data handling
     │   │   └── Seeder/             # Classes used to seed sample/demo data into the database
     │   ├── Sessions/               # Manages user sessions and login status
     │   └── Utils/                  # Utility/helper classes
     │       └── Mappers/           # Classes that map SQL result sets to Java models
     └── resources/
         ├── Font/                   # Custom fonts used in the application (e.g., Inter)
         ├── FXML/                   # FXML files defining UI layout for JavaFX
         │   ├── Admin/              # FXML files for admin dashboard (e.g., user management, reports)
         │   ├── Staff/              # FXML files for staff dashboard (e.g., order management)
         │   └── Statistics/         # FXML files for statistics and charts UI
         ├── Images/                 # Image assets used in the app
         │   ├── Pet/                # Images of pets
         │   ├── Product/            # Images of products
         │   ├── Staff/              # Profile images of staff members
         │   └── User/               # Profile images of customers/users
         └── Styles/                 # CSS stylesheets for customizing JavaFX UI appearance
             ├── Admin/              # Stylesheets specific to the admin dashboard
             └── Staff/              # Stylesheets specific to the staff dashboard

```

---

## V. Input and Output

![Image](https://github.com/user-attachments/assets/e2aaa436-1350-4e2c-a094-77eb3d6aa1f4)

---

## VI. Demo Screenshots

### 1. Staff Interface

![Image](https://github.com/user-attachments/assets/2a5811ed-d509-4a0b-9a3e-0b2ae088d1ea)

<p align="center">
  <img src="images/login.png" alt="Login Interface" width="600"/>
  <br/>
  <strong>Figure 1. Login interface</strong>
</p>

<img width="566" height="683" alt="Image" src="https://github.com/user-attachments/assets/ed49e81f-0e17-49b4-b008-19a7dd7eafba" />

<p align="center">
  <img src="images/change-password.png" alt="Change Password" width="600"/>
  <br/>
  <strong>Figure 2. Change password screen</strong>
</p>

![Image](https://github.com/user-attachments/assets/46832e62-ea31-491f-90ac-7c1ed87eb988)

<p align="center">
  <img src="images/pet-section.png" alt="Pet Section" width="600"/>
  <br/>
  <strong>Figure 3. Pet management section interface</strong>
</p>

![Image](https://github.com/user-attachments/assets/43cd4371-2aab-49b0-8254-eeed52675c43)

<p align="center">
  <img src="images/pet-detail.png" alt="Pet Details" width="600"/>
  <br/>
  <strong>Figure 4. Pet detail view</strong>
</p>

![Image](https://github.com/user-attachments/assets/a37625f0-84e5-480e-a011-8b238ede6c18)

<p align="center">
  <img src="images/order.png" alt="Order Screen" width="600"/>
  <br/>
  <strong>Figure 5. Order screen after selecting products</strong>
</p>

![Image](https://github.com/user-attachments/assets/6f65e428-0cb0-4da4-adae-2f66179acbcc)

<p align="center">
  <img src="images/pdf-invoice.png" alt="PDF Invoice" width="600"/>
  <br/>
  <strong>Figure 6. PDF invoice displayed after clicking OK</strong>
</p>

![Image](https://github.com/user-attachments/assets/a4bfc7e7-a561-4041-90d3-25f134fa1414)

<p align="center">
  <img src="images/invoice-detail.png" alt="Invoice Detail" width="600"/>
  <br/>
  <strong>Figure 7. Invoice detail view</strong>
</p>

![Image](https://github.com/user-attachments/assets/e559d49a-b707-4c21-b529-03c1d19c3e5c)

<p align="center">
  <img src="images/personal-info.png" alt="Personal Info" width="600"/>
  <br/>
  <strong>Figure 8. Personal information view</strong>
</p>

### 2. Manager Interface

![Image](https://github.com/user-attachments/assets/75a73037-2d02-42fe-a72a-53d6f20d4a78)

<p align="center">
  <img src="images/stats-dashboard.png" alt="Statistics Dashboard" width="600"/>
  <br/>
  <strong>Figure 9. Statistical dashboard for managers</strong>
</p>

![Image](https://github.com/user-attachments/assets/74edcb5b-52b7-402a-9d05-1843574b9962)

<p align="center">
  <img src="images/admin-pet.png" alt="Pet Management" width="600"/>
  <br/>
  <strong>Figure 10. Pet management screen</strong>
</p>

![Image](https://github.com/user-attachments/assets/3256f17e-26a6-4a9a-9985-341bee918fd2)

<p align="center">
  <img src="images/pet-detail-admin.png" alt="Pet Detail View" width="600"/>
  <br/>
  <strong>Figure 11. Detailed view of a specific pet</strong>
</p>

![Image](https://github.com/user-attachments/assets/b70be171-03d6-42cc-897c-5d8fc54d6edb)

<p align="center">
  <img src="images/add-promo.png" alt="Add Promotion" width="600"/>
  <br/>
  <strong>Figure 12. Add new promotion interface</strong>
</p>

![Image](https://github.com/user-attachments/assets/daedbdd2-b105-4d92-9d6a-7867c31f0988)

<p align="center">
  <img src="images/invoice-admin.png" alt="Invoice Management" width="600"/>
  <br/>
  <strong>Figure 13. Invoice management interface</strong>
</p>

---

## VII. Project Installation Guide

### Prerequisites

Before you begin, make sure the following tools are installed on your machine:

1. **Java Development Kit (JDK)**

  * Version: **21 or higher**
  * Download: [https://jdk.java.net/](https://jdk.java.net/)

2. **Apache Maven**

  * Build tool used to manage dependencies and build the project
  * Download: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

3. **Database Setup**

  * You can use either:

    * **MySQL** (recommended for ease of integration)
    * **Oracle** (optional)
  * Create a database named (e.g., `petstore_db`) and update connection credentials in the environment file.

4. **Environment Configuration**

  * Create a `.env` file in the root directory to store database connection details.
  * The project uses `dotenv-java` to load environment variables.

   Example `.env` file:

   ```env
   DB_URL=jdbc:mysql://localhost:3306/petstore_db
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```

---

### Clone the Repository

```bash
git clone <repository_url>
cd PBL3-Petstore
```

> Replace `<repository_url>` with the actual URL of the GitHub repository.

---

### Build the Project with Maven

Make sure you're inside the root project directory (`PBL3-Petstore`), then run:

```bash
mvn clean package
```

This will compile the code and resolve all dependencies.

---

###  Run the Application

You can launch the application using the following Maven command:

```bash
mvn clean javafx:run
```

> Make sure your system supports JavaFX runtime or set it up via `--module-path` if needed.

---

## VIII. Dependencies Used

This project uses the following key libraries and dependencies:

| Dependency        | Purpose                                                                 |
| ----------------- | ----------------------------------------------------------------------- |
| **JavaFX**        | Main library for building graphical user interfaces                     |
| **ControlsFX**    | UI controls enhancement for JavaFX                                      |
| **FormsFX**       | Building forms quickly and declaratively in JavaFX                      |
| **Ikonli-javafx** | Icon packs for JavaFX applications (e.g., FontAwesome, Material Design) |
| **BootstrapFX**   | Bootstrap-style theming for JavaFX                                      |
| **TilesFX**       | JavaFX-based tiles for dashboards and statistics                        |
| **JDBC Driver**   | Connects to either MySQL or Oracle databases                            |
| **jbcrypt**       | Secure password hashing and checking                                    |
| **dotenv-java**   | Loads environment variables from a `.env` file                          |


