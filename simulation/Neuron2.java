import java.io.Serializable;
import java.util.ArrayList;

public class Neuron2 implements Serializable {

    String name;
    ArrayList<Neuron2> inputs;
    ArrayList<Neuron2> outputs;
    double output;
    ArrayList<Double> weights;
    boolean linkedSensory;
    boolean linkedAction;
    NeuronType type;

    public Neuron2(String name, NeuronType type) {
        this.name = name;
        this.type = type;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.weights = new ArrayList<>();
    }
    public void feedForward() {
        // Weighted sum of all connections to the neuron
        double weightedSum = 0;
        for (int i = 0; i < this.inputs.size(); i++)
            weightedSum += this.weights.get(i) * this.inputs.get(i).output;
        // Return the result after processing by the neuron activation function
        this.output = this.activationFunction(weightedSum);
    }

    public void feedForward(double input) {
        this.output = this.activationFunction(input);
    }

    private double activationFunction(double weightedSum) {
        double activation;
        if (this.type == NeuronType.SENSORY) {
            // activation = CustomMath.sigmoid(weightedSum);
            activation = CustomMath.relu(weightedSum);
        } else {
            activation = Math.tanh(weightedSum);
        }
        return activation;
    }

}
