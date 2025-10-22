# 🩸 Blood Bank Management System

A comprehensive Java-based distributed application for managing blood bank operations across multiple locations. This system enables seamless communication between admins managing inventory, clients requesting blood, and donors willing to contribute blood when shortages occur.

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

This Blood Bank Management System is designed to optimize blood distribution across multiple blood banks in different locations. It connects blood bank administrators with clients requesting blood and donors willing to donate through a socket-based networking model, ensuring efficient inventory management, rapid blood allocation, and emergency donor notifications.

**Perfect for:** Hospitals, blood banks, donation centers, and healthcare networks managing multiple locations.

---

## ✨ Features

### Admin Features
- **Dashboard Management** - View all blood banks and their current inventory
- **Pending Request Tracking** - Monitor all pending client blood requests in real-time
- **Smart Blood Allocation** - Allocate blood to clients from nearby banks with location awareness
- **Bank Management** - Add new blood banks to the system with their details
- **Donor Integration** - Add willing donors directly to the blood bank inventory
- **Emergency Notifications** - Send urgent blood requests to up to 3 matching donors
- **Donor Response Tracking** - View which donors are willing to help for specific clients
- **Data Persistence** - All changes automatically saved to files

### Client Features
- **Easy Registration** - Register new blood requests with auto-generated unique ID
- **Real-time Status Tracking** - Check the status of your blood request anytime
- **Location-based Search** - System finds nearest banks with required blood type
- **Request Details** - Specify blood type, quantity, and urgency level

### Donor Features
- **Simple Registration** - Register as a donor with blood type and location details
- **Request Notifications** - View pending blood requests matching your blood type and location
- **Donation Confirmation** - Confirm willingness to donate for specific client requests
- **Donation History Tracking** - Automatic updates to your total donated quantity and last donation date
- **Flexible Participation** - Choose which requests to respond to based on your availability

### System Features
- **Location-based Search** - Linked list structure for efficient location traversal
- **Network Communication** - Socket-based client-server architecture
- **Multi-threaded Processing** - Handles multiple concurrent client requests
- **Persistent Storage** - All data backed by file systems (BloodBank.txt, Clients.txt, Donors.txt, DonorMessages.txt, WillingDonors.txt)
- **Emergency Response System** - Automated donor notification when blood shortages occur
- **Error Handling** - Robust exception handling throughout

---

## 🏗️ Project Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   MAIN MENU                             │
│         (Main.java - Entry Point)                       │
└────────────────┬────────────────────────────────────────┘
                 │
        ┌────────┴────────┬────────┐
        │                 │        │
   ┌────▼─────┐      ┌───▼──────┐ │
   │ ADMIN     │      │ CLIENT   │ │
   │ SERVER    │      │ APP      │ │
   └──────┬────┘      └───┬──────┘ │
          │                │        │
          │   Port 5000    │    ┌───▼──────┐
          └────────────────┘    │ DONOR    │
                  │             │ APP      │
        ┌─────────┴──────────┐  └───┬──────┘
        │                    │      │
    ┌───▼──────┐      ┌─────▼────┐ │
    │BloodBank │      │ Clients  │ │
    │   .txt   │      │   .txt   │ │
    └──────────┘      └──────────┘ │
                                    │
        ┌───────────────────────────┘
        │
    ┌───▼──────┐      ┌─────────────┐
    │ Donors   │      │DonorMessages│
    │   .txt   │      │    .txt     │
    └──────────┘      └─────────────┘
                            │
                      ┌─────▼────────┐
                      │WillingDonors │
                      │    .txt      │
                      └──────────────┘
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
- `Donors.txt` - Contains donor information (auto-created on first donor registration)
- `DonorMessages.txt` - Contains emergency notifications sent to donors (auto-created)
- `WillingDonors.txt` - Tracks donors willing to help specific clients (auto-created)

### Step 3: Run the Application
```bash
java Main
```

### Step 4: Select Your Role
- Choose option **1** to start the Admin Server
- Choose option **2** to start a Client Application
- Choose option **3** to start a Donor Application

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
| 5 | Add a donor to the blood bank inventory |
| 6 | Send emergency blood request message to matching donors |
| 7 | Exit admin panel |

**Allocating Blood:**
- Enter client name or ID
- System finds matching bank in client's location
- If sufficient blood available, allocation succeeds
- If not available, system automatically notifies matching donors
- Client status updates to "Allocated" or "Still in Need"
- Admin can view willing donor responses

**Adding Donor to Bank:**
- Enter Donor ID from the Donors.txt file
- System creates a new bank entry with donor's blood
- Donor's contribution is added to inventory
- Automatically refreshes bank data

**Sending Emergency Messages:**
- Enter Client ID requiring urgent blood
- System finds up to 3 donors with matching blood type
- Emergency notifications sent to DonorMessages.txt
- Donors receive alerts when they check their portal

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

### For Donors

**Starting Donor Application:**
1. Run the application and select "3" (Start Donor Application)
2. Choose "Register as New Donor"
3. Provide details (name, blood type, location, quantity willing to donate)
4. Receive a unique Donor ID

**Checking Donation Requests:**
1. Choose "Check Donation Requests"
2. Enter your Donor ID
3. View pending requests matching your blood type
4. Respond "yes" to donate or "no" to skip
5. Your willingness is recorded and admin is notified
6. Your donation history automatically updates

---

## 📁 File Structure

```
blood-bank-system/
├── Main.java                 # Application entry point
├── AdminServer.java          # Admin server & socket handling
├── Client.java              # Client application & networking
├── Donor.java               # Donor application & registration
├── Bank.java                # Bank data model
├── Blood.java               # Blood type & quantity model
├── BloodSearch.java         # Location-based search algorithm
├── LocationList.java        # Linked list for locations
├── LocationNode.java        # Node for location list
├── BloodBank.txt            # Bank inventory data (persistent)
├── Clients.txt              # Client requests (persistent)
├── Donors.txt               # Donor information (persistent)
├── DonorMessages.txt        # Emergency notifications to donors
├── WillingDonors.txt        # Donors willing to help specific clients
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
- Handles all admin operations (view, allocate, add banks, manage donors)
- Sends emergency notifications to donors when blood shortages occur
- Tracks willing donors for allocation decisions
- Persists all changes back to files

### Client.java
- Registers new client requests
- Sends registration data to admin server
- Queries request status
- Communicates via socket protocol

### Donor.java
- Registers new donors with blood type and location
- Checks pending donation requests matching donor's blood type
- Allows donors to confirm willingness to donate
- Updates donation history automatically
- Records responses for admin review

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
                                      → If Shortage: Notifies Donors
                                      → Client Status Updated
```

### Donor Registration & Response Flow
```
Donor Registers → Data Saved to Donors.txt
                → Admin Sends Emergency Message
                → Donor Checks Requests (reads DonorMessages.txt)
                → Donor Confirms Willingness
                → Recorded in WillingDonors.txt
                → Admin Views Willing Donors
                → Admin Adds Donor to Bank Inventory
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
If Available: Deduct from inventory, Update Status to "Allocated"
If Not Available: Mark as "Still in Need" + Notify Matching Donors
        ↓
Check WillingDonors.txt for responses
        ↓
Display willing donor information to admin
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

### Donors.txt Format
```
DonorID, Name, BloodType, Location, QuantityGiven, LastDonatedDate
D476, Shalini, A+, Chennai, 1.0, 2025-10-19
```

### DonorMessages.txt Format
```
DonorID, ClientID, BloodType, Location, NeededQty, RequestDate
D476, C105, O-, Chennai, 6.2, 2025-10-22
```

### WillingDonors.txt Format
```
DonorID, ClientID, BloodType, Location, Quantity, Date
D670, C133, AB-, Trichy, 1.0, 2025-10-20
```

---

## 🎮 Example Walkthrough

**Scenario 1: Successful Blood Allocation**

Ramya needs 2 units of A+ blood in Chennai

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

**Scenario 2: Blood Shortage with Donor Notification**

Sahana needs 6.2 units of O- blood in Chennai, but no bank has sufficient quantity

1. **Sahana (Client):**
   - Registers and receives ID: C105
   - Requests O- blood, quantity 6.2 units

2. **Admin:**
   - Attempts to allocate blood for C105
   - No matching bank found with sufficient O- blood
   - System automatically notifies donors with O- blood type
   - Status marked as "Still in Need"

3. **Shalini (Donor):**
   - Already registered with Donor ID: D476 (Blood type: O-)
   - Checks "Donation Requests"
   - Sees: "Blood Group O- is needed by Client C105 at Chennai (Quantity: 6.2)"
   - Responds "yes" to donate
   - Willingness recorded in WillingDonors.txt

4. **Admin (Follow-up):**
   - Checks allocation again for C105
   - Sees message: "Donor D476 is willing to help for Client C105!"
   - Selects "Add Donor to Bank" 
   - Enters Donor ID: D476
   - System creates new bank entry with donor's blood
   - Can now allocate blood to Sahana successfully

---

## 🚧 Future Enhancements

- **GUI Interface** - Swing/JavaFX for better user experience
- **Database Integration** - Replace file storage with MySQL/PostgreSQL
- **Advanced Analytics** - Dashboard with blood type trends and predictions
- **SMS/Email Notifications** - Alert clients and donors via email/SMS when needed
- **Mobile App** - Android/iOS client for easier registration and real-time notifications

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

**Problem:** "Donor not found"
- **Solution:** Ensure the Donor ID is correct (should start with 'D' followed by 3 digits)

**Problem:** "No pending messages for your Donor ID"
- **Solution:** No matching requests exist yet. Check again later or wait for admin to send emergency notifications

**Problem:** Donors.txt or DonorMessages.txt not found
- **Solution:** These files are auto-created on first use. Register at least one donor to initialize the system

---

## 📝 License

This project is open-source and available for educational and commercial use.

---

## 👥 Contributing

Found a bug or have suggestions? Feel free to create issues or submit pull requests to improve the system.

---

**Last Updated:** October 22, 2025
