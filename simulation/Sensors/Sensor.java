package Sensors;
import java.io.Serializable;

public abstract class Sensor implements Serializable {
    /*
        Lx : east/west world location
        Ly : north/south world location
        BDx : east/west border distance (closer is 1)
        BDy : north/south border distance (closer is 1)
        LMx : last movement on the x-axis
        LMy : last movement on the y-axis
        Blr : blockade left-right
        Bfd : blockade forward
        Osc : oscillation of frequency f
        Pop : population density in surroundings
     */
    public String name;
    public double value;
    protected int agentID;

    public abstract void update(int[][] environment, int time);
}