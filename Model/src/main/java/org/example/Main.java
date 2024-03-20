package org.example;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;


public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Key key1 = new Key();
        byte[] finalKeyArray = new byte[128];
        try {
            finalKeyArray=key1.keyExpansion(Key.generateAESKey(128));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        key1.printFinalKey(finalKeyArray);

    }
}