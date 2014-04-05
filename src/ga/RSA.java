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

        String plaintext;

        System.out.println("pubkey: " + rsa.publickey());
        // System.out.println("pubbkey "+rsa.publickey());
        // System.out.println("gpub: " + rsa.getpublickey(rsa.publickey())[0]);
        System.out.println("plaintext :");
        plaintext = sc.nextLine();

        BigInteger bplaintext;
        bplaintext = new BigInteger(plaintext.getBytes());

        BigInteger bciphertext = rsa.encrypt(bplaintext);

        System.out.println("plaintext : " + bplaintext.toString());
        System.out.println("ciphertext : " + bciphertext.toString());

        bplaintext = rsa.decrypt(bciphertext);

        System.out.println("decrypted : " + bplaintext.toString());
        System.out.println(String.valueOf(new String(bplaintext.toByteArray())));
    }

}
