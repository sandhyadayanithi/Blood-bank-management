public class BloodSearch {
    public static Bank findNearestBank(String startLocation, String bloodType, 
                                       double requiredQty, LocationList locationList) {
        LocationNode temp = locationList.getHead();
        
        // Find the starting city in the linked list
        while (temp != null && !temp.locationName.equalsIgnoreCase(startLocation)) {
            temp = temp.next;
        }
        
        // If not found, start from head (first location)
        if (temp == null) {
            temp = locationList.getHead();
        }
        
        // Traverse locations to find blood
        while (temp != null) {
            for (Bank bank : temp.banks) {
                // Check if this bank has the required blood type with enough quantity
                for (Blood blood : bank.bloodType) {
                    if (blood.type.equalsIgnoreCase(bloodType) && blood.availableQty >= requiredQty) {
                        System.out.println(" Found " + bloodType + " in " + temp.locationName + 
                                         " (" + bank.name + ")");
                        return bank;
                    }
                }
            }
            temp = temp.next;
        }
        
        System.out.println(" Blood type " + bloodType + " not available in any nearby location.");
        return null;
    }
}