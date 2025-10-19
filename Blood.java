public class Blood {
    public String type;
    public double availableQty;

    public Blood() {
        this.type = "";
        this.availableQty = 0;
    }

    public Blood(String type, double availableQty) {
        this.type = type;
        this.availableQty = availableQty;
    }

    @Override
    public String toString() {
        return "Blood{" +
                "type='" + type + '\'' +
                ", availableQty=" + availableQty +
                '}';
    }
}