import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AgentsPanel extends JPanel {

    ArrayList<Agent> agents;
    int rows;
    int cols;

    public AgentsPanel(int rows, int cols, ArrayList<Agent> agents) {
        this.rows = rows;
        this.cols = cols;
        this.agents = agents;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.drawAgents(g);
    }

    private void drawAgents(Graphics g) {
        // TODO: add a unique color per agent
        // TODO: colors should be attributed relative to genome similarity.
        int margin = (int) (0.25 * ((double) getSize().width / this.cols) / 2);
        int diameter = (int) ((double) getSize().width / this.cols) - 2 * margin;

        for (Agent agent : this.agents) {
            Point panelLoc = this.mapToPanelCoord(agent.location);
            Color color = this.getColor(agent);
            g.setColor(color);
            g.drawOval(panelLoc.x + margin, panelLoc.y + margin, diameter, diameter);
            g.fillOval(panelLoc.x + margin, panelLoc.y + margin, diameter, diameter);
        }
    }

    private Point mapToPanelCoord(Point mapLoc) {
        int rowHt = getSize().height / this.rows;
        int rowWid = getSize().width / this.cols;
        return new Point(mapLoc.x * rowWid, mapLoc.y * rowHt);
    }

    /*
    private Color getColor(Agent agent, int azfef) {
        // Get average of all RGB components of each gene composing the genome
        int avgR = 0;
        int avgG = 0;
        int avgB = 0;
        for (String gene : agent.genome.sequencesHexa) {
            avgR += Integer.parseInt("" + gene.charAt(0) + gene.charAt(1), 16);
            avgG += Integer.parseInt("" + gene.charAt(2) + gene.charAt(3), 16);
            avgB += Integer.parseInt("" + gene.charAt(4) + gene.charAt(5), 16);
        }
        avgR /= agent.genome.sequencesHexa.length;
        avgG /= agent.genome.sequencesHexa.length;
        avgB /= agent.genome.sequencesHexa.length;
        return new Color(avgR, avgG, avgB);
    }
    */

    private Color getColor(Agent agent) {
        // sort genome genes alphabetically
        // Arrays.sort(agent.genome.sequencesHexa);
        // mix colors 2 by 2
        Color color = new Color(Integer.parseInt(agent.genome.sequencesHexa[0], 16));
        for (int i = 1; i < agent.genome.sequencesHexa.length; i++) {
            Color otherColor = new Color(Integer.parseInt(agent.genome.sequencesHexa[i], 16));
            color = this.mix(color, otherColor, 0.25);
        }
        return color;
    }

    public Color mix(Color a, Color b, double percent) {
        return new Color((int) (a.getRed() * percent + b.getRed() * (1.0 - percent)),
                (int) (a.getGreen() * percent + b.getGreen() * (1.0 - percent)),
                (int) (a.getBlue() * percent + b.getBlue() * (1.0 - percent)));
    }
}
