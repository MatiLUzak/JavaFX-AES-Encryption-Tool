package org.example;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;


public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Hello world!");
        byte[][] dataBlock = new byte[4][4];
        int counter = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                dataBlock[i][j] = (byte) counter++;
            }
        }
        byte[] fixedFinalKey = new byte[176];
        for (int i = 0; i < fixedFinalKey.length; i++) {
            fixedFinalKey[i] = (byte) i;
        }
        Crypto crypto = new Crypto(Key.generateAESKey(256));
        System.out.println("Before encryption:");
        printDataBlock(dataBlock);
        crypto.encrypt(dataBlock);
        System.out.println("After encryption:");
        printDataBlock(dataBlock);
        crypto.decrypt(dataBlock);
        System.out.println("After decryption:");
        printDataBlock(dataBlock);
        //key1.printFinalKey(finalKeyArray);

    }

    private static void printDataBlock(byte[][] dataBlock) {
        for (byte[] row : dataBlock) {
            for (byte cell : row) {
                System.out.print(String.format("%02X ", cell));
            }
            System.out.println();
        }
    }

}