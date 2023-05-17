package Sensors;
import java.awt.*;

public class LMxSensor extends Sensor{

    Point location;
    Point previousLocation;

    public LMxSensor(Point location, Point previousLocation) {
        this.name = "LMx";
        this.value = 0.0;
        this.location = location;
        this.previousLocation = previousLocation;
    }

    public void update(int[][] environment, int time) {
        if (this.previousLocation.x != this.location.x)
            this.value = 1.0;
        else
            this.value = 0.0;
    }
}
