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
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                List<byte[]> blocks = textConverter.divideIntoRSABlocks(fileBytes);
                encryptedBlocks.clear();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                for (byte[] block : blocks) {
                    BigInteger message = new BigInteger(1, block);
                    BigInteger encrypted = rsa.encrypt(message);
                    encryptedBlocks.add(encrypted);
                    byte[] encryptedBytes = encrypted.toByteArray();
                    outputStream.write(Base64.getEncoder().encode(encryptedBytes));
                    outputStream.write('|');
                }

                byte[] encryptedFileBytes = outputStream.toByteArray();
                FileChooser saveFileChooser = new FileChooser();
                saveFileChooser.setTitle("Save Encrypted File");
                File savedFile = saveFileChooser.showSaveDialog(null);
                if (savedFile != null) {
                    Files.write(savedFile.toPath(), encryptedFileBytes, StandardOpenOption.CREATE);
                    outputTextArea.setText("File encrypted and saved successfully.");
                }
            } catch (IOException e) {
                outputTextArea.setText("File encryption failed: " + e.getMessage());
            }
        }
    }

    public void decryptFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] encryptedFileBytes = Files.readAllBytes(file.toPath());
                String encryptedFileContent = new String(encryptedFileBytes, StandardCharsets.UTF_8);
                String[] encryptedBlockStrings = encryptedFileContent.split("\\|");
                encryptedBlocks.clear();

                for (String blockString : encryptedBlockStrings) {
                    if (!blockString.isEmpty()) {
                        byte[] block = Base64.getDecoder().decode(blockString);
                        encryptedBlocks.add(new BigInteger(1, block));
                    }
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                for (BigInteger encrypted : encryptedBlocks) {
                    BigInteger decrypted = rsa.decrypt(encrypted);
                    byte[] decryptedBytes = removeLeadingZeros(decrypted.toByteArray());
                    outputStream.write(decryptedBytes);
                }

                byte[] decryptedFileBytes = outputStream.toByteArray();

                FileChooser saveFileChooser = new FileChooser();
                saveFileChooser.setTitle("Save Decrypted File");
                File savedFile = saveFileChooser.showSaveDialog(null);
                if (savedFile != null) {
                    Files.write(savedFile.toPath(), decryptedFileBytes, StandardOpenOption.CREATE);
                    outputTextArea.setText("File decrypted and saved successfully.");
                }
            } catch (IOException e) {
                outputTextArea.setText("File decryption failed: " + e.getMessage());
            }
        }
    }

    private byte[] removeLeadingZeros(byte[] data) {
        int index = 0;
        while (index < data.length && data[index] == 0) {
            index++;
        }
        return Arrays.copyOfRange(data, index, data.length);
    }





}