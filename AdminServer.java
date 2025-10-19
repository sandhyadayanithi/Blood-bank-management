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
            String line;
            while((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length >= 3) {
                    Bank bank = new Bank();
                    bank.ID = Integer.parseInt(data[0].trim());
                    bank.name = data[1].trim();
                    bank.location = data[2].trim();
                    
                    // Parse blood types
                    if(data.length > 3) {
                        for(int i = 3; i < data.length; i += 2) {
                            if(i + 1 < data.length) {
                                Blood blood = new Blood();
                                blood.type = data[i].trim();
                                blood.availableQty = Double.parseDouble(data[i+1].trim());
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
        // Add all unique locations
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
        
        // Assign each bank to its location node
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
                bw.write("Name, Location, BloodType, Quantity, Urgency, Status, RequestDate");
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
                    out.println("Registration successful. Awaiting admin allocation.");
                } else if(command.startsWith("STATUS")){
                    String name = command.split(":")[1];
                    out.println(getClientStatus(name));
                } else {
                    out.println(" Unknown command");
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
            String line;
            System.out.println("ID, Name, Location, Blood Types");
            while((line=br.readLine())!=null){
                System.out.println(line);
            }
        } catch(Exception e){ System.out.println("Error reading BloodBank.txt"); }
    }

    private static synchronized void viewPendingClients() {
        File f = new File("Clients.txt");
        if (!f.exists()) { System.out.println("No clients found."); return; }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine(); // first line = header
            List<String[]> pendingClients = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                for(int i=0;i<data.length;i++) data[i] = data[i].trim(); // trim spaces
                if(data.length > 6 && 
                (data[6].equalsIgnoreCase("Pending") || data[6].equalsIgnoreCase("Still in Need"))) {
                    pendingClients.add(data);
                }
            }

            if(pendingClients.isEmpty()) {
                System.out.println("No pending clients found.");
                return;
            }

            // Sort based on Urgency (index 5)
            pendingClients.sort((a, b) -> Integer.parseInt(a[5]) - Integer.parseInt(b[5]));

            // Print header
            System.out.println(header);

            // Print sorted clients
            for(String[] c : pendingClients) {
                System.out.println(String.join(", ", c));
            }

        } catch (Exception e) {
            System.out.println("Error reading Clients.txt");
        }
    }


    private static synchronized void allocateBloodInteractive(Scanner sc){
        System.out.print("Enter Client Name to allocate blood: ");
        String clientName = sc.nextLine();

        List<String[]> clients = loadClients();
        boolean foundClient = false;

        for (String[] client : clients) {
            if (client[1].trim().equalsIgnoreCase(clientName)) {
                foundClient = true;
                String clientLocation = client[2].trim();
                String bloodType = client[3].trim();
                double requiredQty = Double.parseDouble(client[4].trim());

                // Use location-based search to find nearest bank
                Bank foundBank = BloodSearch.findNearestBank(
                    clientLocation, bloodType, requiredQty, locationList
                );

                if (foundBank != null) {
                    // Reduce blood quantity from the bank
                    for (Blood blood : foundBank.bloodType) {
                        if (blood.type.equalsIgnoreCase(bloodType)) {
                            blood.availableQty -= requiredQty;
                            break;
                        }
                    }
                    
                    client[5] = "Allocated";
                    System.out.println(" Blood allocated successfully!");
                    System.out.println("   Bank: " + foundBank.name);
                    System.out.println("   Location: " + foundBank.location);
                    System.out.println("   Blood Type: " + bloodType);
                } else {
                    System.out.println(" No suitable bank found for " + clientName);
                    client[5] = "Still in Need";
                }

                break;
            }
        }

        if (!foundClient) {
            System.out.println(" Client not found.");
        }

        // Save updated clients
        saveClients(clients);
        // Save updated banks
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
        try(BufferedReader br=new BufferedReader(new FileReader(f))){
            br.readLine(); // skip header
            String line;
            while((line=br.readLine())!=null){
                clients.add(line.split(","));
            }
        } catch(Exception e){ e.printStackTrace(); }
        return clients;
    }

    private static synchronized void saveClients(List<String[]> clients){
        try(BufferedWriter bw=new BufferedWriter(new FileWriter("Clients.txt"))){
            bw.write("Name, Location, BloodType, Quantity, Urgency, Status, RequestDate");
            bw.newLine();
            for(String[] c:clients) {
                bw.write(String.join(",", c));
                bw.newLine();
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    private static synchronized void saveBanks(){
        try(BufferedWriter bw=new BufferedWriter(new FileWriter("BloodBank.txt"))){
            for(Bank bank : banks) {
                StringBuilder line = new StringBuilder();
                line.append(bank.ID).append(",").append(bank.name).append(",").append(bank.location);
                for(Blood blood : bank.bloodType) {
                    line.append(",").append(blood.type).append(",").append(blood.availableQty);
                }
                bw.write(line.toString());
                bw.newLine();
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    private static synchronized String getClientStatus(String name){
        File f = new File("Clients.txt");
        if(!f.exists()) return "No clients found.";
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.readLine(); // header
            String line;
            while((line=br.readLine())!=null){
                String[] data=line.split(",");
                if(data[0].trim().equalsIgnoreCase(name)){
                    return " " + data[0].trim() + " → Status: " + data[5].trim() + 
                           ", Quantity requested: " + data[3].trim();
                }
            }
        } catch(Exception e){ e.printStackTrace(); }
        return "Client not found.";
    }
}