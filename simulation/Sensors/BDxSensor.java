package Sensors;

public class BDxSensor extends Sensor {

    public BDxSensor(int agentID) {
        this.name = "BDx";
        this.value = 0.0;
        this.agentID = agentID;
    }

    public void update(int[][] environment, int time) {
        // Locate where the agent is on the world map x-axis.
        int xLoc = this.findAgentXaxis(environment);
        // Compute relative distance to the eastern border of the world map.
        int xAxisLen = environment[0].length;
        this.value = (double) (xAxisLen - xLoc) / xAxisLen;
    }

    private int findAgentXaxis(int[][] environment) {
        for (int[] ints : environment) {
            for (int i = 0; i < ints.length; i++) {
                if (ints[i] == this.agentID) {
                    return i;
                }
            }
        }
        return 0;
    }
}
