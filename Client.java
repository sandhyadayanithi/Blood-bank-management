import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client {
    private static final String SERVER_IP = "127.0.0.1"; // change if server is remote
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- CLIENT MENU ---");
            System.out.println("1. Register as New Client");
            System.out.println("2. Check Request Status");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    registerClient(sc);
                    break;
                case 2:
                    checkStatus(sc);
                    break;
                case 3:
                    System.out.println("Exiting client...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 3);

        sc.close();
    }

    // --- Register new client (sends data to server) ---
    private static void registerClient(Scanner sc) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.print("Enter your name: ");
            String name = sc.nextLine();
            System.out.print("Enter your location (city): ");
            String location = sc.nextLine();
            System.out.print("Enter blood type needed: ");
            String bloodType = sc.nextLine();
            System.out.print("Enter quantity (units): ");
            double quantity = sc.nextDouble();
            System.out.print("Enter urgency (days): ");
            int urgency = sc.nextInt();
            sc.nextLine(); // consume newline

            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String clientData = String.join(", ",
                    name, location, bloodType,
                    String.valueOf(quantity),
                    String.valueOf(urgency),
                    "Pending", date);

            // Send registration request to server
            out.println("REGISTER");
            out.println(clientData);

            // Receive server response
            String response = in.readLine();
            System.out.println(response);  // shows server message

            // Extra clear message for the client
            System.out.println("Your request is pending. Please wait for admin allocation.");

        } catch (IOException e) {
            System.out.println(" Could not connect to server. Make sure AdminServer is running.");
        }
    }

    // --- Check status of existing client request ---
    private static void checkStatus(Scanner sc) {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("STATUS:" + name);
            System.out.println(in.readLine());

        } catch (IOException e) {
            System.out.println(" Could not connect to server. Make sure AdminServer is running.");
        }
    }
}
