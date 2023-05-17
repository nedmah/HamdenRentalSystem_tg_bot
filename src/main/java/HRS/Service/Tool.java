package HRS.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class Tool {
    @Getter @Setter
    protected String id;
    @Getter @Setter
    protected String name;
    @Getter @Setter
    protected double pricePerHour;
    @Getter @Setter
    protected boolean deliverable;
    @Getter @Setter
    protected Type type;



    @Override
    public String toString() {
        return  id + ") " + name + '\'' + ", Цена в час: " + pricePerHour + ", Доставка: " + deliverable + ", Тип: " + type + "\n";
    }


}
