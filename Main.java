import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== BLOOD BANK SYSTEM MAIN MENU ===");
            System.out.println("1. Start Admin Server");
            System.out.println("2. Start Client Application");
            System.out.println("3. Start Donor Application");
            System.out.println("4. Exit");
            System.out.print("Select option: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    if (!isPortAvailable(5000)) {
                        System.out.println("AdminServer already running on port 5000. Start client instead.");
                        break;
                    }
                    System.out.println("Starting Admin Server...");
                    AdminServer.adminMain(sc);
                    break;

                case 2:
                    System.out.println("Starting Client Application...");
                    Client.clientMain(sc);
                    break;

                case 3:
                    System.out.println("Starting Donor Application...");
                    Donor.donorMain(sc);
                    break;

                case 4:
                    System.out.println("Exiting system...");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 4);
        sc.close();
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
