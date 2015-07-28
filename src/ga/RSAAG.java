package ga;

import org.ejml.simple.SimpleMatrix;
import java.math.BigInteger;

/**
 * Created by butnaruandrei on 7/28/2015.
 */
public class RSAAG {

    // declare variables
    static RSA rsa = new RSA();

    public static void main(String[] args) {
        final int bitLength = 32; // RSA private key bit length

        // set Genetic Algorithm variables
        final int chromosomeLength = bitLength / 2; // chromosome size ( the bit length of the codification for p / q )
        final int params = 2; // number of params that we need to find using AG ( p and q )
        final int pop_size = 500; // how many individuals ( pairs of p and q ) do we want to have in AGs population

        // number of threads used to parallelize the search
        int threads = 8;

        // initializing the RSA values ( private and public keys, with bit length for p and q equal with chromosome length )
        rsa.Initialize(chromosomeLength);

        // we want to have a population that merges the best individuals from each thread
        final SimpleMatrix finalBestPopulation = Population.init(params * chromosomeLength, pop_size);

        // create a new thread
        for (int i = 0; i < threads; i++) {

            // remember the id of the thread
            final int finalI = i;

            // copy the RSA instance to pass to the new thread
            final RSA rsa_tmp = new RSA(rsa);

            // generate different mutations for each thread for a better search pool
            // it will get the values 0.003, 0.01 (almost), 0.03, 0.1 (almost), 0.3, 0.9
            final double mutation = 3 * Math.pow( 10, (i % 6) / 2.0 - 3 );

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    // initialize the GA algorithm
                    GeneticAlgorithm ga = new RSAPQ(params * chromosomeLength, pop_size, 25, .8, mutation, true, chromosomeLength, finalI, finalBestPopulation, rsa_tmp);

                    try {
                        // start the GA main loop
                        ga.start(false, "singlePointCrossover");

                        // get the chromosome String representation from SimpleMatrix vector
                        String binaryChromosome = ga.getBinaryCromosom(ga.getFittest());

                        // get p and q values from the chromosome representation
                        BigInteger p = new BigInteger(binaryChromosome.substring(0, chromosomeLength), 2);
                        BigInteger q = new BigInteger(binaryChromosome.substring(chromosomeLength, 2 * chromosomeLength), 2);

                        System.out.println("P and Q: (" + p + ", " + q + ")");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            t.start();
        }
    }

    public static class RSAPQ extends GeneticAlgorithm {
        public RSAPQ(int m, int n, int it, double uc, double um, boolean elitism, int geneSize, int id, SimpleMatrix bestPopulation, RSA rsa_tmp) {
            super(m, n, it, uc, um, elitism, geneSize, id, bestPopulation, rsa_tmp);
        }

        @Override
        protected SimpleMatrix convertChromosome(SimpleMatrix chromosome) {
            return chromosome;
        }

        synchronized public double fitness(SimpleMatrix chromosome) {
            // get the chromosome String representation from SimpleMatrix vector
            String binaryChromosome = getBinaryCromosom(chromosome);

            // get p and q values from the chromosome representation
            BigInteger p = new BigInteger(binaryChromosome.substring(0, geneSize), 2);
            BigInteger q = new BigInteger(binaryChromosome.substring(geneSize, 2 * geneSize), 2);

            // multiply p with q ( the get the possible private key )
            BigInteger prod = p.multiply(q);

            // calculate the distance | p*q - private_key |
            // the value should be equal to 0 for a perfect match
            double distance = prod.multiply(BigInteger.valueOf(-1)).add(rsa.n).abs().doubleValue();

            // <<<<<<<------------- DEMO (this must be removed in production)
            // TODO NEED TO REFACTOR THIS ( move this kind of logic to GA main loop )
            if(prod.equals(rsa.n)){
                System.out.println("gata!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(rsa.p + " " + rsa.q);
                System.out.println(p + " " + q);
                System.out.println("P: " + p + " Q:" + q + " Prod: " + prod + " Target:" + n);

                System.exit(0);
            }
            // ------------------>>>>>>>>>>

            return distance;
        }
    }
}
