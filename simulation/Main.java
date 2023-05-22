import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Selection criteria = new Selection();
        // World world = new World(128, 128, 1000, 128, 4, 4, 1000, 150, criteria, 0.01); // Selection -> Central
        World world = new World(128, 128, 1000, 128, 4, 4, 500, 250, criteria, 0.01); // Selection -> Corners
        // World world = new World(128, 128, 1000, 8, 1, 1, 500, 100, criteria, 0.001); // Selection -> Right Side
        world.live();
        // world.testBrainLive();
    }
}