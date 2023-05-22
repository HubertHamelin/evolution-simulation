import Directions.Direction;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.lang.Math;

public class World {

    int xDim;  // Dimension of the world along the x-axis.
    int yDim;  // Dimension of the world along the y-axis.
    int [][] map;  // Agents IDs are located on the world map. Indexes start at 1, empty is 0.
    int nbInitialAgents;  // Number of agents to spawn at each generation.
    int nbConnections;  // Maximal number of active connections in the agents' brain.
    int nbHiddenLayers;  // Maximal number of hidden type neuron layers in the agent's brain.
    int nbNeuronsPerLayer; // Maximal number of active hidden neurons in the hidden layers of the agent's brain.
    int generation;  // Counter of the current generation of the world since its started.
    ArrayList<Agent> agents;  // All agents currently spawned in the world.
    int stepsPerGeneration;
    int limitGeneration;
    Selection criteria;
    GUI gui;
    double mutationRate;

    public World(int x, int y, int nbInitialAgents, int nbConnections, int nbHiddenLayers, int nbNeuronsPerLayer,
                 int limitGeneration, int stepsPerGeneration, Selection criteria, double mutationRate) {
        this.xDim = x;
        this.yDim = y;
        this.map = new int[y][x];
        this.nbInitialAgents = nbInitialAgents;
        this.nbConnections = nbConnections;
        this.nbHiddenLayers = nbHiddenLayers;
        this.nbNeuronsPerLayer = nbNeuronsPerLayer;
        this.agents = new ArrayList<>();
        this.generation = 0;
        this.limitGeneration = limitGeneration;
        this.stepsPerGeneration = stepsPerGeneration;
        this.mutationRate = mutationRate;
        this.criteria = criteria;
        this.gui = new GUI(this);
    }

    public void live() throws IOException {
        this.populateWorld();
        int[] killsPerGeneration = new int[this.limitGeneration];
        for (int g = 0; g < this.limitGeneration; g++) {
            System.out.println(" --- GENERATION " + this.generation + " ---");
            this.gui.display("gen_" + this.generation);
            for (int s = 0; s < this.stepsPerGeneration; s++) {
                for (Agent agent : this.agents) {
                    agent.updateSensorsState(this.map, s);
                    agent.act(this.map);
                    this.updateMap(agent.id);
                }
                this.gui.display("gen_" + this.generation + "_step_" + s);
            }

            // Take a snapshot of the ending of current generation
            this.gui.saveImage(this.gui.frame, this.gui.dim * this.gui.factor, this.gui.dim * this.gui.factor, "gen_" + this.generation);

            this.generation += 1;
            this.agents = this.criteria.select(this.agents, this.map);
            killsPerGeneration[g] = this.criteria.kills;
            this.reproduce();
            this.resetAllLocations();
            this.resetAllDirections();
        }
        System.out.println(Arrays.toString(killsPerGeneration));

        // Most common brain
        String fileContent = this.getMostCommonBrain().printBrain();
        Path filename = Path.of("./most_common_brain.csv");
        Files.writeString(filename, fileContent);
    }

    public void testBrainLive() {
        this.populateWorld();
    }

    private Brain2 getMostCommonBrain() {
        // For each agent, compute brain similarity to all other agents. Save top 5 similar in a Dictionary
        int top = 3;
        Dictionary<Agent, Agent[]> brainsSimilarity = new Hashtable<>();
        for (Agent agent : this.agents) {
            double[] similarity = new double[this.agents.size()];
            for (Agent otherAgent : this.agents) {
                if (agent.id != otherAgent.id) {  // Don't forget to exclude itself of the top 5 !
                    // Compute brains similarity between agent and otherAgent
                    similarity[otherAgent.id - 1] = this.computeBrainsSimilarity(agent.brain.connectionsID, otherAgent.brain.connectionsID);
                }
            }

            // Save top 5 in the dictionary : can use sort but would need a custom implementation (keep original index)
             Agent[] topSimilar = new Agent[top];
            for (int i = 0; i < top; i++) {
                Agent topAgent = this.getMaxSimilarity(similarity);
                topSimilar[i] = topAgent;
                similarity[topAgent.id - 1] = 0;
            }
            brainsSimilarity.put(agent, topSimilar);
        }

        // Count appearances in similarity Dictionary of all agents
        int[] similarityCounts = new int[this.agents.size()];
        Enumeration<Agent> keys = brainsSimilarity.keys();
        while (keys.hasMoreElements()) {
            Agent key = keys.nextElement();
            for (Agent agent : brainsSimilarity.get(key)) {
                similarityCounts[agent.id - 1] += 1;
            }
        }

        // Return max(count) agent's brain
        int maxCount = 0;
        int maxCountIndex = -1;
        for (int i = 0; i < similarityCounts.length; i++) {
            if (similarityCounts[i] > maxCount) {
                maxCount = similarityCounts[i];
                maxCountIndex = i;
            }
        }
        System.out.println("MaxCount for brain similarity = " + maxCount);
        return this.agents.get(maxCountIndex).brain;
    }

    private double computeBrainsSimilarity(ArrayList<String> brain1, ArrayList<String> brain2) {
        int count = 0;
        for (String connectionID : brain1) {
            if (brain2.contains(connectionID)){
                count++;
            }
        }
        return (double) count / brain1.size();
    }

    private Agent getMaxSimilarity(double[] similarity) {
        // Indexes in the similarity array correspond to its agent's ID
        double max = 0.0;
        int maxID = -1;
        for (int i = 0; i < similarity.length; i++) {
            if (similarity[i] > max) {
                max = similarity[i];
                maxID = i;
            }
        }
        return this.agents.get(maxID);
    }

    private void updateMap(int agentID) {
        int agentIndex = agentID - 1;
        this.map[this.agents.get(agentIndex).previousLoc.y][this.agents.get(agentIndex).previousLoc.x] = 0;
        this.map[this.agents.get(agentIndex).location.y][this.agents.get(agentIndex).location.x] = agentID;
    }

    private void visualize() {
        // Print a rough estimate of the world current state in the console
        System.out.println(" ");
        for (int[] row : this.map) {
            System.out.println(Arrays.toString(row));
        }
    }

    private void populateWorld() {
        for (int i = 0; i < this.nbInitialAgents; i++) {
            // Create a new agent
            Agent agent = this.generateAgent();
            this.agents.add(agent);

            // Update world map
            this.map[agent.location.y][agent.location.x] = agent.id;
        }
    }

    private Point getRandomUnoccupiedLocation() {
        // Is there at least one unoccupied location on the world map ?
        ArrayList<Point> unoccupiedLocations = new ArrayList<Point>();
        for (int j = 0; j < this.map.length; j++) {
            for (int i = 0; i < this.map[j].length; i++) {
                if (map[j][i] == 0) {
                    Point location = new Point(i, j);
                    unoccupiedLocations.add(location);
                }
            }
        }
        // Return a random pick from the subset of unoccupied locations if it exists.
        int randomIndex = (int) (Math.random() * unoccupiedLocations.size());
        return unoccupiedLocations.get(randomIndex);
    }

    private Agent generateAgent() {
        //Spawn an agent at a random unoccupied location on the world map.
        int agentId = this.agents.size() + 1;
        Point location = this.getRandomUnoccupiedLocation();
        return new Agent(agentId, location, this.nbConnections, this.nbHiddenLayers, this.nbNeuronsPerLayer);
    }

    private void repopulate() {
        // test : random repopulation -> this shows that with too much randomness, there is a limit to survivability
        int nbMissingAgents = this.nbInitialAgents - this.agents.size();
        // reset indexes
        for (int i = 0; i < this.agents.size(); i++) {
            this.agents.get(i).id = i + 1;
        }
        // repopulate
        for (int i = 0; i < nbMissingAgents; i++) {
            Agent newAgent = this.generateAgent();
            this.agents.add(newAgent);
        }
    }

    private void replicate() {
        // TODO: for generation studies, do not reset indexes
        // Self replication and occasional mutation
        // Shuffle remaining agents
        Collections.shuffle(this.agents);
        // Reset indexes
        for (int i = 0; i < this.agents.size(); i++) {
            this.agents.get(i).id = i + 1;
        }
        // Self replicate in a loop until the target quota is filled
        int nbMissingAgents = this.nbInitialAgents - this.agents.size();
        for (int i = 0; i < nbMissingAgents; i++) {
            Agent newAgent = this.deepClone(this.agents.get(i));
            double mutate = Math.random();
            if (mutate <= this.mutationRate)
                newAgent.mutation();
            this.agents.add(newAgent);
        }
    }

    private void reproduce() {
        // shuffle remaining agents -> all reproductions will be random
        ArrayList<Agent> oldAgents = (ArrayList<Agent>) this.agents.clone();
        Collections.shuffle(oldAgents);
        this.agents.clear();
        // 2-len window to chain reproductions then put them at the back of the list (as a moving stack)
        for (int i = 0; i < this.nbInitialAgents; i++){
            String[] childGenome = new String[this.nbConnections];

            // Create a randomly combined genome from both parents
            for (int c = 0; c < this.nbConnections; c++) {
                double randomFlip = Math.random();
                if (randomFlip < 0.5)
                    childGenome[c] = oldAgents.get(0).genome.sequencesHexa[c];
                else
                    childGenome[c] = oldAgents.get(1).genome.sequencesHexa[c];
            }

            // Create a new agent and reset his genome and brain with the inherited one.
            Agent child = this.generateAgent();
            child.genome = new Genome(childGenome);
            child.brain = new Brain2(this.nbHiddenLayers, this.nbNeuronsPerLayer, child.sensors, child.genome);

            // Apply mutation
            double mutate = Math.random();
            if (mutate <= this.mutationRate)
                child.mutation();

            // Add child to world agents
            this.agents.add(child);

            // Move parents at the back of the stack
            oldAgents.add(oldAgents.get(0));
            oldAgents.add(oldAgents.get(1));
            oldAgents.remove(0);
            oldAgents.remove(1);
        }
    }

    private void resetAllLocations() {
        // Randomly shuffle all agents locations on the world map.
        this.map = new int[this.yDim][this.xDim];
        for (Agent agent : this.agents) {
            agent.location = this.getRandomUnoccupiedLocation();
            this.updateMap(agent.id);
        }
    }

    private Agent deepClone(Agent object){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(bais);
            return (Agent) objectInputStream.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void resetAllDirections() {
        // Reset all agents direction
        for (Agent agent : this.agents) {
            agent.direction = Direction.values()[(int) (Math.random() * Direction.values().length)];
        }
    }
}
