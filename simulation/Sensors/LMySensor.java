package Sensors;

import java.awt.*;

public class LMySensor extends Sensor{

    Point location;
    Point previousLocation;

    public LMySensor(Point location, Point previousLocation) {
        this.name = "LMy";
        this.value = 0.0;
        this.location = location;
        this.previousLocation = previousLocation;
    }

    public void update(int[][] environment, int time) {
        if (this.previousLocation.y != this.location.y)
            this.value = 1.0;
        else
            this.value = 0.0;
    }
}
