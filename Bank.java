import java.util.*;

public class Bank {
    public int ID;
    public String name;
    public String location;
    public ArrayList<Blood> bloodType;

    public Bank() {
        this.bloodType = new ArrayList<>();
    }

    public Bank(int ID, String name, String location, ArrayList<Blood> bloodType) {
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
        this.ID = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter the name of the bank:");
        this.name = sc.nextLine();
        System.out.println("Enter the location of the bank (city):");
        this.location = sc.nextLine();
        System.out.println("Enter the number of blood types available:");
        int n = sc.nextInt();
        sc.nextLine();
        for (int i = 0; i < n; i++) {
            Blood blood = new Blood();
            System.out.println("Enter the blood type:");
            blood.type = sc.nextLine();
            System.out.println("Enter the quantity available of the blood type:");
            blood.availableQty = sc.nextDouble();
            sc.nextLine();
            bloodType.add(blood);
        }
        System.out.println(" New bank details added successfully.");
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