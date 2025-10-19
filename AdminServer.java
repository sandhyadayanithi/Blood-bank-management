import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;

public class AdminServer {
    private static final int PORT = 5000;
    private static boolean running = true;

    public static void main(String[] args) {
        // Start a thread to handle incoming client registrations
        new Thread(() -> listenToClients()).start();

        // Admin menu in main thread
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All Blood Banks");
            System.out.println("2. View Pending Client Requests");
            System.out.println("3. Allocate Blood to Client");
            System.out.println("4. Add New Blood Bank");
            System.out.println("5. Exit Admin");
            System.out.print("Choose an option: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: viewBloodBanks(); break;
                case 2: viewPendingClients(); break;
                case 3: allocateBloodInteractive(sc); break;
                case 4: addBloodBank(sc); break;
                case 5: System.out.println("Exiting Admin..."); running=false; break;
                default: System.out.println("Invalid choice!");
            }
        } while (choice != 5);
        sc.close();
    }

    // Append new client request to file (always Pending)
    private static synchronized void appendClient(String clientData) throws IOException {
        File f = new File("Client.txt");
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
                        appendClient(clientData); // Only append, no auto allocation
                    }
                    out.println("✅ Registration successful. Awaiting admin allocation.");
                } else if(command.startsWith("STATUS")){
                    String name = command.split(":")[1];
                    out.println(getClientStatus(name));
                } else {
                    out.println("❌ Unknown command");
                }
            } catch(Exception e){ e.printStackTrace(); }
            finally{ try{socket.close();}catch(Exception ignored){} }
        }
    }

    // --- Admin Menu Operations ---

    private static synchronized void viewBloodBanks(){
        File f = new File("BloodBank.txt");
        if(!f.exists()){ System.out.println("No blood banks found."); return;}
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            String header = br.readLine();
            if(header!=null) System.out.println(header);
            String line;
            while((line=br.readLine())!=null){
                System.out.println(line);
            }
        } catch(Exception e){ System.out.println("Error reading BloodBank.txt"); }
    }

    private static synchronized void viewPendingClients(){
        File f = new File("Client.txt");
        if(!f.exists()){ System.out.println("No clients found."); return;}
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            String header = br.readLine();
            if(header!=null) System.out.println(header);
            String line;
            while((line=br.readLine())!=null){
                String[] data = line.split(",\\s*");
                if(data[5].equalsIgnoreCase("Pending") || data[5].equalsIgnoreCase("Still in Need"))
                    System.out.println(line);
            }
        } catch(Exception e){ System.out.println("Error reading Client.txt"); }
    }

    private static synchronized void allocateBloodInteractive(Scanner sc){
        System.out.print("Enter Client Name to allocate blood: ");
        String clientName = sc.nextLine();

        List<String[]> clients = loadClients();
        List<String[]> banks = loadBloodBanks();

        boolean foundClient = false;
        for (String[] client : clients) {
            if (client[0].equalsIgnoreCase(clientName)) {
                foundClient = true;
                double requiredQty = Double.parseDouble(client[3]);
                boolean matchFound = false;

                for (String[] bank : banks) {
                    // Match by blood type and location
                    if (bank[2].equalsIgnoreCase(client[2]) && bank[4].equalsIgnoreCase(client[1])) {
                        matchFound = true;
                        int bankQty = Integer.parseInt(bank[3]);

                        if (bankQty >= requiredQty) {
                            // Enough blood units
                            bank[3] = String.valueOf(bankQty - (int) requiredQty);
                            client[5] = "Allocated";
                            System.out.println("✅ Blood allocated successfully to " + clientName);
                            break;
                        } else {
                            // Not enough units
                            System.out.println("❌ Not enough units at location " + client[1] + " for blood type " + client[2]);
                        }
                    }
                }

                if (!matchFound) {
                    System.out.println("❌ No blood bank with required type and location found for " + clientName);
                }

                break;
            }
        }

        if (!foundClient) {
            System.out.println("❌ Client not found.");
        }

        // Save updated clients and banks
        saveClients(clients);
        saveBloodBanks(banks);
    }

    private static synchronized void addBloodBank(Scanner sc){
        System.out.print("Bank ID: "); String id=sc.nextLine();
        System.out.print("Bank Name: "); String name=sc.nextLine();
        System.out.print("Blood Group: "); String group=sc.nextLine();
        System.out.print("Quantity (units): "); int qty=sc.nextInt(); sc.nextLine();
        System.out.print("Location: "); String location=sc.nextLine();
        String contact = "+91-" + (9000000000L + new Random().nextInt(999999999 - 900000000));
        String email = name.toLowerCase().replaceAll("\\s+","") + "@bloodbank.in";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try(BufferedWriter bw=new BufferedWriter(new FileWriter("BloodBank.txt",true))){
            File f = new File("BloodBank.txt");
            if(f.length()==0){
                bw.write("BankID, BankName, BloodGroup, Quantity, Location, ContactNumber, Email, LastUpdatedDate");
                bw.newLine();
            }
            bw.write(String.join(", ", id,name,group,String.valueOf(qty),location,contact,email,date));
            bw.newLine();
            System.out.println("New blood bank added.");
        } catch(Exception e){ e.printStackTrace();}
    }

    // --- Helpers ---
    private static synchronized List<String[]> loadClients(){
        List<String[]> clients = new ArrayList<>();
        File f = new File("Client.txt");
        if(!f.exists()) return clients;
        try(BufferedReader br=new BufferedReader(new FileReader(f))){
            br.readLine(); // skip header
            String line;
            while((line=br.readLine())!=null){
                clients.add(line.split(",\\s*"));
            }
        } catch(Exception e){ e.printStackTrace(); }
        return clients;
    }

    private static synchronized List<String[]> loadBloodBanks(){
        List<String[]> banks = new ArrayList<>();
        File f = new File("BloodBank.txt");
        if(!f.exists()) return banks;
        try(BufferedReader br=new BufferedReader(new FileReader(f))){
            br.readLine(); // skip header
            String line;
            while((line=br.readLine())!=null){
                String[] data=line.split(",\\s*");
                if(!data[3].equals("0")) banks.add(data); // skip zero qty records
            }
        } catch(Exception e){ e.printStackTrace(); }
        return banks;
    }

    private static synchronized void saveClients(List<String[]> clients){
        try(BufferedWriter bw=new BufferedWriter(new FileWriter("Client.txt"))){
            bw.write("Name, Location, BloodType, Quantity, Urgency, Status, RequestDate"); bw.newLine();
            for(String[] c:clients) bw.write(String.join(", ",c)+System.lineSeparator());
        } catch(Exception e){ e.printStackTrace(); }
    }

    private static synchronized void saveBloodBanks(List<String[]> banks){
        try(BufferedWriter bw=new BufferedWriter(new FileWriter("BloodBank.txt"))){
            bw.write("BankID, BankName, BloodGroup, Quantity, Location, ContactNumber, Email, LastUpdatedDate"); bw.newLine();
            for(String[] b:banks) bw.write(String.join(", ",b)+System.lineSeparator());
        } catch(Exception e){ e.printStackTrace(); }
    }

    private static synchronized String getClientStatus(String name){
        File f = new File("Client.txt");
        if(!f.exists()) return "No clients found.";
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.readLine(); // header
            String line;
            while((line=br.readLine())!=null){
                String[] data=line.split(",\\s*");
                if(data[0].equalsIgnoreCase(name)){
                    return "🧾 "+data[0]+" → Status: "+data[5]+", Quantity requested: "+data[3];
                }
            }
        } catch(Exception e){ e.printStackTrace(); }
        return "Client not found.";
    }
}
