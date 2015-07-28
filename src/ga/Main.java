package ga;


import org.ejml.simple.SimpleMatrix;

import java.math.BigInteger;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public class Main {
    static RSA rsa = new RSA();
    static String message[] = "an".split(" ");
    //static String message[] = "a b c d e f g h i j k l m n".split(" ");
    static BigInteger ciphertext[];
    static BigInteger plaintext[];
    static BigInteger dick;
    static BigInteger P, Q;
//    static BigInteger n = new BigInteger("19422373558956680261016835140062514512405762908185415254453918880896532310241324\n" +
//            "90672988206187422906294798170962879028410675775089083626529725397575248467147558\n" +
//            "23324088083912010156954416145219072853422131877151535267053229787324617104146259\n" +
//            "39853309792748018681960574742035183641905471293077811643385518231561028270690463\n" +
//            "078509625585487806139801071681737807732087364739314229753431",10);

    public static void main(String[] args) {
        final int bitLength = 18;
        final int chromosomeLength = bitLength;
        final int params = 2;
        int threads = 10;
        final int pop_size = 500;



        rsa.Initialize(bitLength);
        dick = rsa.d;
        plaintext = new BigInteger[message.length];
        ciphertext = new BigInteger[message.length];

        for (int i = 0; i < message.length; i++) {
            plaintext[i] = new BigInteger(message[i].getBytes());
            ciphertext[i] = rsa.encrypt(plaintext[i]);
        }
        P = rsa.p;
        Q = rsa.q;

        final SimpleMatrix finalBestPopulation = Population.init(params * chromosomeLength, pop_size);
        for (int i = 0; i < threads; i++) {

            final RSA rsa_tmp = new RSA();

            rsa_tmp.p = rsa.p;
            rsa_tmp.q = rsa.q;
            rsa_tmp.n = rsa.n;
            rsa_tmp.e = rsa.e;
            rsa_tmp.d = rsa.d;

            final double mutations[] = {.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9,.005, .05, .3, .5, .9,.005, .05, .2, .5, .9};
            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {


                    // AG pe codificare pe alfabet binare
                    GeneticAlgorithm ga = new RSAPQ(params * chromosomeLength, pop_size, 1000000000, .8, mutations[finalI], true, chromosomeLength, finalI, finalBestPopulation, rsa_tmp);
                    //GeneticAlgorithm ga = new EX1(8, 50, 150, .80, .002, true, 8);

                    SimpleMatrix tmp;
                    SimpleMatrix fittest;
                    try {
                        ga.start(false, "singlePointCrossover");
                        fittest = ga.getFittest();

                        System.out.println("Cromosom: " + fittest);
                        System.out.println("Fitness: " + ga.getFitness(fittest));

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
            BigInteger p = new BigInteger(binaryChromosome.substring(0, geneSize), 2);
            BigInteger q = new BigInteger(binaryChromosome.substring(geneSize, 2 * geneSize), 2);
            BigInteger n = rsa.n;
            double distance;
            BigInteger prod;


            prod = p.multiply(q);

            distance = prod.multiply(BigInteger.valueOf(-1)).add(n).abs().doubleValue();
            if(prod.equals(n)){
                System.out.println("gata!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(rsa.p + " " + rsa.q);
                System.out.println(p + " " + q);
                System.out.println("P: " + p + " Q:" + q + " Prod: " + prod + " Target:" + n);
                System.exit(0);
            }

            return distance;
        }
    }

    public static class EX1 extends GeneticAlgorithm {
        public EX1(int m, int n, int it, double uc, double um, boolean elitism, int geneSize, int id, SimpleMatrix bestPopulation, RSA rsa_tmp) {
            super(m, n, it, uc, um, elitism, geneSize, id, bestPopulation, rsa_tmp);
        }

        public EX1(int m, int n, int it, double uc, double um, boolean elitism, int low, int high, int id, SimpleMatrix bestPopulation, RSA rsa_tmp) {
            super(m, n, it, uc, um, elitism, low, high, id, bestPopulation, rsa_tmp);
        }

        public double fitness(SimpleMatrix chromosome) {
            return Math.sin((Math.PI * chromosome.get(0)) / 256);
        }
    }

    public static class RSAAG extends GeneticAlgorithm {
        public RSAAG(int m, int n, int it, double uc, double um, boolean elitism, int geneSize, int id, SimpleMatrix bestPopulation, RSA rsa_tmp) {
            super(m, n, it, uc, um, elitism, geneSize, id, bestPopulation, rsa_tmp);
        }


        @Override
        protected SimpleMatrix convertChromosome(SimpleMatrix chromosome) {
            return chromosome;
        }

        double last_distance = message.length * 100000000d;
        BigInteger decrypted[] = new BigInteger[message.length];

        synchronized public double fitness(SimpleMatrix chromosome) {
            String binaryChromosome = getBinaryCromosom(chromosome);
            rsa_tmp.d = new BigInteger(binaryChromosome, 2);
            double distance = 0d;

            for (int i = 0; i < message.length; i++) {
                decrypted[i] = rsa_tmp.decrypt(ciphertext[i]);
                distance += hammingDistance(plaintext[i], decrypted[i]);
            }

            if (distance < last_distance) {
                last_distance = distance;
                if (plaintext.equals(decrypted))
                    System.out.println("REVOLUTIE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                System.out.println("id:" + id + "\tinitial d: " + dick + "\t Current d: " + rsa_tmp.d + "\t Dist: " + distance + " p&q:" + rsa_tmp.p + " " + rsa_tmp.q + " e:" + rsa_tmp.e + " d:" + dick);
            }

            if (dick.equals(rsa_tmp.d)){
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("id:" + id + "\tinitial d: " + dick + "\t Current d: " + rsa_tmp.d + "\t Dist: " + distance + " p&q:" + rsa_tmp.p + " " + rsa_tmp.q + " e:" + rsa_tmp.e + " d:" + dick);
                System.exit(0);
            }

            return distance;
        }

        public double distance1(BigInteger plaintext, BigInteger decrypted) {
            return Math.abs(plaintext.subtract(decrypted).doubleValue());
        }

        public double euclidianDistance(BigInteger plaintext, BigInteger decrypted) {
            double dist = 0;
            String a = plaintext.toString();
            String b = decrypted.toString();

            if (a.length() != b.length()) {
                return message.length * 1000000000d;
            }

            for (int i = 0; i < a.length(); i++) {
                dist += Math.pow(Character.getNumericValue(a.charAt(i)) - Character.getNumericValue(b.charAt(i)), 2);
            }

            return Math.sqrt(dist);
        }

        public double leeDistance(BigInteger decrypted) {
            double dist = 0;
            String a = plaintext.toString();
            String b = decrypted.toString();

            if (a.length() != b.length()) {
                return 1000000000;
            }

            int tmpA, tmpB, q = 10;
            for (int i = 0; i < a.length(); i++) {
                tmpA = Math.abs(Character.getNumericValue(a.charAt(i)) - Character.getNumericValue(b.charAt(i)));
                tmpB = q - tmpA;
                dist += tmpA < tmpB ? tmpA : tmpB;
            }

            return dist;
        }

        public double canberraDistance(BigInteger decrypted) {
            double dist = 0;
            String a = plaintext.toString();
            String b = decrypted.toString();

            if (a.length() != b.length()) {
                return 0;
            }

            double c1, c2;
            for (int i = 0; i < a.length(); i++) {
                c1 = Character.getNumericValue(a.charAt(i));
                c2 = Character.getNumericValue(b.charAt(i));
                dist += Math.abs(c1 - c2) / (Math.abs(c1) + Math.abs(c2));
            }

            return dist;
        }

        // minimize
        public double hammingDistance(BigInteger plaintext,BigInteger decrypted) {
            double dist = 0;
            String a = plaintext.toString();
            String b = decrypted.toString();

            if (a.length() != b.length()) {
                return 100000000d;
            }

            for (int i = 0; i < a.length(); i++) {
                if (a.charAt(i) != b.charAt(i))
                    dist += 1;
            }

            return dist;
        }


        public double distance3(BigInteger decrypted) {
            double dist = 0;
            String a = plaintext.toString();
            String b = decrypted.toString();

            String cur = "";
            int length = a.length() < b.length() ? a.length() : b.length();
            for (int i = 0; i < length; i++) {
                if (a.charAt(i) == b.charAt(i)) {
                    dist++;
                    cur += a.charAt(i);
                }
            }
            if (cur != "") {
                BigInteger curent = BigInteger.valueOf(Integer.parseInt(cur));
                if (plaintext.equals(String.valueOf(new String(curent.toByteArray()))))
                    System.out.println("REVOLUTIEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + String.valueOf(new String(curent.toByteArray())));

                System.out.println("\t Dist: " + dist + " " + cur + " " + String.valueOf(new String(curent.toByteArray())));
            } else System.out.println("\t Dist: " + dist);
            return dist;
        }
    }
}
