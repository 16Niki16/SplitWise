# 💸 Splitwise-like Expense Sharing Application

## 📌 Overview

This project is a Java-based backend system inspired by Splitwise.  
It allows users to manage shared expenses, track debts, and settle balances within groups.

It was developed as a course project for the **Modern Java Technologies** course at FMI.

👉 Original project requirements: [Splitwise Course Project](https://github.com/fmi/java-course/blob/master/course-projects/splitwise.md)


---

## 🚀 Features

- ⚡ Server-client architecture with request/response handling (JSON format)
- 👤 User registration and authentication
- 👥 Group creation and management
- 💰 Expense tracking and splitting between users
- 📊 Debt calculation and balance tracking
- 💱 Real-time currency conversion via external API
- 🔔 Notification system for updates and changes
- 💾 Persistent storage:
  - File-based storage (JSON format)
  - Database support (JDBC-based)

---

## 🧠 Architecture & Design

The project is built using clean architecture principles:

- **Client-Server Model** – communication via structured requests and responses
- **Command Pattern** – each operation is implemented as a separate command
- **Service Layer** – business logic is separated from transport and persistence layers
- **Repository Pattern** – abstraction over data storage (files and database)
- **DTO-based communication** – all data is transferred using JSON (Jackson)
- **Extensible persistence layer** – supports both file and database implementations

---

## 📡 Data Format

All communication between client and server is done using **JSON**.
Additionally, file-based persistence also uses JSON serialization.

---

## 💾 Persistence Layer

The system supports two persistence mechanisms:

### 📁 File-based storage
- Data is stored in JSON files
- Used for users, groups, debts, and notifications

### 🗄️ Database storage (JDBC)
- Relational database integration (SQLite)
- Designed to replace or extend file-based storage

---

## 🧪 Testing

The project includes unit testing using:

- **JUnit 5** – test framework
- **Mockito** – mocking dependencies for isolated testing

---

## ⚙️ Technologies & Tools

- Java 21
- Java NIO
- JDBC (Database integration)
- Jackson (JSON processing)
- JUnit 5
- Mockito
- Lombok
- HTTP Client (external API integration)
- File I/O
