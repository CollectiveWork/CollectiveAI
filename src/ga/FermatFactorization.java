package ga;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Cristi on 7/29/15.
 */
public class FermatFactorization {
    /** Fermat factor **/
    public void FermatFactor(BigInteger N)
    {
        long i=0;
        BigInteger a = sqrt(N);
        BigInteger b2 = (a.multiply(a)).subtract(N);
        while (!isSquare(b2))
        {   i++;
            if(i%100000==0) System.out.println("Interation: "+i);

            a = new BigInteger(""+a.add(BigInteger.ONE),10);

            b2 = (a.multiply(a)).subtract(N);

           //   System.out.println("a: " + a + " b: " + b2);


        }
        BigInteger r1 = a.subtract(sqrt(b2));
        BigInteger r2 = N.divide(r1);
        display(r1, r2);
    }

    public void display(BigInteger r1, BigInteger r2)
    {
        System.out.println("\nRoots = "+ r1 +" , "+ r2);
    }

    public boolean isSquare(BigInteger N)
    {
        BigInteger sqr =  sqrt(N);
        if ((sqr.multiply(sqr)).equals(N) || ((sqr.add(BigInteger.ONE)).multiply(sqr.add(BigInteger.ONE))).equals(N))
            return true;
        return false;
    }

    static BigInteger sqrt(BigInteger n) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
        while(b.compareTo(a) >= 0) {
            BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
            if(mid.multiply(mid).compareTo(n) > 0) b = mid.subtract(BigInteger.ONE);
            else a = mid.add(BigInteger.ONE);
        }
        return a.subtract(BigInteger.ONE);
    }


    public static void main(String[] args)
    {

        int bitLength = 64;
        BigInteger p = new BigInteger(bitLength,128,new Random());
        BigInteger q = new BigInteger(bitLength,128,new Random());
        System.out.println("p: "+ p + " q: "+q);
        BigInteger N = p.multiply(q);
        //N = new BigInteger("16");

        System.out.println("Integer: "+ N);

        System.out.println("square: "+ sqrt(N));

        FermatFactorization ff = new FermatFactorization();
        ff.FermatFactor(N);



    }
}
