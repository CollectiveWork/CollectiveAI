package ga.projects.P8;

import ga.GeneticAlgorithm;
import ga.windows.DrawPanel;
import ga.windows.MainWindow;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * Created by AndreiMadalin on 4/9/14.
 */
public class P8 extends GeneticAlgorithm {
    public P8(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
        super(m, n, it, uc, um, elitism, geneSize);
    }

    public static void main(String[] args) {

        MainWindow window = new MainWindow("AG", 700, 700);

        P8 ga = new P8(8, 20, 1500, .8, .333, true, 8);
        ga.setWindow(window);
        ga.drawDataset();

        SimpleMatrix fittest;
        try {
            ga.start(false, "singlePointCrossover");
            fittest = ga.getFittest();
            System.out.println(fittest);
            System.out.println("Fitness: " + ga.getFitness(fittest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawDataset() {
        ArrayList<ArrayList<ArrayList<Integer>>> points = new ArrayList<>();
        ArrayList<ArrayList<Integer>> class1 = new ArrayList<>();
        ArrayList<ArrayList<Integer>> class2 = new ArrayList<>();
        ArrayList<Integer> temp = null;

        // generate class 1 points
        for (double x = 0; x <= 2; x += .01) {
            for (double y = 0; y <= 2; y += .01) {
                temp = new ArrayList<>();
                temp.add((int) (x *(DrawPanel.X_AXIS_FIRST_X_COORD + DrawPanel.SECOND_LENGHT))); // x position
                temp.add((int) (f1(x) * (DrawPanel.Y_AXIS_FIRST_Y_COORD + DrawPanel.SECOND_LENGHT))); // y position
                class1.add(temp);
            }
        }
        points.add(class1);

        // generate class 2 points
        for (double x = 1; x <= 3; x += .01) {
            for (double y = 0; y <= 2; y += .01) {
                temp = new ArrayList<>();
                temp.add((int) (x * (DrawPanel.X_AXIS_FIRST_X_COORD + DrawPanel.SECOND_LENGHT))); // x position
                temp.add((int) (f2(x) * (DrawPanel.Y_AXIS_FIRST_Y_COORD + DrawPanel.SECOND_LENGHT))); // y position
                class2.add(temp);
            }
        }
        points.add(class2);

        window.draw_panel.setPoints2Draw(points);
        window.draw_panel.repaint();
    }

    double f1(double x) {
        if (x <= 0)
            return 0;
        if (0 < x && x <= 1)
            return x;
        if (1 < x && x <= 2)
            return 2 - x;
        if (x > 2)
            return 0;

        return 0;
    }

    double f2(double x) {
        if (x <= 1)
            return 0;
        if (1 < x && x <= 2)
            return x - 1;
        if (2 < x && x <= 3)
            return 3 - x;
        if (x > 3)
            return 0;

        return 0;
    }

    double f(double y) {
        if (y <= 0)
            return 0;
        if (0 < y && y <= 1)
            return y;
        if (1 < y && y <= 2)
            return 2 - y;
        if (y > 2)
            return 0;

        return 0;
    }

    public double fitness(SimpleMatrix chromosome) {
        return 0;
    }

}
