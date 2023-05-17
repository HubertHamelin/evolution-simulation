import Directions.Direction;
import Sensors.*;
import java.awt.Point;
import java.io.Serializable;

enum Action {
    Mfd, // MOVEFORWARD
    Mrv, // MOVEREVERSE
    Mri, // MOVERIGHT
    Mlf, // MOVELEFT
    Mea, // MOVEEAST
    Mwe, // MOVEWEST
    Mno, // MOVENORTH
    Mso, // MOVESOUTH
    Mra, // MOVERANDOM
    Dno // DONOTHING
}


public class Agent implements Serializable {
    // Agent that will populate the environment
    int id;  // Each agent is identified by a unique ID, starting at 1
    Point location;  // [x, y] coordinates of the agent into the environment
    Brain2 brain;  // Artificial brain of the agent, responsible for its actions
    Sensor[] sensors;  // Sensors enable the agent to interpret its environment
    Direction direction;  // Current forward movement direction of the agent
    Point previousLoc;  // Location of the agent at the previous step
    Genome genome;  // hexadecimal sequence that determines the connections in the agent's brain
    int nbHiddenLayers;
    int nbNeuronsPerLayer;


    public Agent(int id, Point location, int nbConnections, int nbHiddenLayers, int nbNeuronsPerLayer) {
        this.id = id;
        this.location = location;
        this.previousLoc = location;
        this.nbHiddenLayers = nbHiddenLayers;
        this.nbNeuronsPerLayer = nbNeuronsPerLayer;
        this.direction = this.initiateRandomDirection();
        this.sensors = this.initiateSensors();
        this.genome = new Genome(nbConnections, 6);
        this.brain = new Brain2(nbHiddenLayers, nbNeuronsPerLayer, this.sensors, this.genome);
    }

    public void mutation() {
        this.genome.mutation();
        this.brain = new Brain2(this.nbHiddenLayers, this.nbNeuronsPerLayer, this.sensors, this.genome);
    }

    public void updateSensorsState(int[][] environment, int time){
        for (Sensor sensor : this.sensors) {
            sensor.update(environment, time);
        }
    }

    public void act(int[][] environment){
        // TODO: there must be some code refactorisation here.
        // TODO : if there are no possible actions (no active neuron in the brain), Action.DONOTHING default
        // TODO: think -> Neuron ? => name AND output, so we can vary between -1/1 actions

        Action action = this.brain.think(this.sensors);
        this.previousLoc = this.location;
        this.doAction(action, environment);
    }

    private void doAction(Action action, int[][] environment) {
        if (action == Action.Mfd) {
            Point newLoc = this.moveForward();
            if (this.checkMove(environment, newLoc)) {
                this.location = newLoc;
            }
        } else if (action == Action.Mlf) {
            this.rotateLeft();
            Point newLoc = this.moveForward();
            if (this.checkMove(environment, newLoc)) {
                this.location = newLoc;
            }
        } else if (action == Action.Mri) {
            this.rotateRight();
            Point newLoc = this.moveForward();
            if (this.checkMove(environment, newLoc)) {
                this.location = newLoc;
            }
        } else if (action == Action.Mrv) {
            this.rotateRight();
            this.rotateRight();
            Point newLoc = this.moveForward();
            if (this.checkMove(environment, newLoc)) {
                this.location = newLoc;
            }
        } else if (action == Action.Mea) {
            this.direction = Direction.EAST;
            Point newLoc = this.moveForward();
            if (this.checkMove(environment, newLoc)) {
                this.location = newLoc;
            }
        } else if (action == Action.Mwe) {
            this.direction = Direction.WEST;
            Point newLoc = this.moveForward();
            if (this.checkMove(environment, newLoc)) {
                this.location = newLoc;
            }
        } else if (action == Action.Mno) {
            this.direction = Direction.NORTH;
            Point newLoc = this.moveForward();
            if (this.checkMove(environment, newLoc)) {
                this.location = newLoc;
            }
        } else if (action == Action.Mso) {
            this.direction = Direction.SOUTH;
            Point newLoc = this.moveForward();
            if (this.checkMove(environment, newLoc)) {
                this.location = newLoc;
            }
        } else if (action == Action.Mra) {
            int randomIndex = (int) (Math.random() * 5);
            switch (randomIndex) {
                case 0:
                    this.doAction(Action.Mno, environment);
                case 1:
                    this.doAction(Action.Mso, environment);
                case 2:
                    this.doAction(Action.Mea, environment);
                case 3:
                    this.doAction(Action.Mwe, environment);
                case 4:
                    this.doAction(Action.Dno, environment);
            }
        }
        // else if (action == Action.DONOTHING), well do nothing :)
    }

    private Sensor[] initiateSensors() {
        Sensor[] sensors = new Sensor[10];
        sensors[0] = new LxSensor(this.id);
        sensors[1] = new LySensor(this.id);
        sensors[2] = new BDxSensor(this.id);
        sensors[3] = new BDySensor(this.id);
        sensors[4] = new LMxSensor(this.location, this.previousLoc);
        sensors[5] = new LMySensor(this.location, this.previousLoc);
        sensors[6] = new BlrSensor(this.location, this.direction);
        sensors[7] = new BfdSensor(this.location, this.direction);
        sensors[8] = new Oscillator();
        sensors[9] = new PopSensor(this.location);
        return sensors;
    }

    private Direction initiateRandomDirection() {
        int randomIndex = (int) (Math.random() * Direction.values().length);
        Direction[] directions = Direction.values();
        return directions[randomIndex];
    }

    // Possible actions by the agent : // TODO: Use a better implementation ?
    private Point moveForward() {
        // TODO: manage blockage between agents and with world elements. if applyAction Do else DoNothing ?
        Point newLoc;
        if (this.direction == Direction.NORTH) {
            newLoc = new Point(this.location.x, this.location.y - 1);
        } else if (this.direction == Direction.SOUTH) {
            newLoc = new Point(this.location.x, this.location.y + 1);
        } else if (this.direction == Direction.EAST) {
            newLoc = new Point(this.location.x + 1, this.location.y);
        } else {
            newLoc = new Point(this.location.x - 1, this.location.y);
        }
        return newLoc;
    }

    private void rotateRight() {
        // Changes the forward direction of the agent after a +90° rotation.
        if (this.direction == Direction.NORTH) {
            this.direction = Direction.EAST;
        } else if (this.direction == Direction.EAST) {
            this.direction = Direction.SOUTH;
        } else if (this.direction == Direction.SOUTH) {
            this.direction = Direction.WEST;
        } else if (this.direction == Direction.WEST) {
            this.direction = Direction.NORTH;
        }
    }

    private void rotateLeft() {
        // Changes the forward direction of the agent after a -90° rotation.
        if (this.direction == Direction.NORTH) {
            this.direction = Direction.WEST;
        } else if (this.direction == Direction.EAST) {
            this.direction = Direction.NORTH;
        } else if (this.direction == Direction.SOUTH) {
            this.direction = Direction.EAST;
        } else if (this.direction == Direction.WEST) {
            this.direction = Direction.SOUTH;
        }
    }

    // TODO : may have an error, regarding GUI bugs
    private boolean checkMove(int[][] environment, Point newLoc) {
        return newLoc.y >= 0 && newLoc.y < environment.length && newLoc.x >= 0 && newLoc.x < environment[0].length
                && environment[newLoc.y][newLoc.x] == 0;
    }
}
