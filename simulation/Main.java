import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Selection criteria = new Selection();
        // World world = new World(128, 128, 1000, 128, 4, 4, 100, 100, criteria, 0.01); // Selection -> Central
        World world = new World(128, 128, 1000, 32, 3, 3,
                1000, 100, criteria, 0.01); // Selection -> Right Side
        world.live();
        // world.testBrainLive();
    }
}