package ga;


import org.ejml.simple.SimpleMatrix;

import java.lang.instrument.Instrumentation;
import java.math.BigInteger;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public class Main {
    static RSA rsa = new RSA();
    static String message = "as";
    static BigInteger ciphertext;
    static BigInteger plaintext;
    static BigInteger dick;

    public static void main(String[] args) {

        rsa.Initialize(32);
        dick = rsa.d;
        plaintext = new BigInteger(message.getBytes());
        ciphertext = rsa.encrypt(plaintext);

        // AG pe codificare pe alfabet binare



        GeneticAlgorithm ga = new RSAAG(64, 200, 100000, .8, .05, true, 64);
        //GeneticAlgorithm ga = new EX1(8, 50, 150, .80, .002, true, 8);

        // AG codificare pe numere reale
        //GeneticAlgorithm ga = new ga.RSA(2, 250, 10000, .65, .5, true, 0, 10000);



        SimpleMatrix tmp;
        SimpleMatrix fittest;
        try {
            tmp = ga.start(true, "singlePointCrossover");
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

    public static class RSAAG extends GeneticAlgorithm {
        public RSAAG(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
            super(m, n, it, uc, um, elitism, geneSize);
        }

        public RSAAG(int m, int n, int it, double uc, double um, boolean elitism, int low, int high) {
            super(m, n, it, uc, um, elitism, low, high);
        }

        @Override
        protected SimpleMatrix convertChromosome(SimpleMatrix chromosome) {
            return chromosome;
        }

        public double fitness(SimpleMatrix chromosome){
            String binaryChromosome = getBinaryCromosom(chromosome);
            rsa.d = new BigInteger(binaryChromosome, 2);
            BigInteger decrypted = rsa.decrypt(ciphertext);


            System.out.print("initial d: " + dick + "\t Current d: " + rsa.d + "\t");

//            if(plaintext.equals(decrypted))
//                System.out.println("REVOLUTIE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");


            System.out.print("\t Initial biginteger: " + plaintext + "\t Current biginteger: " + decrypted);

            return distance2(decrypted);
        }

        public int distance1(BigInteger decrypted){
            int dist = Math.abs(plaintext.subtract(decrypted).intValue());
            System.out.println("\tDistance: " + dist);
            return dist;
        }
        public int distance2(BigInteger decrypted){
            int dist = 0;
            String a = plaintext.toString();
            String b = decrypted.toString();

            String cur = "";
            int length = a.length() < b.length() ? a.length() : b.length();
            for (int i = 0; i < length; i++) {
                if(a.charAt(i) == b.charAt(i))
                {dist++;
                 cur+=a.charAt(i);
                }
            }
           if(cur!="") {
               BigInteger curent = BigInteger.valueOf(Integer.parseInt(cur));
            if(plaintext.equals(String.valueOf(new String(curent.toByteArray()))))
                System.out.println("REVOLUTIEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + String.valueOf(new String(curent.toByteArray())));

            System.out.println("\t Dist: " + dist + " " + cur +  " "+String.valueOf(new String(curent.toByteArray())));
           }else System.out.println("\t Dist: " + dist);
            return dist;
        }


// P SI Q STYLE - NOT WORKING
//        public double fitness(SimpleMatrix chromosome) {
//            String binaryChromosome = getBinaryCromosom(chromosome);
//            BigInteger p = new BigInteger(binaryChromosome.substring(0, geneSize), 2);
//            BigInteger q = new BigInteger(binaryChromosome.substring(geneSize, 2 * geneSize), 2);
//            BigInteger n = BigInteger.valueOf(2244959);
//
//            BigInteger prod;
//
//            if(p.isProbablePrime(8) && q.isProbablePrime(8)){
//                prod = p.multiply(q);
//             //   System.out.println(p + " " + q + " " + prod);
//                if(prod.equals(n))
//                    System.out.println("gata!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                return prod.multiply(BigInteger.valueOf(-1)).add(n).abs().doubleValue();
//            }else{
//                return 1000000;
//            }
//        }
    }
}
