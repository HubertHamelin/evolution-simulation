package Sensors;
import java.awt.*;

public class PopSensor extends Sensor{

    Point location;

    public PopSensor(Point location) {
        this.name = "Pop";
        this.value = 0.0;
        this.location = location;
    }

    public void update(int[][] environment, int time) {
        // 0 is alone, 1 is max-crowd
        this.value = (double) this.checkSurroundings(environment) / 8;
    }

    private int checkSurroundings(int[][] environment) {
        // Check all 8 (max) possible cells around the agent's location. Increment if occupied by another agent.
        int neighboursCount = 0;

        if (this.location.y > 0 && environment[this.location.y - 1][this.location.x] > 0)  // up
            neighboursCount++;
        if (this.location.y > 0 && this.location.x < environment[0].length - 1 && environment[this.location.y - 1][this.location.x + 1] > 0)  // up-right
            neighboursCount++;
        if (this.location.x < environment[0].length - 1 && environment[this.location.y][this.location.x + 1] > 0)  // right
            neighboursCount++;
        if (this.location.y < environment.length - 1 && this.location.x < environment[0].length - 1 && environment[this.location.y + 1][this.location.x + 1] > 0)  // down-right
            neighboursCount++;
        if (this.location.y < environment.length - 1 && environment[this.location.y + 1][this.location.x] > 0) // down
            neighboursCount++;
        if (this.location.y < environment.length - 1 && this.location.x > 0 && environment[this.location.y + 1][this.location.x - 1] > 0) // down-left
            neighboursCount++;
        if (this.location.x > 0 && environment[this.location.y][this.location.x - 1] > 0)  // left
            neighboursCount++;
        if (this.location.y > 0 && this.location.x > 0 && environment[this.location.y - 1][this.location.x - 1] > 0)  // up-left
            neighboursCount++;

        return neighboursCount;
    }
}
