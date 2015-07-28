package ga;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Cristi
 */
public class RSA {

    public BigInteger p, q;
    public BigInteger n;
    public BigInteger PhiN;

    public BigInteger e, d; // exponential and private key

    public RSA(){}
    public RSA (RSA rsa_tmp){
        InitializeParams(rsa_tmp.p, rsa_tmp.q, rsa_tmp.e, rsa_tmp.d);
    }

    public void Initialize(int SIZE) {
       

        /* Step 1: Select two large prime numbers. Say p and q. */
        p = new BigInteger(SIZE, 15, new Random());
        q = new BigInteger(SIZE, 15, new Random());

        /* Step 2: Calculate n = p.q */
        n = p.multiply(q);

        /* Step 3: Calculate ø(n) = (p - 1).(q - 1) */
        PhiN = p.subtract(BigInteger.valueOf(1));
        PhiN = PhiN.multiply(q.subtract(BigInteger.valueOf(1)));

        /* Step 4: Find e such that gcd(e, ø(n)) = 1 ; 1 < e < ø(n) */
        do {
            e = new BigInteger(2 * SIZE, new Random());

        } while ((e.compareTo(PhiN) != 1)
                || (e.gcd(PhiN).compareTo(BigInteger.valueOf(1)) != 0));
        /* Step 5: Calculate d such that e.d = 1 (mod ø(n)) */
        d = e.modInverse(PhiN);

    }
    public void InitializeParams(BigInteger p,BigInteger q,BigInteger e,BigInteger d) {
        this.p=p;
        this.q=q;


        /* Step 2: Calculate n = p.q */
        n = p.multiply(q);

        /* Step 3: Calculate ø(n) = (p - 1).(q - 1) */
        PhiN = p.subtract(BigInteger.valueOf(1));
        PhiN = PhiN.multiply(q.subtract(BigInteger.valueOf(1)));

        /* Step 4: Find e such that gcd(e, ø(n)) = 1 ; 1 < e < ø(n) */
       this.e=e;
        /* Step 5: Calculate d such that e.d = 1 (mod ø(n)) */
       this.d=d;

    }

    public String publickey() {
        return e + " " + n;
    }

    public BigInteger[] getpublickey(String pub) {
        BigInteger[] pubkey = new BigInteger[2];
        String[] _pub = pub.split(" ");
        pubkey[0] = new BigInteger(_pub[0]);
        pubkey[1] = new BigInteger(_pub[1]);

        return pubkey;
    }

    public BigInteger encrypt(BigInteger plaintext) {
        return plaintext.modPow(e, n);
    }

    public BigInteger decrypt(BigInteger ciphertext) {
        return ciphertext.modPow(d, n);
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        RSA rsa = new RSA();
        rsa.Initialize(16);

        rsa.p=BigInteger.valueOf(46573);
        rsa.q=BigInteger.valueOf(50707);
       // System.out.println("n = "+ rsa.n.toString() + " "+ rsa.p.multiply(rsa.q));
        rsa.e=new BigInteger("3543127543",10);
        rsa.d=BigInteger.valueOf(1033873687);


       String plaintext = "as";
       String plaintext2 = "ta";
        //System.out.println("pubkey: " + rsa.publickey());
        // System.out.println("pubbkey "+rsa.publickey());
        // System.out.println("gpub: " + rsa.getpublickey(rsa.publickey())[0]);
      //  System.out.println("plaintext :");
       // plaintext = sc.nextLine();

        BigInteger bplaintext;
        bplaintext = new BigInteger(plaintext.getBytes());

        BigInteger bplaintext2;
        bplaintext2 = new BigInteger(plaintext2.getBytes());

        BigInteger bciphertext = rsa.encrypt(bplaintext);
        BigInteger bciphertext2 = rsa.encrypt(bplaintext2);

        System.out.println("plaintext : " + bplaintext.toString());
        System.out.println("ciphertext : " + bciphertext.toString());

        System.out.println("plaintext2 : " + bplaintext2.toString());
        System.out.println("ciphertext2 : " + bciphertext2.toString());

        rsa.d=new BigInteger("2214613603",10);
        bplaintext = rsa.decrypt(bciphertext);
        bplaintext2 = rsa.decrypt(bciphertext2);

        System.out.println("decrypted : " + bplaintext.toString());
        System.out.println(String.valueOf(new String(bplaintext.toByteArray())));

        System.out.println("decrypted2 : " + bplaintext2.toString());
        System.out.println(String.valueOf(new String(bplaintext2.toByteArray())));

    }

}
