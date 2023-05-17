import java.util.ArrayList;

public class Selection {

    // "Those who reproduces do, those who doesn't, doesn't"
    //TODO : visualize Selection criteria on the world map GUI
    int kills = 0;

    public ArrayList<Agent> select(ArrayList<Agent> agents, int[][] map) {
        int initialPopulation = agents.size();
        ArrayList<Agent> agentsAlive = this.killOutsideMapCenter(agents, map);
        this.kills = initialPopulation - agentsAlive.size();
        System.out.println("Killed " + this.kills + " agents");
        return agentsAlive;
    }

    private ArrayList<Agent> killLeftMapHalf(ArrayList<Agent> agents, int[][] map) {
        /*
            This selection criteria applies based on the agents location at the end of the generation.
            Every agent on the right side of the map will get to reproduce, the other half dies.
         */
        int mapXaxisLimit = (map[0].length / 2);
        ArrayList<Agent> agentsToRemove = new ArrayList<>();
        for (Agent agent : agents) {
            if (agent.location.x < mapXaxisLimit) {
                agentsToRemove.add(agent);
            }
        }
        for (Agent agent : agentsToRemove) {
            agents.remove(agent);
        }
        return agents;
    }

    private ArrayList<Agent> killOutsideMapCenter(ArrayList<Agent> agents, int[][] map) {
        /*
            This selection criteria applies based on the agents location at the end of the generation.
            Every agent outside a circle drawn in the middle of the map die, those inside reproduce.
         */
        double radius = (double) map.length / 4;
        int centerX = map[0].length / 2;
        int centerY = map.length / 2;
        ArrayList<Agent> agentsToRemove = new ArrayList<>();
        for (Agent agent : agents) {
            double distance = Math.sqrt((agent.location.x - centerX)*(agent.location.x - centerX) + (agent.location.y - centerY)*(agent.location.y - centerY));
            if (distance > radius)
                agentsToRemove.add(agent);
        }
        for (Agent agent : agentsToRemove) {
            agents.remove(agent);
        }
        return agents;
    }

    private ArrayList<Agent> killOutsideMapCorners(ArrayList<Agent> agents, int[][] map) {
        double factor = 0.15;
        int xMargin = (int) (factor * map[0].length);
        int yMargin = (int) (factor * map.length);
        ArrayList<Agent> agentsToRemove = new ArrayList<>();
        for (Agent agent : agents) {
            if (agent.location.x <= xMargin && agent.location.y > yMargin && agent.location.y < map.length - yMargin)
                agentsToRemove.add(agent);
            else if (agent.location.x >= map[0].length - xMargin && agent.location.y > yMargin && agent.location.y < map.length - yMargin)
                agentsToRemove.add(agent);
            else if (agent.location.x > xMargin && agent.location.x < map[0].length - xMargin)
                agentsToRemove.add(agent);
        }
        for (Agent agent : agentsToRemove) {
            agents.remove(agent);
        }
        return agents;
    }

    private ArrayList<Agent> killOutsideLeftRightMapSides(ArrayList<Agent> agents, int[][] map) {
        double factor = 0.2;
        int leftXlimit = (int) (factor * map[0].length);
        int rightXlimit = map[0].length - leftXlimit;
        ArrayList<Agent> agentsToRemove = new ArrayList<>();
        for (Agent agent : agents) {
            if (agent.location.x > leftXlimit && agent.location.x < rightXlimit)
                agentsToRemove.add(agent);
        }
        for (Agent agent : agentsToRemove) {
            agents.remove(agent);
        }
        return agents;
    }
}
