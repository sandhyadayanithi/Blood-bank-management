import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Donor {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5000;

    public static void donorMain(Scanner sc) {
        int choice;
        do {
            System.out.println("\n--- DONOR MENU ---");
            System.out.println("1. Register as New Donor");
            System.out.println("2. Check Donation Requests");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    registerDonor(sc);
                    break;
                case 2:
                    checkRequests(sc);
                    break;
                case 3:
                    System.out.println("Exiting Donor portal...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 3);
    }

    // Register a new donor 
    private static void registerDonor(Scanner sc) {
        try {
            File f = new File("Donors.txt");
            boolean writeHeader = !f.exists() || f.length() == 0;
            BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));

            Random rand = new Random();
            String donorID = "D" + (rand.nextInt(900) + 100);
            System.out.println("Your Donor ID: " + donorID);

            System.out.print("Enter your Name: ");
            String name = sc.nextLine();

            System.out.print("Enter your Blood Type: ");
            String bloodType = sc.nextLine();

            System.out.print("Enter your Location: ");
            String location = sc.nextLine();

            System.out.print("Enter Quantity you wish to donate (units): ");
            double qty = sc.nextDouble();
            sc.nextLine();

            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            if (writeHeader) {
                bw.write("DonorID, Name, BloodType, Location, QuantityGiven, LastDonatedDate");
                bw.newLine();
            }

            String record = String.join(", ",
                    donorID, name, bloodType, location, String.valueOf(qty), date);
            bw.write(record);
            bw.newLine();
            bw.flush();
            bw.close();

            System.out.println("Registration successful! Thank you for being a donor.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
// Check if any donation requests are pending for this donor 
private static void checkRequests(Scanner sc) {
    System.out.print("Enter your Donor ID: ");
    String donorID = sc.nextLine().trim();

    File msgFile = new File("DonorMessages.txt");
    if (!msgFile.exists()) {
        System.out.println("No new requests found.");
        return;
    }

    List<String> updatedMessages = new ArrayList<>();
    boolean found = false;

    try (BufferedReader br = new BufferedReader(new FileReader(msgFile))) {
        String header = br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split("\\s*,\\s*");
            if (data[0].equalsIgnoreCase(donorID)) {
                found = true;
                String clientID = data[1];
                String bloodType = data[2];
                String location = data[3];
                String qty = data[4];

                System.out.println("⚠ Blood Group " + bloodType + " is needed by Client " + clientID +
                        " at " + location + " (Quantity: " + qty + ").");
                System.out.print("Are you willing to donate? (yes/no): ");
                String choice = sc.nextLine().trim().toLowerCase();

                if (choice.equals("yes")) {
                    // 1. Update donor record in Donors.txt
                    updateDonorRecord(donorID, Double.parseDouble(qty));
                    System.out.println("Thank you! Your donation has been recorded.");

                    // 2. Record willingness for admin in WillingDonors.txt
                    File willingFile = new File("WillingDonors.txt");
                    boolean writeHeader = !willingFile.exists() || willingFile.length() == 0;

                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(willingFile, true))) {
                        if (writeHeader) {
                            bw.write("DonorID, ClientID, BloodType, Location, Quantity, Date");
                            bw.newLine();
                        }
                        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        bw.write(String.join(", ", donorID, clientID, bloodType, location, qty, today));
                        bw.newLine();
                        bw.flush();
                        System.out.println("Your willingness has been recorded for admin to process.");
                    } catch (IOException e) {
                        System.out.println("Error saving to WillingDonors.txt: " + e.getMessage());
                    }

                } else {
                    updatedMessages.add(line);
                    System.out.println("No problem, maybe next time.");
                }
            } else {
                updatedMessages.add(line);
            }
        }

        // Rewrite messages file 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(msgFile))) {
            if (header != null) {
                bw.write(header);
                bw.newLine();
            }
            for (String msg : updatedMessages) {
                bw.write(msg);
                bw.newLine();
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    if (!found) {
        System.out.println("No pending messages for your Donor ID.");
    }
}


 
    private static void updateDonorRecord(String donorID, double donatedQty) {
        File file = new File("Donors.txt");
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header != null) lines.add(header);

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\s*,\\s*");
                if (data[0].equalsIgnoreCase(donorID)) {
                    double prevQty = Double.parseDouble(data[4]);
                    double newQty = prevQty + donatedQty;
                    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    data[4] = String.valueOf(newQty);
                    data[5] = today;
                }
                lines.add(String.join(", ", data));
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String s : lines) {
                    bw.write(s);
                    bw.newLine();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
