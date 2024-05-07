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
        int blockSize=keySize/8-11;
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
        List<byte[]> blocks = textConverter.divideIntoRSABlocks(text.getBytes());
        List<String> encryptedBlockStrings = new ArrayList<>();

        try {
            for (byte[] block : blocks) {
                BigInteger message = new BigInteger(1, block);
                BigInteger encrypted = rsa.encrypt(message);
                String base64Encoded = Base64.getEncoder().encodeToString(encrypted.toByteArray());
                encryptedBlockStrings.add(base64Encoded);
            }
            // Łączymy listę zaszyfrowanych bloków do wyświetlenia, oddzielając je przecinkami
            String encodedString = String.join(", ", encryptedBlockStrings);
            outputTextArea.setText(encodedString);
        } catch (Exception e) {
            outputTextArea.setText("Encryption failed: " + e.getMessage());
        }
    }


    public void decrypt(ActionEvent actionEvent) {
        String encodedText = outputTextArea.getText();
        String[] encodedBlocks = encodedText.split(", ");  // Zakładamy, że bloki są oddzielone przecinkami
        StringBuilder decryptedTextBuilder = new StringBuilder();

        try {
            for (String encodedBlock : encodedBlocks) {
                byte[] decodedBytes = Base64.getDecoder().decode(encodedBlock);
                BigInteger encrypted = new BigInteger(1, decodedBytes);
                BigInteger decrypted = rsa.decrypt(encrypted);
                decryptedTextBuilder.append(new String(decrypted.toByteArray()));
                //decryptedTextBuilder.append("\n");  // Dodajemy nową linię dla każdego bloku
            }
            inputTextArea.setText(decryptedTextBuilder.toString());
        } catch (Exception e) {
            inputTextArea.setText("Decryption failed: " + e.getMessage());
        }
    }


    public void encryptFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] fileData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                List<byte[]> blocks = textConverter.divineIntoBlocksFile(fileData);
                List<String> encryptedBlockStrings = new ArrayList<>();

                for (byte[] block : blocks) {
                    BigInteger message = new BigInteger(1, block);
                    BigInteger encrypted = rsa.encrypt(message);
                    String base64Encoded = Base64.getEncoder().encodeToString(encrypted.toByteArray());
                    encryptedBlockStrings.add(base64Encoded);
                }
                String encodedString = String.join(", ", encryptedBlockStrings);
                FileChooser saveFileChooser = new FileChooser();
                File saveFile = saveFileChooser.showSaveDialog(null);
                if (saveFile != null) {
                    Files.writeString(Paths.get(saveFile.getAbsolutePath()), encodedString, StandardCharsets.UTF_8);
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
                String encodedString = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
                String[] encodedBlocks = encodedString.split(", ");
                StringBuilder decryptedTextBuilder = new StringBuilder();

                for (String encodedBlock : encodedBlocks) {
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedBlock);
                    BigInteger encrypted = new BigInteger(1, decodedBytes);
                    BigInteger decrypted = rsa.decrypt(encrypted);
                    decryptedTextBuilder.append(new String(decrypted.toByteArray(), StandardCharsets.UTF_8));
                }
                FileChooser saveFileChooser = new FileChooser();
                File saveFile = saveFileChooser.showSaveDialog(null);
                if (saveFile != null) {
                    Files.writeString(Paths.get(saveFile.getAbsolutePath()), decryptedTextBuilder.toString(), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
