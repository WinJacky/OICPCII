package edu.seu.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Standard {
    private String type;
    private double occupancy;
    private double infrastructure;
    private double depository;
    private double production;
    private double traffic;
    private double green;

    public Standard(){

    }

    @Override
    public String toString() {
        return "Weight{" +
                "type=" + type +
                ", occupancy='" + occupancy + '\'' +
                ", infrastructure='" + infrastructure + '\'' +
                ", depository='" + depository + '\'' +
                ", production=" + production +
                ", traffic=" + traffic +
                ", green=" + green +
                '}';
    }
}
