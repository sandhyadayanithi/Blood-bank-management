import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

class BloodBankRecord {
    String bankID;
    String bankName;
    String bloodGroup;
    int quantity;
    String bankLocation;
    String contactNumber;
    String email;
    String lastUpdatedDate;

    public BloodBankRecord(String bankID, String bankName, String bloodGroup, int quantity,
                           String bankLocation, String contactNumber, String email, String lastUpdatedDate) {
        this.bankID = bankID;
        this.bankName = bankName;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.bankLocation = bankLocation;
        this.contactNumber = contactNumber;
        this.email = email;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @Override
    public String toString() {
        return String.join(", ",
                bankID, bankName, bloodGroup, String.valueOf(quantity),
                bankLocation, contactNumber, email, lastUpdatedDate);
    }
}

public class Data {
    public static void main(String[] args) {
        List<String> bloodGroups = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        List<String> bankNames = Arrays.asList("LifeLine", "RedHope", "VitalFlow", "PureBlood", "CityCare",
                                               "HealTrust", "SafeDonor", "UnityBlood", "HeartSource", "PrimeLife");
        List<String> tnLocations = Arrays.asList(
                "Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Erode", "Tirunelveli",
                "Vellore", "Thoothukudi", "Thanjavur", "Dindigul", "Nagercoil", "Karur", "Namakkal",
                "Cuddalore", "Tirupur", "Villupuram", "Kanchipuram", "Sivaganga", "Virudhunagar"
        );

        Random rand = new Random();
        List<BloodBankRecord> records = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 1; i <= 100; i++) {
            String bankID = String.format("BB%03d", i);
            String bankName = bankNames.get(rand.nextInt(bankNames.size()));
            String bloodGroup = bloodGroups.get(rand.nextInt(bloodGroups.size()));
            int quantity = rand.nextInt(50) + 1; // 1–50 units
            String location = tnLocations.get(rand.nextInt(tnLocations.size()));
            String contact = "+91-" + (9000000000L + rand.nextInt(999999999 - 900000000)) ;
            String email = bankName.toLowerCase() + "@bloodbank.in";
            String date = sdf.format(new Date(System.currentTimeMillis() - rand.nextInt(365) * 24L * 60 * 60 * 1000));

            records.add(new BloodBankRecord(bankID, bankName, bloodGroup, quantity, location, contact, email, date));
        }

        // Sort by Blood Group, then by Bank Name
        records.sort(Comparator.comparing((BloodBankRecord r) -> r.bloodGroup)
                               .thenComparing(r -> r.bankName));

        // Write to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("BloodBank.txt"))) {
            writer.write("BankID, BankName, BloodGroup, Quantity, BankLocation, ContactNumber, Email, LastUpdatedDate");
            writer.newLine();
            for (BloodBankRecord r : records) {
                writer.write(r.toString());
                writer.newLine();
            }
            System.out.println("✅ BloodBank.txt created successfully with 100 Tamil Nadu–based records.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
