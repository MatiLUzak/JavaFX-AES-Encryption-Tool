package org.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.example.RSA;
import org.example.TextConverter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class RSAController {
    public Button generateKeyButton;
    public TextField publicKeyField;
    public TextArea inputTextArea;
    public TextField privateKeyField;
    public TextArea outputTextArea;
    public RadioButton keySize1024;
    public RadioButton keySize2048;
    public RadioButton keySize3072;
    private RSA rsa;
    private TextConverter textConverter;
    private List<BigInteger> encryptedBlocks; // Pole do przechowywania zaszyfrowanych bloków

    // Konstruktor
    public RSAController() {
        this.encryptedBlocks = new ArrayList<>(); // Inicjalizacja listy zaszyfrowanych bloków
    }

    public void onGenerateKey(ActionEvent actionEvent) {
        int keySize;
        if (keySize1024.isSelected()) {
            keySize = 1024;
        } else if (keySize2048.isSelected()) {
            keySize = 2048;
        } else if (keySize3072.isSelected()) {
            keySize = 3072;
        } else {
            publicKeyField.setText("Select a key size");
            return;
        }
        this.rsa = new RSA(keySize);
        int blockSize = (keySize / 8) - 11;
        this.textConverter = new TextConverter(blockSize);
        publicKeyField.setText(rsa.getPublicKeyString());
        privateKeyField.setText(rsa.getPrivateKeyString());
    }

    @FXML
    public void switchToAES(ActionEvent actionEvent) {
        try {
            HelloApplication.loadAES();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encrypt(ActionEvent actionEvent) {
        String text = inputTextArea.getText();
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        List<byte[]> blocks = textConverter.divideIntoRSABlocks(textBytes);
        encryptedBlocks.clear(); // Wyczyszczenie listy zaszyfrowanych bloków przed nowym szyfrowaniem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            for (byte[] block : blocks) {
                BigInteger message = new BigInteger(1, block);
                BigInteger encrypted = rsa.encrypt(message);
                encryptedBlocks.add(encrypted); // Zapisanie zaszyfrowanego bloku do listy
                byte[] encryptedBytes = encrypted.toByteArray();
                outputStream.write(encryptedBytes);
            }
            String encodedString = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            outputTextArea.setText(encodedString);
        } catch (Exception e) {
            outputTextArea.setText("Encryption failed: " + e.getMessage());
        }
    }

    public void decrypt(ActionEvent actionEvent) {
        try {
            StringBuilder decryptedTextBuilder = new StringBuilder();
            for (BigInteger encrypted : encryptedBlocks) {
                BigInteger decrypted = rsa.decrypt(encrypted);
                byte[] decryptedBytes = decrypted.toByteArray();
                decryptedTextBuilder.append(new String(decryptedBytes, StandardCharsets.UTF_8));
            }
            inputTextArea.setText(decryptedTextBuilder.toString());
        } catch (Exception e) {
            outputTextArea.setText("Decryption failed: " + e.getMessage());
        }
    }

    public void encryptFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] fileData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                List<byte[]> blocks = textConverter.divideIntoRSABlocks(fileData);
                encryptedBlocks.clear(); // Wyczyszczenie listy zaszyfrowanych bloków przed nowym szyfrowaniem
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                for (byte[] block : blocks) {
                    BigInteger message = new BigInteger(1, block);
                    BigInteger encrypted = rsa.encrypt(message);
                    encryptedBlocks.add(encrypted); // Zapisanie zaszyfrowanego bloku do listy

                    byte[] encryptedBytes = encrypted.toByteArray();
                    int encryptedBlockSize = rsa.getN().bitLength() / 8;
                    if (encryptedBytes.length < encryptedBlockSize) {
                        byte[] paddedEncryptedBytes = new byte[encryptedBlockSize];
                        System.arraycopy(encryptedBytes, 0, paddedEncryptedBytes, encryptedBlockSize - encryptedBytes.length, encryptedBytes.length);
                        encryptedBytes = paddedEncryptedBytes;
                    }

                    outputStream.write(encryptedBytes);
                }

                byte[] encryptedData = outputStream.toByteArray();
                FileChooser saveFileChooser = new FileChooser();
                File saveFile = saveFileChooser.showSaveDialog(null);
                if (saveFile != null) {
                    Files.write(Paths.get(saveFile.getAbsolutePath()), encryptedData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void decryptFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz zaszyfrowany plik do odszyfrowania");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] encodedData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                int blockSize = rsa.getN().bitLength() / 8;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                for (int start = 0; start < encodedData.length; start += blockSize) {
                    int end = Math.min(start + blockSize, encodedData.length);
                    byte[] block = Arrays.copyOfRange(encodedData, start, end);

                    BigInteger encrypted = new BigInteger(1, block);
                    BigInteger decrypted = rsa.decrypt(encrypted);
                    byte[] decryptedBytes = decrypted.toByteArray();
                    outputStream.write(decryptedBytes);
                }

                byte[] decryptedData = outputStream.toByteArray();
                FileChooser saveFileChooser = new FileChooser();
                File saveFile = saveFileChooser.showSaveDialog(null);
                if (saveFile != null) {
                    Files.write(Paths.get(saveFile.getAbsolutePath()), decryptedData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}