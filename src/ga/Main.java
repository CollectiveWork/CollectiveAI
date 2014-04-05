package ga;


import org.ejml.simple.SimpleMatrix;

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
        int bitLength = 16;

        rsa.Initialize(bitLength);
        dick = rsa.d;
        plaintext = new BigInteger(message.getBytes());
        ciphertext = rsa.encrypt(plaintext);

        // AG pe codificare pe alfabet binare
        GeneticAlgorithm ga = new RSAAG(2 * bitLength, 2, 1000000000, .8, .5, true, 2 * bitLength);
        //GeneticAlgorithm ga = new EX1(8, 50, 150, .80, .002, true, 8);

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

        double last_distance = 1000000000d;
        public double fitness(SimpleMatrix chromosome) {
            String binaryChromosome = getBinaryCromosom(chromosome);
            rsa.d = new BigInteger(binaryChromosome, 2);
            BigInteger decrypted = rsa.decrypt(ciphertext);

            double distance = distance2(decrypted);
            if (distance < last_distance) {
                last_distance = distance;
                if (plaintext.equals(decrypted))
                    System.out.println("REVOLUTIE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                System.out.println("initial d: " + dick + "\t Current d: " + rsa.d + "\t Initial biginteger: " + plaintext + "\t Current biginteger: " + decrypted + "\t Dist: " + distance);
            }

            return distance;
        }

        public int distance1(BigInteger decrypted) {
            int dist = Math.abs(plaintext.subtract(decrypted).intValue());
            System.out.println("\tDistance: " + dist);
            return dist;
        }

        public double distance2(BigInteger decrypted) {
            int dist = 0;
            String a = plaintext.toString();
            String b = decrypted.toString();

            if (a.length() != b.length()) {
                return 1000000000;
            }

            for (int i = 0; i < a.length(); i++) {
                dist += Math.pow(Character.getNumericValue(a.charAt(i)) - Character.getNumericValue(b.charAt(i)),2);
            }

            return Math.sqrt(dist);
        }

        public int distance3(BigInteger decrypted) {
            int dist = 0;
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
