import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel{

    int rows;
    int cols;

    public MapPanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.setBackground(Color.WHITE);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.drawWorldGrid(g);
    }

    private void drawWorldGrid(Graphics g) {
        // TODO: upper left corner out of frame ?
        int width = getSize().width;
        int height = getSize().height;

        // draw the rows
        int rowHt = height / this.rows;
        for (int i = 0; i <= this.rows; i++)
            g.drawLine(0, i * rowHt, width, i * rowHt);

        // draw the columns
        int rowWid = width / this.cols;
        for (int i = 0; i <= this.cols; i++)
            g.drawLine(i * rowWid, 0, i * rowWid, height);
    }
}