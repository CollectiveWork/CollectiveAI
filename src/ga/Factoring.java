package ga;


import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by Cristi on 4/12/14.
 */

public class Factoring {


        static BigInteger n = new BigInteger("19422373558956680261016835140062514512405762908185415254453918880896532310241324906729882061874229062947981709628790284106757750890836265297253975752484671475582332408808391201015695441614521907285342213187715153526705322978732461710414625939853309792748018681960574742035183641905471293077811643385518231561028270690463078509625585487806139801071681737807732087364739314229753431",10);
    public static void main(String[] args) {
        System.out.println(n.bitLength());
        BigInteger x,p,q;
        for (BigInteger i = sqrt(n); i.compareTo(n)!=1; i = new BigInteger(""+i.add(BigInteger.ONE),10)) {
            x = sqrt(i.pow(2).subtract(n));
            p = i.add(x);
            q = i.subtract(x);
            if(p.multiply(q).equals(n)){

                System.out.println("Gasit p:" + p + " q:" + q);
                System.out.println("P * Q : \n"+ "   "+p.multiply(q));
                System.out.println("N: " + n);
                System.exit(0);
            }
            else{

                System.out.println("Searching...");
            }
        }
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

}

