import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;

public class AdminServer {
    private static final int PORT = 5000;
    private static boolean running = true;

    // Location-based search system
    private static LocationList locationList = new LocationList();
    private static ArrayList<Bank> banks = new ArrayList<>();

    public static void main(String[] args) {
        // Initialize bank data on server startup
        System.out.println(" Initializing bank data...");
        loadBankData();
        System.out.println(" Bank data loaded and location list built.\n");

        // Start a thread to handle incoming client registrations
        new Thread(() -> listenToClients()).start();

        // Admin menu in main thread
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All Banks");
            System.out.println("2. View Pending Client Requests");
            System.out.println("3. Allocate Blood to Client");
            System.out.println("4. Add New Bank");
            System.out.println("5. Exit Admin");
            System.out.print("Choose an option: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: viewAllBanks(); break;
                case 2: viewPendingClients(); break;
                case 3: allocateBloodInteractive(sc); break;
                case 4: addNewBank(sc); break;
                case 5: System.out.println("Exiting Admin..."); running=false; break;
                default: System.out.println("Invalid choice!");
            }
        } while (choice != 5);
        sc.close();
    }

    // --- Load bank data and build location list ---
    private static synchronized void loadBankData() {
        File f = new File("BloodBank.txt");
        if(!f.exists()) {
            System.out.println(" BloodBank.txt not found.");
            return;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine(); // skip header
            String line;
            while((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length >= 3) {
                    Bank bank = new Bank();
                    try {
                        bank.ID = Integer.parseInt(data[0].trim());
                    } catch(Exception e) { continue; } // skip invalid lines
                    bank.name = data[1].trim();
                    bank.location = data[2].trim();

                    // Parse blood types
                    if(data.length > 3) {
                        for(int i = 3; i < data.length; i += 2) {
                            if(i + 1 < data.length) {
                                Blood blood = new Blood();
                                blood.type = data[i].trim();
                                try {
                                    blood.availableQty = Double.parseDouble(data[i+1].trim());
                                } catch(Exception e){ blood.availableQty = 0; }
                                bank.bloodType.add(blood);
                            }
                        }
                    }
                    banks.add(bank);
                }
            }
        } catch(Exception e) {
            System.out.println("Error loading bank data: " + e.getMessage());
        }

        // Build location list from loaded banks
        buildLocationList(banks);
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
                    out.println("✅ Registration successful! Your request is pending admin allocation.");
                } else if(command.startsWith("STATUS")){
                    String[] parts = command.split(":", 2);
                    if(parts.length < 2 || parts[1].trim().isEmpty()){
                        out.println("❌ Invalid request. Name missing.");
                    } else {
                        out.println(getClientStatus(parts[1].trim()));
                    }
                } else {
                    out.println("❌ Unknown command");
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

            // Find a bank in the same location with sufficient quantity
            Bank foundBank = null;
            for (Bank bank : banks) {
                if (bank.location.equalsIgnoreCase(clientLocation)) {
                    for (Blood blood : bank.bloodType) {
                        if (blood.type.equalsIgnoreCase(bloodType) && blood.availableQty >= requiredQty) {
                            foundBank = bank;
                            break;
                        }
                    }
                }
                if (foundBank != null) break;
            }

            if (foundBank != null) {
                // Allocate the blood
                for (Blood blood : foundBank.bloodType) {
                    if (blood.type.equalsIgnoreCase(bloodType)) {
                        blood.availableQty -= requiredQty;
                        break;
                    }
                }
                client[5] = "Allocated";
                System.out.println("✅ Blood allocated successfully!");
                System.out.println("   Client: " + clientName + " (ID: " + clientID + ")");
                System.out.println("   Bank: " + foundBank.name + " | Location: " + foundBank.location);
                System.out.println("   Blood Type: " + bloodType + " | Quantity: " + requiredQty);
            } else {
                client[5] = "Still in Need";
                System.out.println("❌ No sufficient units available at this location for " + clientName);
            }

            break;
        }
    }

    if (!foundClient) {
        System.out.println("❌ Client not found.");
    }

    // Save updated clients and banks (only updates quantities, preserves other data)
    saveClients(clients);
    saveBanks();
}


    private static synchronized void addNewBank(Scanner sc){
        Bank newBank = new Bank();
        newBank.getDetails();
        banks.add(newBank);
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
    if (!f.exists()) return;

    try {
        // Read existing file
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine();
            lines.add(header); // preserve header
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        // Update only quantities in memory
        for (int i = 1; i < lines.size(); i++) { // skip header
            String[] parts = lines.get(i).split(",");
            for (int j = 0; j < parts.length; j++) parts[j] = parts[j].trim();

            // Match bank in memory
            Optional<Bank> bankOpt = banks.stream()
                    .filter(b -> b.ID == Integer.parseInt(parts[0]))
                    .findFirst();

            if (bankOpt.isPresent()) {
                Bank bank = bankOpt.get();
                // Update only blood quantities
                for (int k = 3; k < parts.length; k += 2) {
                    String type = parts[k];
                    Optional<Blood> b = bank.bloodType.stream()
                            .filter(x -> x.type.equalsIgnoreCase(type))
                            .findFirst();
                    if (b.isPresent()) {
                        parts[k + 1] = String.valueOf(b.get().availableQty);
                    }
                }
            }

            lines.set(i, String.join(", ", parts));
        }

        // Write back
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
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
