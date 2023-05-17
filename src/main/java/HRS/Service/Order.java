package HRS.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
public class Order {
    @Getter @Setter
    protected double summary;
    @Getter @Setter
    protected String receiving;
    @Getter @Setter
    protected String address;

    public Order(double summary, String receiving) {
        this.summary = summary;
        this.receiving = receiving;
    }

    @Override
    public String toString() {
        return "Order{" + "summary=" + summary + ", receiving='" + receiving + '\'' + ", address='" + address + '\'' + '}';
    }
}
