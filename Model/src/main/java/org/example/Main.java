package org.example;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Hello world!");
        TextConverter textConverter = new TextConverter();

        // Przykładowy tekst do zaszyfrowania
        String textToEncrypt = "This a";
        // Dzielenie tekstu na 16-bajtowe bloki
        String[] blocks = textConverter.divideIntoBlocks(textToEncrypt);

        // Generowanie klucza AES
        SecretKey secretKey = Key.generateAESKey(256);
        Crypto crypto = new Crypto(secretKey);

        for (String block : blocks) {
            // Przekształcanie bloku tekstowego na macierz bajtów
            byte[][] dataBlock = textConverter.transformToByteMatrix(block.getBytes());

            System.out.println("Before encryption:");
            printDataBlock(dataBlock);

            // Szyfrowanie bloku
            crypto.encrypt(dataBlock);

            System.out.println("After encryption:");
            printDataBlock(dataBlock);

            // Deszyfrowanie bloku
            crypto.decrypt(dataBlock);

            System.out.println("After decryption:");
            printDataBlock(dataBlock);
        }
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
