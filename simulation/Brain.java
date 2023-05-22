import Sensors.Sensor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class Brain {


    // TODO : handling computations using Matrices will be faster.
    // TODO: nbConnections inherited from the agent's genome. At first we will use a simple default brain.
    // TODO: current implementation might work for small brains, but more efficiency is needed to expand its size.

    Neuron[] sensoryNeurons;
    Neuron[] hiddenNeurons;
    Neuron[] actionNeurons;
    int nbConnections;
    ArrayList<ArrayList<Neuron>> layers;

    public Brain(int nbConnections, int nbHiddenNeurons, Sensor[] sensors) {
        this.generateSensoryNeurons(sensors);
        this.generateHiddenNeurons(nbHiddenNeurons);
        this.generateActionNeurons();
        this.nbConnections = nbConnections;
        this.generateRandomConnections();
        this.simplifyNetwork();
        this.layers = new ArrayList<>();
        this.organizeLayers();
    }

    public Action think(Sensor[] sensors) {
        // Instantiate the Input layer : set a sensor input vector
        ArrayList<Double> inputs = new ArrayList<>();
        for (Sensor sensor : sensors) {
            inputs.add(sensor.value);
        }

        // Do the feedForward process through the neural network
        this.feedForward(inputs);

        // TODO: simplification: pick whichever action that has the highest output
        // TODO: there might be a need for a random default action
        double maxActivation = 0.0;
        Action activatedAction = Action.Mfd;
        for (Neuron neuron : this.actionNeurons) {
            if (neuron.output > maxActivation) {
                maxActivation = neuron.output;
            }
        }
        for (Neuron neuron : this.actionNeurons) {
            if (neuron.output == maxActivation) {
                activatedAction = neuron.action;
            }
        }

        return activatedAction;
    }

    public void exportBrain() {
        System.out.println(" ------------- ");
        for (Neuron neuron : this.hiddenNeurons) {
            for (Connection connection : neuron.connections) {
                System.out.println(connection.neuron.name + " " + neuron.name + " " + connection.weight);
            }
        }
        for (Neuron neuron : this.actionNeurons) {
            for (Connection connection : neuron.connections) {
                System.out.println(connection.neuron.name + " " + neuron.name + " " + connection.weight);
            }
        }
    }

    private void organizeLayers() {
        // Use established connections to sort each neuron to its corresponding layer. Mandatory for feed-forward op.
        // +1 layer for each source connection of a hidden to another hidden
        // TODO: not a viable method. Might work only for small sized brains, but it is incorrect.

        // First layer are the sensory neurons
        ArrayList<Neuron> firstLayer = new ArrayList<>(Arrays.asList(this.sensoryNeurons));
        this.layers.add(firstLayer);

        // Add consecutive layers for the different levels of hidden neurons
        // Let's assume that there will not be more than 3 hidden layers
        // TODO: this is so not optimised.
        int nbHiddenLayers = 3;
        for (int i = 1; i <= nbHiddenLayers; i++) {
            ArrayList<Neuron> hiddenLayer = new ArrayList<>();
            for (Neuron neuron : this.hiddenNeurons) {
                int countConnectionsToHidden = 0;
                for (Connection connection : neuron.connections){
                    if (connection.neuron.type == NeuronType.HIDDEN) {
                        countConnectionsToHidden += 1;
                    }
                }
                if (countConnectionsToHidden == i) {
                    hiddenLayer.add(neuron);
                }
            }
            this.layers.add(hiddenLayer);
        }
        // Remove any hidden layer that has no neurons in it
        ArrayList<ArrayList<Neuron>> layersToRemove = new ArrayList<>();
        for (ArrayList<Neuron> layer : this.layers) {
            if (layer.size() == 0) {
                layersToRemove.add(layer);
            }
        }
        for (ArrayList<Neuron> layer : layersToRemove) {
            this.layers.remove(layer);
        }

        // Last layer is the action neurons
        ArrayList<Neuron> lastLayer = new ArrayList<>(Arrays.asList(this.actionNeurons));
        this.layers.add(lastLayer);
    }

    private void feedForward(ArrayList<Double> inputs) {
        for (ArrayList<Neuron> neurons : this.layers) {
            for (int i = 0; i < neurons.size(); i++) {
                if (neurons.get(i).type != NeuronType.SENSORY) {
                    neurons.get(i).feedForward();
                } else {
                    neurons.get(i).feedForward(inputs.get(i));
                }
            }
        }
    }

    private void generateSensoryNeurons(Sensor[] sensors) {
        this.sensoryNeurons = new Neuron[sensors.length];
        for (int i = 0; i < sensors.length; i++) {
            this.sensoryNeurons[i] = new Neuron(sensors[i].name, NeuronType.SENSORY);
        }
    }

    private void generateHiddenNeurons(int nbHiddenNeurons) {
        this.hiddenNeurons = new Neuron[nbHiddenNeurons];
        for (int i = 0; i < nbHiddenNeurons; i++) {
            this.hiddenNeurons[i] = new Neuron("N" + i, NeuronType.HIDDEN);
        }
    }

    private void generateActionNeurons() {
        this.actionNeurons = new Neuron[Action.values().length];
        for (int i = 0; i < Action.values().length; i++) {
            this.actionNeurons[i] = new Neuron(String.valueOf(Action.values()[i]), NeuronType.ACTION);
            this.actionNeurons[i].action = Action.values()[i];
        }
    }

    private void generateRandomConnections() {
        // TODO : check if connection already exists ?
        for (int i = 0; i < this.nbConnections; i++) {
            Connection connection;

            // Generate a random weight for initialisation : TODO: this will be done by the Genome translation.
            double randomWeight = Math.random() * 4;
            if (Math.random() >= 0.5){
                randomWeight *= -1;
            }

            // Pick at random a sensoryNeuron or a hiddenNeuron that will be the source of the connection
            int randomStartIndex = (int) (Math.random() * (this.sensoryNeurons.length + this.hiddenNeurons.length));
            if (randomStartIndex >= this.sensoryNeurons.length) {
                randomStartIndex -= this.sensoryNeurons.length;
                connection = new Connection(randomWeight, this.hiddenNeurons[randomStartIndex]);
            } else {
                connection = new Connection(randomWeight, this.sensoryNeurons[randomStartIndex]);
            }

            // Pick at random either a hiddenNeuron or an actionNeuron that will be at the end of the connection.
            int randomEndIndex = (int) (Math.random() * (this.hiddenNeurons.length + this.actionNeurons.length));
            if (randomEndIndex >= this.hiddenNeurons.length) {
                randomEndIndex -= this.hiddenNeurons.length;
                this.actionNeurons[randomEndIndex].addConnection(connection);
            } else {
                this.hiddenNeurons[randomEndIndex].addConnection(connection);
            }
        }
    }

    private void simplifyNetwork() {
        // TODO: remove duplicates connections
        for (Neuron neuron : this.actionNeurons) {
            this.removeDuplicateConnections(neuron);
        }
        for (Neuron neuron : this.hiddenNeurons) {
            this.removeDuplicateConnections(neuron);
        }

        // TODO: implement a better design using graphs and trees (parent/child nodes)
        // Get rid of connections between hidden and action neuron if hidden has no path to sensory
        for (Neuron neuron : this.actionNeurons) {
            ArrayList<Connection> connectionsToDrop = new ArrayList<>();
            for (Connection connection : neuron.connections) {
                if (connection.neuron.type == NeuronType.HIDDEN && !hasPathToSensory(connection.neuron)) {
                    connectionsToDrop.add(connection);
                }
            }
            for (Connection connection : connectionsToDrop) {
                neuron.connections.remove(connection);
            }
        }

        // TODO: Get rid of connections between hidden and sensory if hidden has no path to action
        // Get all hidden neurons that have a direct connection to an actionNeuron
        ArrayList<String> hiddenWithDirectPathToAction = new ArrayList<>();
        for (Neuron neuron : this.actionNeurons) {
            for (Connection connection : neuron.connections) {
                if (connection.neuron.type == NeuronType.HIDDEN) {
                    hiddenWithDirectPathToAction.add(connection.neuron.name);
                }
            }
        }
        for (Neuron neuron : this.hiddenNeurons) {
            ArrayList<Connection> connectionsToDrop = new ArrayList<>();
            if (!this.hasPathToAction(neuron, hiddenWithDirectPathToAction)) {
                for (Connection connection : neuron.connections) {
                    if (connection.neuron.type == NeuronType.SENSORY) {
                        connectionsToDrop.add(connection);
                    }
                }
                for (Connection connection : connectionsToDrop) {
                    neuron.connections.remove(connection);
                }
            }
        }

        // TODO: Get rid of connections between hidden and hidden if it is its only connection.
        for (Neuron neuron : this.hiddenNeurons) {
            if (neuron.connections.size() == 1 && neuron.connections.get(0).neuron.type == NeuronType.HIDDEN) {
                neuron.connections.remove(0);
            }
        }

    }

    private boolean hasPathToSensory(Neuron neuron) {
        // TODO: use a marker system ? hasPathToSensory boolean as a Neuron attributes
        // TODO: unfinished : path must be followed by connections !! not neurons
        if (neuron.connections.size() > 0) {
            for (Connection connection : neuron.connections) {
                if (!Objects.equals(connection.neuron.name, neuron.name)) {
                    if (connection.neuron.type == NeuronType.SENSORY) {
                        return true;
                    } else {
                        return this.hasPathToSensory(connection.neuron);
                    }
                }
            }
        }
        return false;
    }

    private boolean hasPathToAction(Neuron neuron, ArrayList<String> hiddenWithDirectPathToAction) {
        // TODO: use a marker system ? hasPathToAction boolean as a Neuron attributes
        for (String name : hiddenWithDirectPathToAction) {
            if (Objects.equals(name, neuron.name)) {
                return true;
            }
        }
        return false;
    }

    private void removeDuplicateConnections(Neuron neuron) {
        ArrayList<Connection> connectionsToDrop = new ArrayList<>();
        for (Connection connection : neuron.connections) {
            for (Connection con : neuron.connections) {
                if (!connectionsToDrop.contains(con) && !connectionsToDrop.contains(connection) && connection != con
                        && connection.neuron == con.neuron) {
                    connectionsToDrop.add(con);
                }
            }
        }
        for (Connection connection : connectionsToDrop) {
            neuron.connections.remove(connection);
        }
    }
}
