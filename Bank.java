import java.util.*;

public class Bank {
    public String ID;
    public String name;
    public String location;
    public String contactNumber;
    public String email;
    public ArrayList<Blood> bloodType;

    public Bank() {
        this.bloodType = new ArrayList<>();
    }

    public Bank(String ID, String name, String location, ArrayList<Blood> bloodType) {
        this.ID = ID;
        this.name = name;
        this.location = location;
        this.bloodType = bloodType != null ? bloodType : new ArrayList<>();
    }

    // for adding new bank
    public void getDetails() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the bank details:");

        System.out.print("Enter the bank ID: ");
        this.ID = sc.nextLine().trim();

        System.out.print("Enter the bank name: ");
        this.name = sc.nextLine().trim();

        System.out.print("Enter the bank location (city): ");
        this.location = sc.nextLine().trim();

        System.out.print("Enter contact number (e.g., +91-9876543210): ");
        this.contactNumber = sc.nextLine().trim();

        System.out.print("Enter email: ");
        this.email = sc.nextLine().trim();

        System.out.print("Enter how many blood types are available: ");
        int n;
        while (true) {
            try {
                n = Integer.parseInt(sc.nextLine().trim());
                if (n <= 0) {
                    System.out.print("Enter a valid number (>0): ");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }

        for (int i = 0; i < n; i++) {
            Blood blood = new Blood();

            System.out.print("Enter blood type " + (i + 1) + ": ");
            blood.type = sc.nextLine().trim();

            System.out.print("Enter quantity available for " + blood.type + ": ");
            while (true) {
                try {
                    blood.availableQty = Double.parseDouble(sc.nextLine().trim());
                    if (blood.availableQty < 0) {
                        System.out.print("Quantity cannot be negative. Enter again: ");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.print("Please enter a valid number: ");
                }
            }

            bloodType.add(blood);
        }

        System.out.println("New bank details added successfully.");
    }


    @Override
    public String toString() {
        return "Bank{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", bloodTypes=" + bloodType.size() +
                '}';
    }
}