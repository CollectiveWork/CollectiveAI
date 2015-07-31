package ga;

import org.ejml.simple.SimpleMatrix;

import java.math.BigInteger;

/**
 * Created by butna on 7/30/2015.
 *
 * Using Fermat Factorization as fitness function
 */
public class FFAG {
    // declare variables
    static RSA rsa = new RSA();

    public static void main(String[] args) {
        final int bitLength = 16; // RSA private key bit length

        // set Genetic Algorithm variables
        final int chromosomeLength = bitLength; // chromosome size ( the bit length of the codification for a)
        final int params = 1; // number of params that we need to find using AG ( p and q )
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
                        ga.start(true, "singlePointCrossover");

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
            String binaryChromosome = getBinaryCromosom(chromosome);
            BigInteger a = new BigInteger(binaryChromosome, 2);
            BigInteger b, sqrtb,xp, xq;
            double distance;


            BigInteger a2 = a.multiply(a);


            if(a2.compareTo(rsa.n) == -1)
                return -Double.MAX_VALUE;

            BigInteger sqrt = Factoring.sqrt(a2.subtract(rsa.n));
            BigInteger sqrt2 = sqrt.multiply(sqrt);
            distance = rsa.n.subtract(a2.subtract(sqrt2)).doubleValue();

            // DEMO FOR TESTING PURPOSE!!! ------->>>>>>>>>>>>>>>>>>>>
            b = a2.subtract(rsa.n);
            sqrtb = Factoring.sqrt(b);

            xp = a.subtract(sqrtb);
            xq = a.add(sqrtb);

            if(xp.multiply(xq).compareTo(rsa.n) == 0){
                System.out.println("GATA!!!!!!!!!!!!!!!!");
                System.out.println("a = " + a + " distance = " + distance + " xp = " + xp + " xq = " + xq + "real p = " + rsa.p + "real q =" + rsa.q);
                System.exit(0);
            }

            // <<<<<<<<<<<<<<<-------------------

            return distance;
        }
    }
}
