package org.example;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

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

    public String getPublicKeyString() {
        return e.toString();
    }

    public String getPrivateKeyString() {
        return d.toString();
    }

    public BigInteger getN() {
        return n;  // zwróć wartość n
    }

    public static void main(String[] args) {
        RSA rsa = new RSA(1024);
        TextConverter converter = new TextConverter(117);  // RSA block size

        // Example long message
        String longText = "The quick brown fox jumps over the lazy dog repeated to make a long string. ";
        longText = longText.repeat(10);

        List<byte[]> blocks = converter.divideIntoRSABlocks(longText.getBytes());

        List<BigInteger> encryptedBlocks = new ArrayList<>();
        List<String> decryptedBlocks = new ArrayList<>();

        for (byte[] block : blocks) {
            BigInteger message = new BigInteger(1, block);
            BigInteger encrypted = rsa.encrypt(message);
            encryptedBlocks.add(encrypted);

            BigInteger decrypted = rsa.decrypt(encrypted);
            decryptedBlocks.add(new String(decrypted.toByteArray()));
        }

        // Output the results
        System.out.println("Original message: " + longText);
        System.out.println("Decrypted message: ");
        decryptedBlocks.forEach(System.out::print);
    }
}