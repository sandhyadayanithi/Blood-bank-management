import java.util.ArrayList;

public class LocationNode {
    String locationName;
    ArrayList<BloodBankRecord> banks;
    LocationNode next;

    public LocationNode(String locationName) {
        this.locationName = locationName;
        this.banks = new ArrayList<>();
        this.next = null;
    }

    public void addBank(BloodBankRecord bank) {
        banks.add(bank);
    }
}
