
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

Dưới đây là phần **chú thích hình ảnh (image captions)** được viết bằng tiếng Anh, phù hợp để bạn sử dụng trong README, báo cáo kỹ thuật, hoặc slide trình bày. Mỗi hình đều được đánh số và mô tả rõ ràng:

---

## 📸 Screenshots & Descriptions

### 👤 Staff Interface

* 
* 
* 
* 
* 
* 
* 
* 

### 🛠️ Admin/Manager Interface

* 
* 
* 
* 
* 

---

👉 Nếu bạn muốn mình **chèn hình mẫu với mã Markdown hoặc HTML**, hoặc **thiết kế layout trình bày hình ảnh trong báo cáo** (ví dụ: lưới 2 cột, slide, PDF export), hãy nói rõ nhé!


### 1. Staff Interface

![Image](https://github.com/user-attachments/assets/2a5811ed-d509-4a0b-9a3e-0b2ae088d1ea)

**Figure 1.** Login interface


<img width="566" height="683" alt="Image" src="https://github.com/user-attachments/assets/ed49e81f-0e17-49b4-b008-19a7dd7eafba" />

**Figure 2.** Change password screen


![Image](https://github.com/user-attachments/assets/46832e62-ea31-491f-90ac-7c1ed87eb988)

**Figure 3.** Pet management section interface


![Image](https://github.com/user-attachments/assets/43cd4371-2aab-49b0-8254-eeed52675c43)

**Figure 4.** Pet detail view


![Image](https://github.com/user-attachments/assets/a37625f0-84e5-480e-a011-8b238ede6c18)

**Figure 5.** Order screen after selecting products


![Image](https://github.com/user-attachments/assets/6f65e428-0cb0-4da4-adae-2f66179acbcc)

**Figure 6.** PDF invoice displayed after clicking "OK"


![Image](https://github.com/user-attachments/assets/a4bfc7e7-a561-4041-90d3-25f134fa1414)

**Figure 7.** Invoice detail view


![Image](https://github.com/user-attachments/assets/e559d49a-b707-4c21-b529-03c1d19c3e5c)

**Figure 8.** Personal information view

### 2. Manager Interface


![Image](https://github.com/user-attachments/assets/75a73037-2d02-42fe-a72a-53d6f20d4a78)

**Figure 9.** Statistical dashboard for managers


![Image](https://github.com/user-attachments/assets/74edcb5b-52b7-402a-9d05-1843574b9962)

**Figure 10.** Pet management screen


![Image](https://github.com/user-attachments/assets/3256f17e-26a6-4a9a-9985-341bee918fd2)

**Figure 11.** Detailed view of a specific pet


![Image](https://github.com/user-attachments/assets/b70be171-03d6-42cc-897c-5d8fc54d6edb)

**Figure 12.** Add new promotion interface


![Image](https://github.com/user-attachments/assets/daedbdd2-b105-4d92-9d6a-7867c31f0988)

**Figure 13.** Invoice management interface

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


