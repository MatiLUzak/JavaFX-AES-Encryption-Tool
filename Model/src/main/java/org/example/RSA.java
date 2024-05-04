package org.example;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
    private BigInteger n;  // n = p*q
    private BigInteger e;  // public key
    private BigInteger d;  // private key

    public RSA(int bits) {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bits / 2, random);
        BigInteger q = BigInteger.probablePrime(bits / 2, random);
        n = p.multiply(q);

        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bits / 2, random);
        // Ensure e is coprime to phi(n) and smaller than phi(n)
        while (phi.gcd(e).intValue() > 1) {
            e = BigInteger.probablePrime(bits / 2, random);
        }
        d = e.modInverse(phi);
    }

    public BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n);
    }

    public BigInteger decrypt(BigInteger message) {
        return message.modPow(d, n);
    }

    public static void main(String[] args) {
        RSA rsa = new RSA(1024);  // Use 1024-bit keys for example

        // Example message
        String text = "Hello, RSA!";
        byte[] bytes = text.getBytes();
        BigInteger message = new BigInteger(bytes);

        BigInteger encrypted = rsa.encrypt(message);
        BigInteger decrypted = rsa.decrypt(encrypted);

        String decryptedText = new String(decrypted.toByteArray());
        System.out.println("Original: " + text);
        System.out.println("Encrypted: " + encrypted.toString());
        System.out.println("Decrypted: " + decryptedText);
    }
}