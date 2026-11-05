========================================
HEALTHFIRST PHARMACY
PHARMACY INVENTORY MANAGEMENT SYSTEM
========================================

Project: Pharmacy Inventory Management System (PIMS)

Developed using:
- Java
- Java Swing/AWT
- JDBC
- MySQL
- MySQL Workbench

========================================
1. SYSTEM DESCRIPTION
========================================

HealthFirst Pharmacy Inventory Management System is a desktop
application developed to manage pharmacy inventory, suppliers,
users, sales and business reports.

The system provides separate access for Administrators and
Cashiers.

========================================
2. ADMINISTRATOR FEATURES
========================================

The Administrator can:

- Login to the system
- Add, update, view and delete medicines
- Manage suppliers
- Create and delete cashier accounts
- View sales reports
- View item-wise sales reports
- View low-stock reports
- View medicine expiry reports
- Print reports

========================================
3. CASHIER FEATURES
========================================

The Cashier can:

- Login to the system
- Search/check medicine availability
- Add medicines to the sales cart
- Process sales
- Calculate the total amount
- Generate a customer bill
- Print/save the bill
- Clear the shopping cart

Cashiers cannot manage medicines, suppliers or users.

========================================
4. DEFAULT LOGIN DETAILS
========================================

Administrator:
Username: admin
Password: admin123

Cashier:
Username: cashier
Password: cash123

========================================
5. DATABASE
========================================

Database name:

health_pims

The database contains the following tables:

1. users
2. suppliers
3. medicines
4. sales
5. sale_items

The database SQL script is included in:

database.sql

========================================
6. DATABASE SETUP
========================================

1. Install MySQL Server and MySQL Workbench.

2. Open MySQL Workbench.

3. Create/import the database using the supplied
   database.sql file.

4. Make sure the database is named:

   health_pims

5. Update the database connection details in DBConnection.java
   if the MySQL username or password is different.

========================================
7. DATABASE CONNECTION
========================================

The application uses JDBC to connect to MySQL.

Default connection:

Database:
health_pims

Host:
localhost

Port:
3306

Username:
root

========================================
8. PROJECT STRUCTURE
========================================

The project contains Java source files for:

- Login
- AdminDashboard
- CashierDashboard
- MedicinePanel
- SupplierPanel
- UserPanel
- ReportPanel
- DBConnection

========================================
9. REPORTS
========================================

The Administrator can generate the following reports:

- Sales Report
- Item-Wise Sales Report
- Low Stock Report
- Expiry Report

The reports can be printed.

========================================
10. SECURITY / ACCESS CONTROL
========================================

The system uses role-based login.

Administrators are redirected to the Admin Dashboard.

Cashiers are redirected to the Cashier Dashboard.

Cashiers do not have access to administrator functions.

========================================
11. TECHNOLOGIES USED
========================================

Programming Language:
Java

GUI:
Java Swing / AWT

Database:
MySQL

Database Connectivity:
JDBC

IDE:
____________________________

========================================
12. AUTHOR
========================================

Student Name:Botshelo
____________________________

Student Number:402411625
____________________________

Course:
PRO732

========================================
END OF README
========================================
