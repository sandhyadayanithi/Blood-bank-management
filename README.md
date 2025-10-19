# 🩸 Blood Bank Management System

A comprehensive Java-based distributed application for managing blood bank operations across multiple locations. This system enables seamless communication between admins managing inventory and clients requesting blood donations.

---

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Project Architecture](#project-architecture)
- [System Requirements](#system-requirements)
- [Installation & Setup](#installation--setup)
- [Usage Guide](#usage-guide)
- [File Structure](#file-structure)
- [Key Components](#key-components)
- [How It Works](#how-it-works)
- [Data Formats](#data-formats)
- [Future Enhancements](#future-enhancements)

---

## 🎯 Overview

This Blood Bank Management System is designed to optimize blood distribution across multiple blood banks in different locations. It connects blood bank administrators with clients requesting blood through a socket-based networking model, ensuring efficient inventory management and rapid blood allocation.

**Perfect for:** Hospitals, blood banks, donation centers, and healthcare networks managing multiple locations.

---

## ✨ Features

### Admin Features
- **Dashboard Management** - View all blood banks and their current inventory
- **Pending Request Tracking** - Monitor all pending client blood requests in real-time
- **Smart Blood Allocation** - Allocate blood to clients from nearby banks with location awareness
- **Bank Management** - Add new blood banks to the system with their details
- **Data Persistence** - All changes automatically saved to files

### Client Features
- **Easy Registration** - Register new blood requests with auto-generated unique ID
- **Real-time Status Tracking** - Check the status of your blood request anytime
- **Location-based Search** - System finds nearest banks with required blood type
- **Request Details** - Specify blood type, quantity, and urgency level

### System Features
- **Location-based Search** - Linked list structure for efficient location traversal
- **Network Communication** - Socket-based client-server architecture
- **Multi-threaded Processing** - Handles multiple concurrent client requests
- **Persistent Storage** - All data backed by file systems
- **Error Handling** - Robust exception handling throughout

---

## 🏗️ Project Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   MAIN MENU                             │
│         (Main.java - Entry Point)                       │
└────────────────┬────────────────────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
   ┌────▼─────┐      ┌───▼──────┐
   │ ADMIN     │      │ CLIENT   │
   │ SERVER    │      │ APP      │
   └──────┬────┘      └───┬──────┘
          │                │
          │   Port 5000    │
          └────────────────┘
                  │
        ┌─────────┴──────────┐
        │                    │
    ┌───▼──────┐      ┌─────▼────┐
    │BloodBank │      │ Clients  │
    │   .txt   │      │   .txt   │
    └──────────┘      └──────────┘
```

---

## 💻 System Requirements

- **Java Version**: JDK 8 or higher
- **OS**: Windows, macOS, or Linux
- **RAM**: Minimum 512MB
- **Storage**: 100MB for code and data files
- **Network**: Port 5000 must be available

---

## 🚀 Installation & Setup

### Step 1: Compile the Project
```bash
javac *.java
```

### Step 2: Prepare Data Files
Ensure the following files exist in the project directory:
- `BloodBank.txt` - Contains blood bank inventory data (provided)
- `Clients.txt` - Contains client requests (auto-created on first registration)

### Step 3: Run the Application
```bash
java Main
```

### Step 4: Select Your Role
- Choose option **1** to start the Admin Server
- Choose option **2** to start a Client Application

---

## 📖 Usage Guide

### For Admins

**Starting the Admin Server:**
1. Run the application and select "1" (Start Admin Server)
2. The system initializes bank data from `BloodBank.txt`
3. Location list is built automatically

**Admin Menu Options:**

| Option | Action |
|--------|--------|
| 1 | View all registered blood banks and their inventory |
| 2 | See all pending client blood requests |
| 3 | Allocate blood from nearby banks to pending clients |
| 4 | Register a new blood bank in the system |
| 5 | Exit admin panel |

**Allocating Blood:**
- Enter client name or ID
- System finds matching bank in client's location
- If sufficient blood available, allocation succeeds
- Client status updates to "Allocated" or "Still in Need"

---

### For Clients

**Starting Client Application:**
1. Run the application and select "2" (Start Client Application)
2. Choose "Register as New Client"
3. Provide your details (name, location, blood type, quantity, urgency)
4. Receive a unique Client ID

**Checking Request Status:**
1. Choose "Check Request Status"
2. Enter your Client ID
3. View allocation status and quantity details

---

## 📁 File Structure

```
blood-bank-system/
├── Main.java                 # Application entry point
├── AdminServer.java          # Admin server & socket handling
├── Client.java              # Client application & networking
├── Bank.java                # Bank data model
├── Blood.java               # Blood type & quantity model
├── BloodSearch.java         # Location-based search algorithm
├── LocationList.java        # Linked list for locations
├── LocationNode.java        # Node for location list
├── BloodBank.txt            # Bank inventory data (persistent)
├── Clients.txt              # Client requests (persistent)
└── .gitignore               # Git ignore rules
```

---

## 🔧 Key Components

### Main.java
Entry point for the entire application. Manages the main menu and port availability checks before starting admin or client instances.

### AdminServer.java
- Loads bank data from files
- Builds location list structure
- Listens for client connections on port 5000
- Handles all admin operations (view, allocate, add banks)
- Persists all changes back to files

### Client.java
- Registers new client requests
- Sends registration data to admin server
- Queries request status
- Communicates via socket protocol

### BloodSearch.java
Implements the location-based search algorithm using linked list traversal to find the nearest bank with required blood type and quantity.

### LocationList & LocationNode
Efficient linked list data structure organizing banks by geographic location for faster searches.

### Bank.java & Blood.java
Data models representing blood bank entities and blood inventory respectively.

---

## 🔄 How It Works

### Registration Flow
```
Client Registration → Sends to Server → Server Appends to Clients.txt
                                      → Admin Reviews in Menu
                                      → Admin Allocates Blood
                                      → Client Status Updated
```

### Blood Allocation Flow
```
Admin Selects "Allocate Blood"
        ↓
Enters Client Name/ID
        ↓
System Finds Client in Clients.txt
        ↓
Searches BloodBank.txt for matching location & blood type
        ↓
If Available: Deduct from inventory, Update Status
If Not: Mark as "Still in Need"
        ↓
Changes Saved to Files
```

### Status Check Flow
```
Client Provides ID → Connects to Server
                  → Server Reads Clients.txt
                  → Returns Current Status & Quantity
                  → Connection Closes
```

---

## 📊 Data Formats

### BloodBank.txt Format
```
BankID, BankName, BloodGroup, Quantity, Location, Contact, Email, LastUpdatedDate
BB016, HealTrust, A+, 2.0, Chennai, +91-9053461456, healtrust@bloodbank.in, 2025-03-28
```

### Clients.txt Format
```
ClientID, Name, Location, BloodType, Quantity, Status, Urgency, RequestDate
C132, Ramya, Chennai, A+, 2.0, Allocated, 3, 2025-10-19
```

---

## 🎮 Example Walkthrough

**Scenario:** Ramya needs 2 units of A+ blood in Chennai

1. **Ramya (Client):**
   - Runs app → Selects "Register"
   - Enters: Name=Ramya, Location=Chennai, Blood=A+, Qty=2, Urgency=3
   - Receives ID: C132

2. **Admin:**
   - Sees Ramya's pending request in "View Pending Clients"
   - Selects "Allocate Blood"
   - Enters "C132"
   - System finds HealTrust bank in Chennai with 2+ units of A+
   - Blood allocated, Ramya's status changes to "Allocated"

3. **Ramya checks status:**
   - Enters Client ID: C132
   - Sees: "C132 → Status: Allocated, Quantity requested: 2.0"

---

## 🚧 Future Enhancements

- **GUI Interface** - Swing/JavaFX for better user experience
- **Database Integration** - Replace file storage with MySQL/PostgreSQL
- **Advanced Analytics** - Dashboard with blood type trends and predictions
- **SMS/Email Notifications** - Alert clients when blood is allocated
- **Mobile App** - Android/iOS client for easier registration
- **Blood Compatibility Check** - Verify donation compatibility before allocation
- **Donor Management** - Track donor information and donation history
- **Report Generation** - Comprehensive inventory and allocation reports

---

## 🐛 Troubleshooting

**Problem:** "Port 5000 already in use"
- **Solution:** Close the admin server or kill the Java process using port 5000

**Problem:** "BloodBank.txt not found"
- **Solution:** Ensure the file is in the same directory as the compiled classes

**Problem:** "Could not connect to server"
- **Solution:** Make sure Admin Server is running on port 5000 before starting client

**Problem:** "Client not found"
- **Solution:** Double-check the Client ID format (should start with 'C' followed by 3 digits)

---

## 📝 License

This project is open-source and available for educational and commercial use.

---

## 👥 Contributing

Found a bug or have suggestions? Feel free to create issues or submit pull requests to improve the system.


---

**Last Updated:** October 19, 2025
