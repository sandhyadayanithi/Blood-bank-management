import java.util.ArrayList;

public class LocationNode {
    public String locationName;
    public ArrayList<Bank> banks;
    public LocationNode next;

    public LocationNode(String locationName) {
        this.locationName = locationName;
        this.banks = new ArrayList<>();
        this.next = null;
    }

    public void addBank(Bank bank) {
        banks.add(bank);
    }
}