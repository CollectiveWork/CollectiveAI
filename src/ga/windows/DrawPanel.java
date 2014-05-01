package ga.windows;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Created by AndreiMadalin on 4/10/14.
 */
public class DrawPanel extends JPanel {
    ArrayList<ArrayList<ArrayList<Double>>> classes = new ArrayList<>();
    ArrayList<Color> class_color = new ArrayList<>();
    String chromosome;
    int H, b;
    double diag;
    boolean painLine = false;
    // x-axis coord constants
    public static final int X_AXIS_FIRST_X_COORD = 500;
    public static final int X_AXIS_SECOND_X_COORD = 600;
    public static final int X_AXIS_Y_COORD = 600;

    // y-axis coord constants
    public static final int Y_AXIS_FIRST_Y_COORD = 300;
    public static final int Y_AXIS_SECOND_Y_COORD = 600;
    public static final int Y_AXIS_X_COORD = 300;

    //arrows of axis are represented with "hipotenuse" of
    //triangle
    // now we are define length of cathetas of that triangle
    public static final int FIRST_LENGHT = 10;
    public static final int SECOND_LENGHT = 5;

    // size of start coordinate lenght
    public static final int ORIGIN_COORDINATE_LENGHT = 6;

    // distance of coordinate strings from axis
    public static final int AXIS_STRING_DISTANCE = 20;
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (classes.size() > 0) {
            //drawCoordinateSystem(g);
            drawPoints(g);
            if(painLine)
                drawLines(g);
        }
    }

    private void drawPoints(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(0.0, getHeight());
        g2d.scale(1.0, -1.0);

        generateColors();
        for (int k = 0; k < classes.size(); k++) {
            g2d.setColor(class_color.get(k));
            for (ArrayList<Double> point : classes.get(k)) {
                if(k == 0)
                    g2d.drawRect((int)(X_AXIS_FIRST_X_COORD + point.get(0)), (int)(Y_AXIS_FIRST_Y_COORD + AXIS_STRING_DISTANCE + point.get(1)),5,5);
                if(k == 1)
                    g2d.drawOval((int)(X_AXIS_FIRST_X_COORD + point.get(0)), (int)(Y_AXIS_FIRST_Y_COORD + AXIS_STRING_DISTANCE + point.get(1)),5,5);
            }
        }
    }

    synchronized private void drawLines(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        int numHyperplanes = H;
        double angle;
        double d;
        String hyperplane;

        double d_min = 0;
        for (int h = 0; h < numHyperplanes; h++) {
            hyperplane = chromosome.substring(h*2*b,(h+1)*2*b);
            angle = Integer.parseInt(hyperplane.substring(0,b), 2);
            angle *= (2 * Math.PI) / Math.pow(2, b);

            d = Integer.parseInt(hyperplane.substring(b,2*b), 2);
            d *= diag / Math.pow(2, b) + d_min;

            int f = (int) (d *30);
            int startX = (int) (X_AXIS_FIRST_X_COORD +  f);
            int startY = (int) (Y_AXIS_FIRST_Y_COORD + f);
            int endX   = (int) (X_AXIS_FIRST_X_COORD + 1000*Math.sin(angle - Math.PI / 2) +  f);
            int endY   = (int) (Y_AXIS_FIRST_Y_COORD + 1000*Math.cos(angle - Math.PI / 2) + f);
            g2d.drawLine(startX,startY,endX,endY);

             endX   = (int) (X_AXIS_FIRST_X_COORD +  1000*Math.sin(angle - 3*Math.PI / 2) +  f);
             endY   = (int) (Y_AXIS_FIRST_Y_COORD  + 1000*Math.cos(angle - 3*Math.PI / 2) + f);
            g2d.drawLine(startX,startY,endX,endY);
        }
    }

    private void drawCoordinateSystem(Graphics g){
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // x-axis
        g2.drawLine(X_AXIS_FIRST_X_COORD, X_AXIS_Y_COORD,
                X_AXIS_SECOND_X_COORD, X_AXIS_Y_COORD);
        // y-axis
        g2.drawLine(Y_AXIS_X_COORD, Y_AXIS_FIRST_Y_COORD,
                Y_AXIS_X_COORD, Y_AXIS_SECOND_Y_COORD);

        // x-axis arrow
        g2.drawLine(X_AXIS_SECOND_X_COORD - FIRST_LENGHT,
                X_AXIS_Y_COORD - SECOND_LENGHT,
                X_AXIS_SECOND_X_COORD, X_AXIS_Y_COORD);
        g2.drawLine(X_AXIS_SECOND_X_COORD - FIRST_LENGHT,
                X_AXIS_Y_COORD + SECOND_LENGHT,
                X_AXIS_SECOND_X_COORD, X_AXIS_Y_COORD);

        // y-axis arrow
        g2.drawLine(Y_AXIS_X_COORD - SECOND_LENGHT,
                Y_AXIS_FIRST_Y_COORD + FIRST_LENGHT,
                Y_AXIS_X_COORD, Y_AXIS_FIRST_Y_COORD);
        g2.drawLine(Y_AXIS_X_COORD + SECOND_LENGHT,
                Y_AXIS_FIRST_Y_COORD + FIRST_LENGHT,
                Y_AXIS_X_COORD, Y_AXIS_FIRST_Y_COORD);

        // draw origin Point
        g2.fillOval(
                X_AXIS_FIRST_X_COORD - (ORIGIN_COORDINATE_LENGHT / 2),
                Y_AXIS_SECOND_Y_COORD - (ORIGIN_COORDINATE_LENGHT / 2),
                ORIGIN_COORDINATE_LENGHT, ORIGIN_COORDINATE_LENGHT);

        // draw text "X" and draw text "Y"
        g2.drawString("X", X_AXIS_SECOND_X_COORD - AXIS_STRING_DISTANCE / 2,
                X_AXIS_Y_COORD + AXIS_STRING_DISTANCE);
        g2.drawString("Y", Y_AXIS_X_COORD - AXIS_STRING_DISTANCE,
                Y_AXIS_FIRST_Y_COORD + AXIS_STRING_DISTANCE / 2);
        g2.drawString("(0, 0)", X_AXIS_FIRST_X_COORD - AXIS_STRING_DISTANCE,
                Y_AXIS_SECOND_Y_COORD + AXIS_STRING_DISTANCE);

        // numerate axis
        int xCoordNumbers = 10;
        int yCoordNumbers = 10;
        int xLength = (X_AXIS_SECOND_X_COORD - X_AXIS_FIRST_X_COORD)
                / xCoordNumbers;
        int yLength = (Y_AXIS_SECOND_Y_COORD - Y_AXIS_FIRST_Y_COORD)
                / yCoordNumbers;

        // draw x-axis numbers
        for(int i = 1; i < xCoordNumbers; i++) {
            g2.drawLine(X_AXIS_FIRST_X_COORD + (i * xLength),
                    X_AXIS_Y_COORD - SECOND_LENGHT,
                    X_AXIS_FIRST_X_COORD + (i * xLength),
                    X_AXIS_Y_COORD + SECOND_LENGHT);
            g2.drawString(Integer.toString(i),
                    X_AXIS_FIRST_X_COORD + (i * xLength) - 3,
                    X_AXIS_Y_COORD + AXIS_STRING_DISTANCE);
        }

        //draw y-axis numbers
        for(int i = 1; i < yCoordNumbers; i++) {
            g2.drawLine(Y_AXIS_X_COORD - SECOND_LENGHT,
                    Y_AXIS_SECOND_Y_COORD - (i * yLength),
                    Y_AXIS_X_COORD + SECOND_LENGHT,
                    Y_AXIS_SECOND_Y_COORD - (i * yLength));
            g2.drawString(Integer.toString(i),
                    Y_AXIS_X_COORD - AXIS_STRING_DISTANCE,
                    Y_AXIS_SECOND_Y_COORD - (i * yLength));
        }
    }

    private void generateColors() {
//        for (int i = 0; i < classes.size(); i++) {
//            class_color.add(i, new Color((int) (Math.random() * 0xFFFFFF)));
//        }

        class_color.add(0, new Color(1, 35,178));
        class_color.add(1, new Color(112, 109,0));
    }

    public void setPoints2Draw(ArrayList<ArrayList<ArrayList<Double>>> classes) {
        this.classes = classes;
    }

    public void setVariables(int H, double diag, int b){
        this.H = H;
        this.diag = diag;
        this.b = b;
    }

    public void setChromosome(String chromosome){
        painLine = false;
        this.chromosome = chromosome;
        painLine = true;
        repaint();
    }
}
