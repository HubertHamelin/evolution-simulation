import Sensors.Sensor;
import java.io.Serializable;
import java.util.ArrayList;

public class Brain2 implements Serializable {

    // ERROR: it appears that only one neuron per hidden layer is active in converging simulations

    ArrayList<Neuron2>[] layers;
    int nbHiddenLayers;
    int nbNeuronsPerLayer;
    ArrayList<String> connectionsID;

    public Brain2(int nbHiddenLayers, int nbNeuronsPerLayer, Sensor[] sensors, Genome genome) {
        this.nbHiddenLayers = nbHiddenLayers;
        this.nbNeuronsPerLayer = nbNeuronsPerLayer;
        this.initLayers(nbHiddenLayers, nbNeuronsPerLayer, sensors);
        this.initConnections(genome);
        this.simplifyNetwork();
        this.getConnectionsID();
    }

    public Action think(Sensor[] sensors) {
        // Instantiate the Input layer : set a sensor input vector
        ArrayList<Double> inputs = new ArrayList<>();
        for (Sensor sensor : sensors) {
            inputs.add(sensor.value);
        }

        // Do the feedForward process through the neural network
        this.feedForward(inputs);

        // TODO: simplification: pick whichever action that has the highest output: target -> probabilistic pick
        double maxActivation = 0.0;
        Action activatedAction = null;
        for (Neuron2 neuron : this.layers[this.nbHiddenLayers + 1]) {
            if (neuron.output > maxActivation) {
                maxActivation = neuron.output;
            }
        }
        for (int i = 0; i < this.layers[this.nbHiddenLayers + 1].size(); i++) {
            if (this.layers[this.nbHiddenLayers + 1].get(i).output == maxActivation) {
                activatedAction = Action.values()[i];
            }
        }
        return activatedAction;
    }

    public String printBrain() {
        StringBuilder brainPrinted = new StringBuilder();
        for (ArrayList<Neuron2> layer: this.layers) {
            for (Neuron2 neuron: layer) {
                for (int i = 0; i < neuron.inputs.size(); i++) {
                    brainPrinted.append(neuron.inputs.get(i).name).append(";").append(neuron.name).append(";").append(neuron.weights.get(i)).append("\n");
                }
            }
        }
        return brainPrinted.toString();
    }

    private void getConnectionsID() {
        // Add to connectionsID each connection as concat(src_name, dest_name)
        this.connectionsID = new ArrayList<>();
        for (ArrayList<Neuron2> layer : this.layers) {
            for (Neuron2 neuron : layer) {
                for (Neuron2 source : neuron.inputs) {
                    String connectionID = source.name + neuron.name;
                    this.connectionsID.add(connectionID);
                }
            }
        }
    }

    private void simplifyNetwork() {
        // Remove duplicate connections : only keep the first one.
        this.removeDuplicateConnections();
        // Establish for each neuron if it has a path to Sensory or Action neurons.
        this.explorePathToSensoryNeurons();
        this.explorePathToActionNeurons();
        // Remove any neuron that has not a path to both Sensory and Action neurons.
        for (ArrayList<Neuron2> layer : this.layers) {
            ArrayList<Neuron2> neuronsToRemove = new ArrayList<>();
            for (Neuron2 neuron : layer) {
                if (!(neuron.linkedSensory && neuron.linkedAction)) {
                    // Remove itself from its sources outputs
                    for (Neuron2 source : neuron.inputs)
                        source.outputs.remove(neuron);
                    // Remove itself from its destinations inputs
                    for (Neuron2 destination : neuron.outputs)
                        destination.inputs.remove(neuron);
                    // Remove it from current layer
                    neuronsToRemove.add(neuron);
                }
            }
            for (Neuron2 neuron : neuronsToRemove)
                layer.remove(neuron);
        }
    }

    private void feedForward(ArrayList<Double> sensorsInputs) {
        for (ArrayList<Neuron2> neurons : this.layers) {
            for (int i = 0; i < neurons.size(); i++) {
                if (neurons.get(i).type != NeuronType.SENSORY) {
                    neurons.get(i).feedForward();
                } else {
                    neurons.get(i).feedForward(sensorsInputs.get(i));
                }
            }
        }
    }

    private void initConnections(Genome genome) {
        // Parse the genome sequence to generate the agent's brain connections.
        for (String sequence : genome.sequences) {
            // 1st bit is the source type: 0 -> sensory neuron, 1 -> hidden neuron
            int sourceType = Integer.parseInt(String.valueOf(sequence.charAt(0)), 2);
            int nbPossibleSources;
            if (sourceType == 0) {
                nbPossibleSources = layers[0].size();
            } else {
                nbPossibleSources = this.nbHiddenLayers * this.nbNeuronsPerLayer;
            }
            // 7 next bits are the source ID
            int sourceID = Integer.parseInt(sequence.substring(1, 8), 2) % nbPossibleSources;
            // 9th bit is the destination type: 0 -> hidden neuron, 1 -> action neuron
            int destinationType = Integer.parseInt(String.valueOf(sequence.charAt(8)), 2);
            int nbPossibleDestinations;
            if (destinationType == 0) {
                nbPossibleDestinations = this.nbHiddenLayers * this.nbNeuronsPerLayer;
            } else {
                nbPossibleDestinations = layers[this.nbHiddenLayers + 1].size();
            }
            // 7 next bits are the destination ID
            int destinationID = Integer.parseInt(sequence.substring(9, 16), 2) % nbPossibleDestinations;
            // 16th bit is the sign of the associated weight, 0 -> positive, 1 -> negative
            int weightSign = Integer.parseInt(String.valueOf(sequence.charAt(16)), 2);
            // 7 last bits are for computing the weight value
            double weight = (double) Integer.parseInt(sequence.substring(17, 24), 2) / 32;
            if (weightSign == 1) {
                weight *= -1;
            }

            // Apply the genome parsing to the agent's brain
            Neuron2 source;
            if (sourceType == 0) {
                source = this.layers[0].get(sourceID);
            } else {
                int layerIndex = sourceID / this.nbNeuronsPerLayer;
                int internalIndex = sourceID % this.nbNeuronsPerLayer;
                source = this.layers[layerIndex + 1].get(internalIndex);
            }
            Neuron2 destination;
            if (destinationType == 0) {
                int layerIndex = destinationID / this.nbNeuronsPerLayer;
                int internalIndex = destinationID % this.nbNeuronsPerLayer;
                destination = this.layers[layerIndex + 1].get(internalIndex);
            } else {
                destination = this.layers[this.nbHiddenLayers + 1].get(destinationID);
            }
            source.outputs.add(destination);
            destination.inputs.add(source);
            destination.weights.add(weight);
        }
    }

    private void initLayers(int nbHiddenLayers, int nbNeuronsPerLayer, Sensor[] sensors) {
        // Initiate layers array
        this.layers = new ArrayList[nbHiddenLayers + 2];
        // Initiate Sensory neurons layer
        this.layers[0] = new ArrayList<>();
        for (Sensor sensor : sensors) {
            this.layers[0].add(new Neuron2(sensor.name, NeuronType.SENSORY));
        }
        // Initiate Hidden neurons layers
        for (int i = 0; i < nbHiddenLayers; i++) {
            this.layers[i + 1] = new ArrayList<>();
            for (int j = 0; j < nbNeuronsPerLayer; j++) {
                this.layers[i + 1].add(new Neuron2("N" + (i * nbHiddenLayers + j), NeuronType.HIDDEN));
            }
        }
        // Initiate Action neurons layer
        this.layers[nbHiddenLayers + 1] = new ArrayList<>();
        for (Action action : Action.values()) {
            this.layers[nbHiddenLayers + 1].add(new Neuron2(String.valueOf(action), NeuronType.ACTION));
        }
    }

    private void removeDuplicateConnections() {
        for (ArrayList<Neuron2> layer : this.layers) {
            for (Neuron2 neuron : layer) {
                ArrayList<Integer> indexToDrop = new ArrayList<>();
                ArrayList<Neuron2> inputsToDrop = new ArrayList<>();
                ArrayList<Double> weightsToDrop = new ArrayList<>();
                for (int j = 0; j < neuron.inputs.size(); j++) {
                    for (int i = 0; i < neuron.inputs.size(); i++) {
                        if (!indexToDrop.contains(j) && !indexToDrop.contains(i) &&
                                j != i && neuron.inputs.get(j) == neuron.inputs.get(i)) {
                            indexToDrop.add(i);
                            inputsToDrop.add(neuron.inputs.get(i));
                            weightsToDrop.add(neuron.weights.get(i));
                        }
                    }
                }
                for (Neuron2 input : inputsToDrop)
                    neuron.inputs.remove(input);
                for (Double weight : weightsToDrop)
                    neuron.weights.remove(weight);
            }
        }
    }

    private void explorePathToSensoryNeurons() {
        for (ArrayList<Neuron2> layer : this.layers) {
            for (Neuron2 neuron : layer) {
                boolean hasSensorySource = false;
                for (Neuron2 source : neuron.inputs) {
                    if (source.linkedSensory) {
                        hasSensorySource = true;
                        break;
                    }
                }
                neuron.linkedSensory = neuron.type == NeuronType.SENSORY || hasSensorySource;
            }
        }
    }

    private void explorePathToActionNeurons() {
        for (int i = this.layers.length - 1; i >= 0; i--) {
            for (Neuron2 neuron : this.layers[i]) {
                boolean hasActionDestination = false;
                for (Neuron2 destination : neuron.outputs) {
                    if (destination.linkedAction) {
                        hasActionDestination = true;
                        break;
                    }
                }
                neuron.linkedAction = neuron.type == NeuronType.ACTION || hasActionDestination;
            }
        }
    }
}
