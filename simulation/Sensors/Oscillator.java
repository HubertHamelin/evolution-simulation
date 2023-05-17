package Sensors;

public class Oscillator extends Sensor {

    int frequency;

    public Oscillator() {
        // TODO: the frequency could be randomized ?
        this.name = "Osc";
        this.value = 0.0;
        // this.frequency = (int) (Math.random() * 4) + 1;
        this.frequency = 4;
    }

    public void update(int[][] environment, int time) {
        /*
        Alternate values on a sinusoid curve, speed of the oscillation depends on the frequency property.
         */
        this.value = Math.sin((double) time / this.frequency);
    }
}
