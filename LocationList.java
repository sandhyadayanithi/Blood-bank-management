public class LocationList {
    private LocationNode head;

    public void addLocation(String name) {
        LocationNode newNode = new LocationNode(name);
        if (head == null) {
            head = newNode;
            return;
        }
        LocationNode temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = newNode;
    }

    public LocationNode getHead() {
        return head;
    }

    // Debug print
    public void display() {
        LocationNode temp = head;
        while (temp != null) {
            System.out.print(temp.locationName + " -> ");
            temp = temp.next;
        }
        System.out.println("null");
    }
}
