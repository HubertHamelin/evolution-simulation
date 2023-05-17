package Sensors;
import Directions.Direction;
import java.awt.*;

public class BfdSensor extends Sensor {

    Point location;
    Direction direction;

    public BfdSensor(Point location, Direction direction) {
        this.name = "Bfd";
        this.value = 0.0;
        this.location = location;
        this.direction = direction;
    }

    public void update(int[][] environment, int time) {
        /*
        1 is a blockade, 0 means nothing in the focus axis.
         */
        // TODO : a larger range of values depending on the intensity of the blockade ?
        // TODO : define global constants for what constitues a block or an empty space on the world map
        // TODO : maybe the relative left/rights can be updated directly using a single public method from agent class
        if (this.direction == Direction.NORTH) {
            if (this.location.y - 1 < 0 || environment[this.location.y - 1][this.location.x] != 0)
                this.value = 1;
            else
                this.value = 0;
        } else if (this.direction == Direction.SOUTH) {
            if (this.location.y + 1 >= environment.length || environment[this.location.y + 1][this.location.x] != 0)
                this.value = 1;
            else
                this.value = 0;
        } else if (this.direction == Direction.EAST) {
            if (this.location.x + 1 >= environment[0].length || environment[this.location.y][this.location.x + 1] != 0)
                this.value = 1;
            else
                this.value = 0;
        } else if (this.direction == Direction.WEST) {
            if (this.location.x - 1 < 0 || environment[this.location.y][this.location.x - 1] != 0)
                this.value = 1;
            else
                this.value = 0;
        }
    }
}
