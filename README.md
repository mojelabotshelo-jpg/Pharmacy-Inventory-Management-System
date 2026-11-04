HealthFirst - Pharmacy Inventory Management System (PIMS)

A robust, desktop-based Pharmacy Inventory Management System built to streamline stock control, manage medication inventories, track supplier details, and automate administrative workflows for modern pharmacies and healthcare institutions.

📌 Features

Inventory Tracking: Real-time monitoring of pharmaceutical stock levels, batch numbers, and expiration dates.

Supplier & Order Management: Track vendor details, purchase orders, and stock receipts seamlessly.

Relational Database Integration: Powered by MySQL using mysql-connector-j for fast, secure, and structured data persistence.

User Management & Security: Built-in role-based access for pharmacists, administrators, and inventory staff.

Desktop Application: Packaged and configured for easy cross-platform desktop deployment.

🛠️ Built With

Language: Java (JDK 17+)

Database: MySQL

Database Driver: mysql-connector-j

Build Tool: Maven

🚀 Getting Started

Follow these instructions to get a local copy up and running for development and testing purposes.

Prerequisites

Make sure you have the following installed on your machine:

Java Development Kit (JDK 17 or higher)

MySQL Server

Apache Maven

Git

Installation & Setup

Clone the repository

git clone https://github.com/your-username/healthfirst-pims.git
cd healthfirst-pims


Configure the Database

Open your MySQL client (e.g., MySQL Workbench or CLI).

Create a new database and run the provided SQL setup script:

CREATE DATABASE healthfirst_db;
USE healthfirst_db;
-- Run the initialization scripts located in the /database directory


Configure Database Credentials
Update your database connection parameters (URL, Username, Password) in your application configuration file (e.g., db.properties or database config class).

Build the Project
Clean and build the project dependencies using Maven:

mvn clean install


Run the Application

mvn exec:java


📁 Repository Structure

healthfirst-pims/
├── src/                # Java source code and resources
├── database/           # SQL initialization and schema scripts
├── lib/                # Dependencies (e.g., MySQL connector)
├── pom.xml             # Maven configuration file
└── README.md           # Project documentation


🤝 Contributing

Contributions are welcome! If you find any issues or have ideas for improvement:

Fork the Project

Create your Feature Branch (git checkout -b feature/AmazingFeature)

Commit your Changes (git commit -m 'Add some AmazingFeature')

Push to the Branch (git checkout origin feature/AmazingFeature)

Open a Pull Request

📜 License

Distributed under the MIT License. See LICENSE for more information.

📧 Contact

Project Link: https://github.com/your-username/healthfirst-pims
