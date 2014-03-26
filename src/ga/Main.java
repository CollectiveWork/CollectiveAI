package ga;


import org.ejml.simple.SimpleMatrix;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public class Main {
    public static class EX1 extends GeneticAlgorithm{
        public EX1(int m, int n, int it, double uc, double um, int geneSize){
            super(m,n,it,uc,um, geneSize);
        }

        public EX1(int m, int n, int it, double uc, double um, int low, int high){
            super(m,n,it,uc,um, low, high);
        }

        public double fitness(SimpleMatrix cromosom){
            return Math.sin((Math.PI * cromosom.get(0)) / 256);
        }
    }

    public static class EX2 extends GeneticAlgorithm{
        public EX2(int m, int n, int it, double uc, double um, int geneSize){
            super(m,n,it,uc,um, geneSize);
        }

        // ecuatia sferei
        // sum from i=1 to m of x(i)^2
        // x(i) is coded with 8 bits
        public double fitness(SimpleMatrix cromosom){
            int value = 0;
            int n = cromosom.numCols();
            for (int i = 0; i < n; i++) {
                value += Math.pow(cromosom.get(i), 2);
            }

            return value;
        }
    }

    public static class EX3 extends GeneticAlgorithm{
        public EX3(int m, int n, int it, double uc, double um, int geneSize){
            super(m,n,it,uc,um, geneSize);
        }

        public EX3(int m, int n, int it, double uc, double um, double low, double high){
            super(m,n,it,uc,um, low, high);
        }

        // ecuatia Rosenbrock
        // x(i) is coded with 8 bits
        public double fitness(SimpleMatrix cromosom){
            int value = 0;
            int n = cromosom.numRows();
            double x1,x2;
            for (int i = 0; i < n - 1; i++) {
                x1 = cromosom.get(i);
                x2 = cromosom.get(i + 1);
                value += (100*Math.pow(x2 - Math.pow(x1,2),2) + Math.pow(x1 - 1,2));
            }

            return value;
        }
    }

    public static class EX4 extends GeneticAlgorithm{
        public EX4(int m, int n, int it, double uc, double um, int geneSize){
            super(m,n,it,uc,um, geneSize);
        }

        // ecuatia Rastrigin
        // x(i) is coded with 8 bits
        public double fitness(SimpleMatrix cromosom){
            int value = 0;
            int n = cromosom.numCols();
            double x1;
            for (int i = 0; i < n; i++) {
                x1 = cromosom.get(i);
                value += (x1*x1 - 10 * Math.cos(Math.PI*x1) + 10);
            }

            return value;
        }
    }

    public static class EX5 extends GeneticAlgorithm{
        public EX5(int m, int n, int it, double uc, double um, int geneSize){
            super(m,n,it,uc,um, geneSize);
        }

        public EX5(int m, int n, int it, double uc, double um, double low, double high){
            super(m,n,it,uc,um, low, high);
        }

        // ecuatia Griewank
        // x(i) is coded with 8 bits
        public double fitness(SimpleMatrix cromosom){
            int sum = 0, product = 1;
            int n = cromosom.numCols();
            double x1;
            for (int i = 0; i < n; i++) {
                x1 = cromosom.get(i);
                sum += x1 * x1;
                product *= Math.cos(x1 / Math.sqrt(i+1));
            }

            return 1 / 4000 * sum - product + 1;
        }
    }

    public static void main(String[] args) {
        // AG pe codificare pe alfabet binare
        GeneticAlgorithm ga = new EX1(8, 100, 1000, .75, .125, 8);

        // AG codificare pe numere reale
        //GeneticAlgorithm ga = new EX1(1, 100, 1000, .75, .125, 0, 255);

        SimpleMatrix tmp;
        SimpleMatrix fittest;
        try {
            tmp = ga.start("singlePointCrossover");
            fittest = ga.getFittest("max");

            System.out.println("Cromosom:" + fittest);
            System.out.println("Fitness:" + ga.getFitness(fittest));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
