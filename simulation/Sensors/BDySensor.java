package Sensors;

public class BDySensor extends Sensor{

    public BDySensor(int agentID) {
        this.name = "BDy";
        this.value = 0.0;
        this.agentID = agentID;
    }

    public void update(int[][] environment, int time) {
        // Locate where the agent is on the world map y-axis.
        int yLoc = this.findAgentYaxis(environment);
        // Compute relative location on the y-axis.
        int yAxisLen = environment.length;
        this.value = (double) (yAxisLen - yLoc) / yAxisLen;
    }

    private int findAgentYaxis(int[][] environment) {
        for (int j = 0; j < environment.length; j++) {
            for (int i = 0; i < environment[j].length; i++) {
                if (environment[j][i] == this.agentID) {
                    return j;
                }
            }
        }
        return 0;
    }
}
