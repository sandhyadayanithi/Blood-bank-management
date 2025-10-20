import java.io.*;
import java.util.*;
import java.net.*;

public class AdminServer {
    private static final int PORT = 5000;
    private static boolean running = true;

    // Location-based search system
    private static LocationList locationList = new LocationList();
    private static ArrayList<Bank> banks = new ArrayList<>();

    public static void adminMain(Scanner sc) {
        // Initialize bank data on server startup
        System.out.println(" Initializing bank data...");
        loadBankData();
        System.out.println(" Bank data loaded and location list built.\n");

        // Start a thread to handle incoming client registrations
        new Thread(() -> listenToClients()).start();

        // Admin menu in main thread
        int choice;
        do {
    System.out.println("\n=== ADMIN MENU ===");
    System.out.println("1. View All Banks");
    System.out.println("2. View Pending Client Requests");
    System.out.println("3. Allocate Blood to Client");
    System.out.println("4. Add New Bank");
    System.out.println("5. Add Donor to Bank");
    System.out.println("6. Send Emergency Message");  // ✅ moved up
    System.out.println("7. Exit Admin");              // ✅ now last option
    System.out.print("Choose an option: ");
    choice = sc.nextInt();
    sc.nextLine(); // consume newline

    switch (choice) {
        case 1:
            viewAllBanks();
            break;
        case 2:
            viewPendingClients();
            break;
        case 3:
            allocateBloodInteractive(sc);
            break;
        case 4:
            addNewBank(sc);
            break;
        case 5:
            addDonorToBank(sc);
            break;
        case 6:
            sendEmergencyMessage(sc);   // ✅ new order
            break;
        case 7:
            System.out.println("Exiting Admin...");
            running = false;
            break;
        default:
            System.out.println("Invalid choice!");
    }

} while (running);
    }


    // --- Load bank data and build location list ---
    private static synchronized void loadBankData() {
        File f = new File("BloodBank.txt");
        if(!f.exists()) {
            System.out.println("BloodBank.txt not found.");
            return;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine(); // skip header
            String line;
            while((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length < 5) continue; // skip invalid lines

                Bank bank = new Bank();
                bank.ID = data[0].trim();
                bank.name = data[1].trim();

                Blood blood = new Blood();
                blood.type = data[2].trim();
                try {
                    blood.availableQty = Double.parseDouble(data[3].trim());
                } catch(Exception e) {
                    blood.availableQty = 0;
                }

                bank.bloodType.add(blood); // one blood type per line
                bank.location = data[4].trim();

                // Optional: you can store contact info, email, last updated if needed
                // String contact = data[5].trim();
                // String email = data[6].trim();
                // String lastUpdated = data[7].trim();

                banks.add(bank);
            }
        } catch(Exception e) {
            System.out.println("Error loading bank data: " + e.getMessage());
        }

        buildLocationList(banks);
    }

    // --- Send emergency alert to top 3 matching donors ---
private static synchronized void sendEmergencyMessage(Scanner sc) {
    System.out.print("Enter Client ID to send emergency message for: ");
    String clientID = sc.nextLine().trim();

    File clientsFile = new File("Clients.txt");
    if (!clientsFile.exists()) {
        System.out.println("Clients.txt not found.");
        return;
    }

    String bloodType = null;
    String location = null;
    double neededQty = 0;

    // Step 1: Find the client's blood requirement
    try (BufferedReader br = new BufferedReader(new FileReader(clientsFile))) {
        br.readLine(); // skip header
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split("\\s*,\\s*");
            if (data[0].equalsIgnoreCase(clientID)) {
                bloodType = data[3];
                location = data[2];
                neededQty = Double.parseDouble(data[4]);
                break;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    if (bloodType == null) {
        System.out.println("❌ Client not found.");
        return;
    }

    // Step 2: Find up to 3 matching donors by blood type
    File donorFile = new File("Donors.txt");
    File msgFile = new File("DonorMessages.txt");

    if (!donorFile.exists()) {
        System.out.println("Donors.txt not found.");
        return;
    }

    List<String> matchingDonors = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(donorFile))) {
        br.readLine(); // skip header
        String line;
        while ((line = br.readLine()) != null && matchingDonors.size() < 3) {
            String[] data = line.split("\\s*,\\s*");
            if (data.length >= 5 && data[2].equalsIgnoreCase(bloodType)) {
                matchingDonors.add(data[0]);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    if (matchingDonors.isEmpty()) {
        System.out.println("No donors with matching blood group found.");
        return;
    }

    // Step 3: Write messages to DonorMessages.txt
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(msgFile, true))) {
        boolean writeHeader = !msgFile.exists() || msgFile.length() == 0;
        if (writeHeader) {
            bw.write("DonorID, ClientID, BloodType, Location, NeededQty, RequestDate");
            bw.newLine();
        }

        String today = java.time.LocalDate.now().toString();
        for (String donorID : matchingDonors) {
            bw.write(String.join(", ", donorID, clientID, bloodType, location, String.valueOf(neededQty), today));
            bw.newLine();
        }
        bw.flush();
        System.out.println("✅ Emergency messages sent to donors: " + String.join(", ", matchingDonors));

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private static synchronized void addDonorToBank(Scanner sc) {
    System.out.print("Enter Donor ID to add to Bank: ");
    String donorID = sc.nextLine().trim();

    File donorFile = new File("Donors.txt");
    if (!donorFile.exists()) {
        System.out.println("Donors.txt not found.");
        return;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(donorFile))) {
        String header = br.readLine(); // skip header
        String line;
        boolean found = false;

        while ((line = br.readLine()) != null) {
            String[] data = line.split("\\s*,\\s*");
            if (data.length < 6) continue;

            if (data[0].equalsIgnoreCase(donorID)) {
                found = true;
                String donorName = data[1];
                String bloodType = data[2];
                String location = data[3];
                double qty = Double.parseDouble(data[4]);
                String lastDonated = data[5];

                // Create a new unique BankID
                String bankID = generateBankID();
                String bankName = "DonorBank_" + donorID;

                File bankFile = new File("BloodBank.txt");
                boolean writeHeader = !bankFile.exists() || bankFile.length() == 0;

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(bankFile, true))) {
                    if (writeHeader) {
                        bw.write("BankID, BankName, BloodType, Quantity, Location, Contact, Email, LastUpdated");
                        bw.newLine();
                    }

                    String today = java.time.LocalDate.now().toString();
                    String record = String.join(", ",
                            bankID,
                            bankName,
                            bloodType,
                            String.valueOf(qty),
                            location,
                            "N/A",     // Contact
                            "N/A",     // Email
                            today
                    );
                    bw.write(record);
                    bw.newLine();
                    bw.flush();
                }

                System.out.println("✅ Donor added to Blood Bank successfully!");
                System.out.println("   DonorID: " + donorID);
                System.out.println("   Created Bank: " + bankName + " (" + bankID + ")");
                System.out.println("   Blood Type: " + bloodType + " | Location: " + location + " | Qty: " + qty);
                break;
            }
        }

        if (!found) {
            System.out.println("❌ Donor ID not found in Donors.txt.");
        }

    } catch (Exception e) {
        System.out.println("Error while adding donor to bank: " + e.getMessage());
    }

    // Refresh in-memory data so allocation sees new bank
    banks.clear();
    loadBankData();
}

private static String generateBankID() {
    return "B" + (new Random().nextInt(900) + 100);
}

    private static void buildLocationList(ArrayList<Bank> bankList) {
        for (Bank bank : bankList) {
            LocationNode temp = locationList.getHead();
            boolean exists = false;
            while (temp != null) {
                if (temp.locationName.equalsIgnoreCase(bank.location)) {
                    exists = true;
                    break;
                }
                temp = temp.next;
            }
            if (!exists) {
                locationList.addLocation(bank.location);
            }
        }

        for (Bank bank : bankList) {
            LocationNode temp = locationList.getHead();
            while (temp != null) {
                if (bank.location.equalsIgnoreCase(temp.locationName)) {
                    temp.addBank(bank);
                    break;
                }
                temp = temp.next;
            }
        }
    }

    // Append new client request to file
   private static synchronized void appendClient(String clientData) throws IOException {
    File f = new File("Clients.txt");
    boolean writeHeader = !f.exists() || f.length() == 0;

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
        if (writeHeader) {
            bw.write("ClientID, Name, Location, BloodType, Quantity, Status, Urgency, RequestDate");
            bw.newLine();
        }
        bw.write(clientData);
        bw.newLine();
        bw.flush();
    }
}


    // --- Network listener for client registration ---
    private static void listenToClients() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Server stopped listening or port in use.");
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        ClientHandler(Socket socket){ this.socket = socket; }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ){
                String command = in.readLine();
                if(command.equals("REGISTER")){
                    String clientData = in.readLine();
                    synchronized(AdminServer.class){
                        appendClient(clientData);
                    }
                    out.println("Registration successful!");
                } else if(command.startsWith("STATUS")){
                    String[] parts = command.split(":", 2);
                    if(parts.length < 2 || parts[1].trim().isEmpty()){
                        out.println("Invalid request. Name missing.");
                    } else {
                        out.println(getClientStatus(parts[1].trim()));
                    }
                } else {
                    out.println("Unknown command");
                }
            } catch(Exception e){ e.printStackTrace(); }
            finally{ try{socket.close();}catch(Exception ignored){} }
        }
    }

    // --- Admin Menu Operations ---
    private static synchronized void viewAllBanks(){
        File f = new File("BloodBank.txt");
        if(!f.exists()){ System.out.println("No banks found."); return;}
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            String line = br.readLine(); // skip header
            System.out.println("ID, Name, Location, Blood Types");
            while((line=br.readLine())!=null){
                System.out.println(line.trim());
            }
        } catch(Exception e){ System.out.println("Error reading BloodBank.txt"); }
    }

    
    private static synchronized void viewPendingClients() {
        File f = new File("Clients.txt");
        if (!f.exists()) {
            System.out.println("No clients found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine(); // read header
            if (header == null) {
                System.out.println("Clients.txt is empty.");
                return;
            }

            List<String> pendingLines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Normalize commas and spacing
                String[] data = line.split("\\s*,\\s*");
                String joined = String.join(",", data);

                // Try to find "Pending" or "Still in Need" anywhere
                if (joined.toLowerCase().contains("pending") || joined.toLowerCase().contains("still in need")) {
                    pendingLines.add(joined.trim());
                }
            }

            if (pendingLines.isEmpty()) {
                System.out.println("No pending clients found.");
                return;
            }

            System.out.println("\n=== Pending Client Requests ===");
            System.out.println(header);
            for (String p : pendingLines) {
                System.out.println(p);
            }

        } catch (Exception e) {
            System.out.println("Error reading Clients.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static synchronized void notifyDonors(String bloodType, String location, double qty, String clientID) {
    File donorFile = new File("Donors.txt");
    File msgFile = new File("DonorMessages.txt");

    if (!donorFile.exists()) return;

    try (BufferedReader br = new BufferedReader(new FileReader(donorFile));
         BufferedWriter bw = new BufferedWriter(new FileWriter(msgFile, true))) {

        boolean writeHeader = !msgFile.exists() || msgFile.length() == 0;
        if (writeHeader) {
            bw.write("DonorID, ClientID, BloodType, Location, NeededQty, RequestDate");
            bw.newLine();
        }

        br.readLine(); // skip header
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split("\\s*,\\s*");
            if (data[2].equalsIgnoreCase(bloodType) && data[3].equalsIgnoreCase(location)) {
                String today = java.time.LocalDate.now().toString();
                bw.write(String.join(", ", data[0], clientID, bloodType, location,
                        String.valueOf(qty), today));
                bw.newLine();
            }
        }
        bw.flush();

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private static synchronized void allocateBloodInteractive(Scanner sc) {
    System.out.print("Enter Client Name or ID to allocate blood: ");
    String clientInput = sc.nextLine().trim();

    List<String[]> clients = loadClients();
    boolean foundClient = false;

    for (String[] client : clients) {
        if (client[0].trim().equalsIgnoreCase(clientInput) || client[1].trim().equalsIgnoreCase(clientInput)) {
            foundClient = true;
            String clientID = client[0].trim();
            String clientName = client[1].trim();
            String clientLocation = client[2].trim();
            String bloodType = client[3].trim();
            double requiredQty = Double.parseDouble(client[4].trim());

            // Search for a matching bank entry
            Bank foundBank = null;
            Blood foundBlood = null;

            for (Bank bank : banks) {
                if (bank.location.trim().equalsIgnoreCase(clientLocation) && !bank.bloodType.isEmpty()) {
                    Blood blood = bank.bloodType.get(0); // each bank line has one blood type
                    if (blood.type.trim().equalsIgnoreCase(bloodType) && blood.availableQty >= requiredQty) {
                        foundBank = bank;
                        foundBlood = blood;
                        break;
                    }
                }
            }

            if (foundBank != null && foundBlood != null) {
                // Allocate blood
                foundBlood.availableQty -= requiredQty;
                client[5] = "Allocated";  // ✅ Correct index for Status
                System.out.println("✅ Blood allocated successfully!");
                System.out.println("   Client: " + clientName + " (ID: " + clientID + ")");
                System.out.println("   Bank: " + foundBank.name + " | Location: " + foundBank.location);
                System.out.println("   Blood Type: " + bloodType + " | Quantity: " + requiredQty);
            } else {
                client[5] = "Still in Need";  // ✅ Correct index for Status
                System.out.println("⚠ No sufficient units available at this location for " + clientName);

                // Notify donors
                notifyDonors(bloodType, clientLocation, requiredQty, clientID);

                // Check for willing donors
                File willingFile = new File("WillingDonors.txt");
                if (willingFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(willingFile))) {
                        br.readLine(); // skip header
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] data = line.split("\\s*,\\s*");
                            if (data.length > 1 && data[1].equalsIgnoreCase(clientID)) {
                                System.out.println("💬 Donor " + data[0] + " is willing to help for Client " + clientID + "!");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            break; // stop after processing the found client
        }
    }

    if (!foundClient) {
        System.out.println("❌ Client not found.");
    }

    // Save updated clients and banks
    saveClients(clients);
    saveBanks();
}




    private static synchronized void addNewBank(Scanner sc){
        loadBankData();      // Load current data
        Bank newBank = new Bank();
        newBank.getDetails(sc); // Fill from user input
        banks.add(newBank);   // Add to list
        saveBanks();
    }

    // --- Helpers ---
    private static synchronized List<String[]> loadClients(){
        List<String[]> clients = new ArrayList<>();
        File f = new File("Clients.txt");
        if(!f.exists()) return clients;
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.readLine(); // skip header
            String line;
            while((line=br.readLine())!=null){
                clients.add(line.split("\\s*,\\s*"));

            }
        } catch(Exception e){ e.printStackTrace(); }
        return clients;
    }

   private static synchronized void saveClients(List<String[]> clients) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter("Clients.txt"))) {
        bw.write("ClientID, Name, Location, BloodType, Quantity, Status, Urgency, RequestDate");
        bw.newLine();
        for (String[] c : clients) {
            for (int i = 0; i < c.length; i++) c[i] = c[i].trim();
            bw.write(String.join(",", c));
            bw.newLine();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private static synchronized void saveBanks() {
        File f = new File("BloodBank.txt");

        try {
            // ✅ Step 1: Read existing file (if it exists)
            List<String> lines = new ArrayList<>();
            boolean hasHeader = false;
            if (f.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String header = br.readLine();
                    if (header != null && !header.isBlank()) {
                        lines.add(header); // preserve header
                        hasHeader = true;
                    }
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }

            // ✅ Step 2: Update existing entries (same as before)
            for (int i = 1; i < lines.size(); i++) { // skip header
                String[] parts = lines.get(i).split(",");
                for (int j = 0; j < parts.length; j++) parts[j] = parts[j].trim();

                String bankID = parts[0];
                String bloodType = parts[2]; // blood type column
                double updatedQty = -1;

                for (Bank bank : banks) {
                    if (bank.ID.equals(bankID)) {
                        for (Blood b : bank.bloodType) {
                            if (b.type.equalsIgnoreCase(bloodType)) {
                                updatedQty = b.availableQty;
                                break;
                            }
                        }
                    }
                    if (updatedQty != -1) break;
                }

                if (updatedQty != -1) {
                    parts[3] = String.valueOf(updatedQty);
                }

                lines.set(i, String.join(", ", parts));
            }

            // ✅ Step 3: Append any *new* bank entries not found in the file
            Set<String> existingKeys = new HashSet<>();
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                existingKeys.add(parts[0].trim() + "_" + parts[2].trim()); // unique key = ID + BloodType
            }

            for (Bank bank : banks) {
                for (Blood blood : bank.bloodType) {
                    String key = bank.ID + "_" + blood.type;
                    if (!existingKeys.contains(key)) {
                        String today = java.time.LocalDate.now().toString();
                        String newLine = String.join(", ",
                                bank.ID,
                                bank.name,
                                blood.type,
                                String.valueOf(blood.availableQty),
                                bank.location,
                                bank.contactNumber,
                                bank.email,
                                today
                        );
                        lines.add(newLine);
                    }
                }
            }

            // ✅ Step 4: Write everything back
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                if (!hasHeader) {
                    bw.write("BankID, BankName, BloodType, Quantity, Location, Contact, Email, LastUpdated");
                    bw.newLine();
                }
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static synchronized String getClientStatus(String clientId){
    File f = new File("Clients.txt");
    if(!f.exists()) return "No clients found.";
    try(BufferedReader br = new BufferedReader(new FileReader(f))){
        br.readLine(); // skip header
        String line;
        while((line=br.readLine())!=null){
            String[] data = line.split("\\s*,\\s*");

            for(int i=0;i<data.length;i++) data[i]=data[i].trim();
            if(data[0].equalsIgnoreCase(clientId)) {
                return data[0]+" → Status: "+data[6]+", Quantity requested: "+data[4];
            }
        }
    } catch(Exception e){ e.printStackTrace(); }
    return "Client not found.";
}

}
