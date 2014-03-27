package ga;


import org.ejml.simple.SimpleMatrix;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public class Main {
    public static void main(String[] args) {
        // AG pe codificare pe alfabet binare
        GeneticAlgorithm ga = new EX3(16, 10, 1000, .80, .065, true, 8);

        // AG codificare pe numere reale
       // GeneticAlgorithm ga = new EX3(2, 10, 1000, .80, .5, true, 0, 255);

        SimpleMatrix tmp;
        SimpleMatrix fittest;
        try {
            tmp = ga.start(false, "singlePointCrossover");
            fittest = ga.getFittest();

            System.out.println("Cromosom: " + fittest);
            System.out.println("Fitness: " + ga.getFitness(fittest));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class EX1 extends GeneticAlgorithm {
        public EX1(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
            super(m, n, it, uc, um, elitism, geneSize);
        }

        public EX1(int m, int n, int it, double uc, double um, boolean elitism, int low, int high) {
            super(m, n, it, uc, um, elitism, low, high);
        }

        public double fitness(SimpleMatrix chromosome) {
            return Math.sin((Math.PI * chromosome.get(0)) / 256);
        }
    }

    public static class EX2 extends GeneticAlgorithm {
        public EX2(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
            super(m, n, it, uc, um, elitism, geneSize);
        }

        public EX2(int m, int n, int it, double uc, double um, boolean elitism, int low, int high) {
            super(m, n, it, uc, um, elitism, low, high);
        }
        // ecuatia sferei
        // sum from i=1 to m of x(i)^2
        // x(i) is coded with 8 bits
        public double fitness(SimpleMatrix chromosome) {
            double value = 0.0;
            int n = chromosome.numRows();
            for (int i = 0; i < n; i++) {
                value += Math.pow(chromosome.get(i), 2);
            }

            return value;
        }
    }

    public static class EX3 extends GeneticAlgorithm {
        public EX3(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
            super(m, n, it, uc, um, elitism, geneSize);
        }

        public EX3(int m, int n, int it, double uc, double um, boolean elitism, double low, double high) {
            super(m, n, it, uc, um, elitism, low, high);
        }

        // ecuatia Rosenbrock
        // x(i) is coded with 8 bits
        public double fitness(SimpleMatrix cromosom) {
            double value = 0.0;
            int n = cromosom.numRows();
            double x1, x2;
            for (int i = 0; i < n - 1; i++) {
                x1 = cromosom.get(i);
                x2 = cromosom.get(i + 1);
                value += (100 * Math.pow(x2 - Math.pow(x1, 2), 2) + Math.pow(x1 - 1, 2));
            }

            return value;
        }
    }

    public static class EX4 extends GeneticAlgorithm {
        public EX4(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
            super(m, n, it, uc, um, elitism, geneSize);
        }

        public EX4(int m, int n, int it, double uc, double um, boolean elitism, int low, int high) {
            super(m, n, it, uc, um, elitism, low, high);
        }

        // ecuatia Rastrigin
        public double fitness(SimpleMatrix chromosome) {
            double value = 0.0;
            int n = chromosome.numRows();
            double x1;
            for (int i = 0; i < n; i++) {
                x1 = chromosome.get(i);
                value += (x1 * x1 - 10 * Math.cos(2 * Math.PI * x1) + 10);
            }

            return value;
        }
    }

    public static class EX5 extends GeneticAlgorithm {
        public EX5(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
            super(m, n, it, uc, um, elitism, geneSize);
        }

        public EX5(int m, int n, int it, double uc, double um, boolean elitism, double low, double high) {
            super(m, n, it, uc, um, elitism, low, high);
        }

        public EX5(int m, int n, int it, double uc, double um, boolean elitism, int low, int high) {
            super(m, n, it, uc, um, elitism, low, high);
        }

        // ecuatia Griewank
        // x(i) is coded with 8 bits
        public double fitness(SimpleMatrix cromosom) {
            double sum = 0.0, product = 1.0;
            int n = cromosom.numRows();
            double x1;
            for (int i = 0; i < n; i++) {
                x1 = cromosom.get(i);
                sum += x1 * x1;
                product *= Math.cos(x1 / Math.sqrt(i + 1));
            }

            return 1 / 4000 * sum - product + 1;
        }
    }
}
