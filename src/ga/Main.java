package ga;


import org.ejml.simple.SimpleMatrix;

import java.math.BigInteger;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public class Main {
    public static void main(String[] args) {
        // AG pe codificare pe alfabet binare
        GeneticAlgorithm ga = new RSA(32, 200, 10000, .8, .1, true, 16);
        //GeneticAlgorithm ga = new EX6(2, 50, 150, .80, .002, true, 96);

        // AG codificare pe numere reale
        //GeneticAlgorithm ga = new RSA(2, 250, 10000, .65, .5, true, 0, 10000);

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

    public static class EX6 extends GeneticAlgorithm {
        public EX6(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
            super(m, n, it, uc, um, elitism, geneSize);
        }

        @Override
        protected SimpleMatrix convertChromosome(SimpleMatrix chromosome) {
            return chromosome;
        }

        public double fitness(SimpleMatrix chromosome) {
            String binaryChromosome = getBinaryCromosom(chromosome);
            String target = "010010000110010101101100011011000110111100100000011101110110111101110010011011000110010000100001";
            double value = 0.0;

            for (int i = 0; i < target.length(); i++) {
                if (target.charAt(i) == binaryChromosome.charAt(i))
                    value++;
            }

            return value;
        }
    }

    public static class RSA extends GeneticAlgorithm {
        public RSA(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
            super(m, n, it, uc, um, elitism, geneSize);
        }

        public RSA(int m, int n, int it, double uc, double um, boolean elitism, int low, int high) {
            super(m, n, it, uc, um, elitism, low, high);
        }

        @Override
        protected SimpleMatrix convertChromosome(SimpleMatrix chromosome) {
            return chromosome;
        }


        public double fitness(SimpleMatrix chromosome) {

            String binaryChromosome = getBinaryCromosom(chromosome);
            BigInteger p = new BigInteger(binaryChromosome.substring(0, geneSize), 2);
            BigInteger q = new BigInteger(binaryChromosome.substring(geneSize, 2 * geneSize), 2);
            BigInteger n = BigInteger.valueOf(2244959);

            BigInteger prod;
            if (p.isProbablePrime(8) && q.isProbablePrime(8)) {
                prod = p.multiply(q);
                System.out.println(p + " " + q + " " + prod);
                if (prod.equals(n))
                    System.out.println("gata!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return prod.multiply(BigInteger.valueOf(-1)).add(n).abs().doubleValue();
            } else {
                return 100000000;
            }

        }
    }
}
