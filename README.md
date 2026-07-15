# Placement Management System

A Java Swing-based GUI application for managing student placements, company drives, applications, and logs, built with FlatLaf styling and MySQL database.

---

## Setup Instructions (for you and your friend)

To run this application on your machine, please follow these steps:

### 1. Prerequisites
Make sure you have the following installed:
*   **Java Development Kit (JDK) 17** or higher.
*   **Apache Maven** (to build and run the project).
*   **MySQL Server** (running locally on port `3306`).

---

### 2. Database Setup
You need to import the database schema and sample data.

1.  Open your MySQL terminal or database client (like MySQL Workbench, DBeaver, or command line).
2.  Login with your MySQL credentials.
3.  Run/import the script `recreate_schema_and_seed.sql` located in the root of this project.
    *   **Via Command Line:**
        ```bash
        mysql -u root -p < recreate_schema_and_seed.sql
        ```
    *   **Via MySQL Workbench / DBeaver:**
        Open the file `recreate_schema_and_seed.sql` in the query editor and run/execute the entire script.

This will automatically create a database named `placement_management_system` and populate all the necessary tables with initial mock/seed data.

---

### 3. Database Configuration
You need to configure the database credentials to match your local MySQL settings.

1.  Go to `src/main/resources/` directory.
2.  Copy/Duplicate `db.properties.example` and rename it to `db.properties`.
3.  Open `db.properties` in a text editor and update your username and password:
    ```properties
    db.url=jdbc:mysql://localhost:3306/placement_management_system
    db.username=YOUR_MYSQL_USERNAME (usually root)
    db.password=YOUR_MYSQL_PASSWORD (replace with your MySQL root password)
    ```

> **Note:** The `db.properties` file is git-ignored (`.gitignore`) to keep credentials private. This is why it was not present when cloning/extracting if you didn't create it manually.

---

### 4. Build and Run the Application
You can run the application either using an IDE or from the command line.

#### Method A: Command Line (Maven)
In the root directory of the project, run:
```bash
# Compile and run the Main application
mvn clean compile exec:java -Dexec.mainClass="com.placement.Main"
```

#### Method B: In an IDE (VS Code / IntelliJ IDEA / Eclipse)
1.  Open the project folder in your IDE.
2.  Let the IDE load Maven dependencies.
3.  Navigate to `src/main/java/com/placement/Main.java`.
4.  Right-click and select **Run** / **Debug**.

---

## Default Login Credentials
You can log in using the following accounts:
*   **Admin:** Username: `admin1` | Password: `admin123`
*   **Student:** Username: `shashank` | Password: `pass123`
*   **Recruiter:** Username: `tcs_recruiter` | Password: `pass123`
