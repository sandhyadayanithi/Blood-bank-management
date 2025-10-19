public class BloodSearch {

    public static BloodBankRecord findNearestBank(String startLocation, String bloodGroup, int requiredQty) {
        LocationNode temp = BloodBankData.locationList.getHead();

        // Find the starting city in the linked list
        while (temp != null && !temp.locationName.equalsIgnoreCase(startLocation)) {
            temp = temp.next;
        }

        // If not found, start from Chennai (head)
        if (temp == null) temp = BloodBankData.locationList.getHead();

        // Traverse locations
        while (temp != null) {
            for (BloodBankRecord bank : temp.banks) {
                if (bank.bloodGroup.equalsIgnoreCase(bloodGroup) && bank.quantity >= requiredQty) {
                    System.out.println("✅ Found in " + temp.locationName + " (" + bank.bankName + ")");
                    return bank;
                }
            }
            temp = temp.next;
        }

        System.out.println(" Blood type " + bloodGroup + " not available in any nearby location.");
        return null;
    }
}
