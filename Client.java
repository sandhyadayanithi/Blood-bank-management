import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private String name;
    private String location;
    private String bloodType;
    private double quantity;
    private int urgency; // days needed within
    private String status; // Pending / Allocated / Completed
    private String requestDate;

    // Constructor
    public Client() {
        this.status = "Pending";
        this.requestDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Method to collect details interactively
    public void getDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your details:");

        System.out.print("Name: ");
        this.name = sc.nextLine();

        System.out.print("Location (City): ");
        this.location = sc.nextLine();

        System.out.print("Blood Type needed: ");
        this.bloodType = sc.nextLine();

        System.out.print("Quantity of blood needed in units (1 unit = 450ml): ");
        this.quantity = sc.nextDouble();

        System.out.print("Number of days within which blood is required: ");
        this.urgency = sc.nextInt();

        System.out.println("New client details added successfully.");
        // Don't close Scanner if main program may need it again
    }

    // Convert client details to a single CSV-like line
    @Override
    public String toString() {
        return String.join(", ",
                name,
                location,
                bloodType,
                String.valueOf(quantity),
                String.valueOf(urgency),
                status,
                requestDate);
    }

    // Method to append client details to file
    public void appendToFile() {
        File file = new File("Client.txt");
        boolean isNewFile = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (isNewFile) {
                writer.write("Name, Location, BloodType, Quantity(units), Urgency(days), Status, RequestDate");
                writer.newLine();
            }
            writer.write(this.toString());
            writer.newLine();
            System.out.println("✅ Client details appended to Client.txt successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Optional: method to update status later (Allocated / Completed)
    public void updateStatus(String newStatus) {
        if (newStatus.equalsIgnoreCase("Pending") ||
            newStatus.equalsIgnoreCase("Allocated") ||
            newStatus.equalsIgnoreCase("Completed")) {
            this.status = newStatus;
        } else {
            System.out.println("❌ Invalid status. Valid options: Pending / Allocated / Completed.");
        }
    }

    // For quick testing
    public static void main(String[] args) {
        Client c = new Client();
        c.getDetails();
        c.appendToFile();
    }
}
