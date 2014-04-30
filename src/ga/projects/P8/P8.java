package ga.projects.P8;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import ga.GeneticAlgorithm;
import ga.windows.MainWindow;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by AndreiMadalin on 4/9/14.
 */

public class P8 extends GeneticAlgorithm {
    int datasetSize = 4;
    int class_matrix[][] = new int[(int) Math.pow(2,this.geneSize)][3];
    double dataset[][] = new double[datasetSize][3];
    int b;
    double x_max = Double.MIN_VALUE, x_min = Double.MAX_VALUE, y_max = Double.MIN_VALUE, y_min = Double.MAX_VALUE;

    public P8(int m, int n, int it, double uc, double um, boolean elitism, int geneSize, int b) {
        super(m, n, it, uc, um, elitism, geneSize);
        this.b = b;
    }

    public static void main(String[] args) {
        int b = 6;
        int H = 1;
        int chromosome_length = 2 * b * H;
        int population_size = 50;
        int iterations = 5000;

        MainWindow window = new MainWindow("AG", 1000, 700);

        P8 ga = new P8(chromosome_length, population_size, iterations, .8, .333, true, H, b);
        ga.setWindow(window);
        ga.drawDataset();
        ga.setVariables();

        SimpleMatrix fittest;
        try {
            ga.start(true, "singlePointCrossover");
            fittest = ga.getFittest();
            System.out.println(fittest);
            System.out.println("Fitness: " + ga.getFitness(fittest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawDataset() {
        ArrayList<ArrayList<ArrayList<Double>>> points = new ArrayList<>();
        ArrayList<ArrayList<Double>> class1 = new ArrayList<>();
        ArrayList<ArrayList<Double>> class2 = new ArrayList<>();
        ArrayList<Double> temp = null;
        int resize_factor = 30;
        double temp_point[];

        double u1[] = {0.0, 0.0};
        double u2[] = {1.0, 0.0};

        double E1[][] = {{1.0, 0.0}, {0.0, 1.0}};
        double E2[][] = {{4.0, 0.5}, {0.5, 4.0}};

        MultiGaussianDistribution mrd_c1 = new MultiGaussianDistribution(u1, E1);
        MultiGaussianDistribution mrd_c2 = new MultiGaussianDistribution(u2, E2);

        for (int i = 1; i <= datasetSize / 2; i++) {
            temp_point = mrd_c1.generate();
            temp = new ArrayList<>();
            temp.add(temp_point[0] * resize_factor); // x position
            temp.add(temp_point[1] * resize_factor); // y position
            class1.add(temp);
            dataset[i-1][0] = temp_point[0];
            if(temp_point[0] > x_max)
                x_max = temp_point[0];
            else if(temp_point[0] < x_min)
                x_min = temp_point[0];

            dataset[i-1][1] = temp_point[1];
            if(temp_point[1] > y_max)
                y_max = temp_point[1];
            else if(temp_point[1] < y_min)
                y_min = temp_point[1];

            dataset[i-1][2] = 1;
        }
        points.add(class1);

        for (int i = 1; i <= datasetSize / 2; i++) {
            temp_point = mrd_c2.generate();
            temp = new ArrayList<>();
            temp.add(temp_point[0] * resize_factor); // x position
            temp.add(temp_point[1] * resize_factor); // y position
            class2.add(temp);
            dataset[datasetSize / 2 + i-1][0] = temp_point[0];
            if(temp_point[0] > x_max)
                x_max = temp_point[0];
            else if(temp_point[0] < x_min)
                x_min = temp_point[0];

            dataset[datasetSize / 2 + i-1][1] = temp_point[1];
            if(temp_point[1] > y_max)
                y_max = temp_point[1];
            else if(temp_point[1] < y_min)
                y_min = temp_point[1];

            dataset[datasetSize / 2 + i-1][2] = 2;
        }
        points.add(class2);

        window.draw_panel.setPoints2Draw(points);
        window.draw_panel.repaint();
    }

    public void setVariables(){
        double diag = Math.sqrt(Math.pow((x_max - x_min),2) + Math.pow((y_max - y_min), 2));
        window.draw_panel.setVariables(geneSize, diag, b);
    }

    @Override
    protected SimpleMatrix convertChromosome(SimpleMatrix chromosome) {
        return chromosome;
    }

    public double fitness(SimpleMatrix chromosome) {
        return datasetSize - miss(chromosome);
    }

    private int miss(SimpleMatrix chromosome){
        for (int i = 0; i < class_matrix.length; i++) {
            for (int j = 0; j < class_matrix[i].length; j++) {
                class_matrix[i][j] = 0;
            }
        }

        int m;
        String sign_string;
        int dec;
        for (int i = 0; i < datasetSize; i++) {
            m = (int)dataset[i][2];
            sign_string = computeSignString(chromosome, dataset[i]);
            dec = Integer.parseInt(sign_string, 2);
            class_matrix[dec][m]++;
            class_matrix[dec][0] = 1;
        }

        int miss = 0;
        int max, sum = 0;
        for (int i = 0; i < Math.pow(2, geneSize); i++) {
            if(class_matrix[i][0] == 1){
                if(class_matrix[i][1] > class_matrix[i][2]){
                    max = class_matrix[i][1];
                    m = 1;
                }
                else{
                    max = class_matrix[i][2];
                    m = 2;
                }

                sum = 0;
                for (int j = 1; j <= 2; j++) {
                    sum += class_matrix[i][j];
                }
                miss += sum - max;
            }
        }

        return miss;
    }

    private String computeSignString(SimpleMatrix _chromosome, double[] point){
        String sign_string = "";
        String chromosome = getBinaryCromosom(_chromosome);
        int numHyperplanes = geneSize;
        double angle;
        double d;
        String hyperplane;

        double d_min = 0;
        double diag = Math.sqrt(Math.pow((x_max - x_min),2) + Math.pow((y_max - y_min), 2));
        for (int h = 0; h < numHyperplanes; h++) {
            hyperplane = chromosome.substring(h*2*b,(h+1)*2*b);
            angle = Integer.parseInt(hyperplane.substring(0,b), 2);
            angle *= (2 * Math.PI) / Math.pow(2, b);

            d = Integer.parseInt(hyperplane.substring(b,2*b), 2);
            d *= diag / Math.pow(2, b) + d_min;
            if((point[1]*Math.cos(angle) + point[0]*Math.sin(angle) - d) < 0)
                sign_string += "0";
            else
                sign_string += "1";
        }

        return sign_string;
    }

}
