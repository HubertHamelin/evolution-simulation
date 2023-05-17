import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GUI {

    JFrame frame;
    int dim;
    int factor;

    public GUI(World world) {
        this.dim = world.xDim;
        this.factor = 5;
        this.frame = new JFrame("Evolution");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(this.frame.getContentPane(), world);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    public void display(String filename) {
        this.frame.repaint();
        // TODO: wait to slow down animation and favor correct image capture ?
        // this.saveImage(this.frame, this.dim * this.factor, this.dim * this.factor, filename);
    }

    private void addComponentsToPane(Container pane, World world) {
        // TODO: use a buffer ?
        // TODO: add a TOP panel with Generation number
        JPanel mapPanel = new MapPanel(this.dim, this.dim);
        mapPanel.setPreferredSize(new Dimension(this.dim * this.factor, this.dim * this.factor));
        //pane.add(mapPanel);

        JPanel agentsPanel = new AgentsPanel(this.dim, this.dim, world.agents);
        agentsPanel.setPreferredSize(new Dimension(this.dim * this.factor, this.dim * this.factor));
        pane.add(agentsPanel);
    }

    private void saveImage(JFrame frame, int width, int height, String filename) {
        try
        {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            frame.paint(graphics2D);
            String repoPath = "./images/";
            ImageIO.write(image,"jpeg", new File(repoPath + filename + ".jpeg"));
        }
        catch(Exception exception)
        {
            System.out.println(exception);
        }
    }

    public void plotSelectionCriteria () {

    }
}
