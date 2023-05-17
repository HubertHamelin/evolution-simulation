public class CustomMath {

    public static double sigmoid(double t) {
        return 1 / (1 + Math.pow(Math.E, (-1 * t)));
    }

    public static double relu(double t) {
        return Math.min(1.0, Math.abs(t));
    }

}
