package org.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Crypto;
import org.example.Key;
import org.example.TextConverter;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class HelloController {
    public RadioButton keySize128;
    public RadioButton keySize256;
    public RadioButton keySize192;
    public TextField keyDisplayField;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextArea outputTextArea;

    private TextConverter converter=new TextConverter();
    private SecretKey secretKey;
    @FXML
    private void convertToBinary() {

        String text = inputTextArea.getText();
        String bintext=converter.convertToBinary(text);
        outputTextArea.setText(bintext);



    }

    public void onGenerateKey(ActionEvent actionEvent) {
        int keySize;
        if (keySize128.isSelected()) {
            keySize = 128;
        } else if (keySize192.isSelected()) {
            keySize = 192;
        } else if (keySize256.isSelected()) {
            keySize = 256;
        } else {
            keyDisplayField.setText("Select a key size");
            return;
        }
        try {
            this.secretKey = Key.generateAESKey(keySize); // Wywołanie metody generującej klucz
            String keyString = Key.keyToString(this.secretKey); // Konwersja klucza na ciąg znaków
            keyDisplayField.setText(keyString); // Wyświetlenie zakodowanego klucza
        } catch (NoSuchAlgorithmException e) {
            keyDisplayField.setText("Error generating key: " + e.getMessage());
        }
    }

    public void encrypt(ActionEvent actionEvent) {
        String text = inputTextArea.getText();
        TextConverter textConverter = new TextConverter();
        String[] blocks = textConverter.divideIntoBlocks(text);
        Crypto crypto = new Crypto(this.secretKey);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String block : blocks) {
            byte[][] dataBlock = textConverter.transformToByteMatrix(block.getBytes());
            crypto.encrypt(dataBlock);
            for (byte[] row : dataBlock) {
                outputStream.write(row, 0, row.length);
            }
        }
        String encodedString = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        outputTextArea.setText(encodedString);
    }



    public void decrypt(ActionEvent actionEvent) {
        String encodedText = outputTextArea.getText();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
        TextConverter textConverter = new TextConverter();
        Crypto crypto = new Crypto(this.secretKey);
        StringBuilder decryptedTextBuilder = new StringBuilder();

        // Zakładam, że każdy blok po dekodowaniu ma 16 bajtów
        for (int start = 0; start < decodedBytes.length; start += 16) {
            byte[] block = Arrays.copyOfRange(decodedBytes, start, Math.min(start + 16, decodedBytes.length));
            byte[][] dataBlock = textConverter.transformToByteMatrix(block);
            crypto.decrypt(dataBlock);
            for (byte[] row : dataBlock) {
                decryptedTextBuilder.append(new String(row));
            }
        }
        inputTextArea.setText(decryptedTextBuilder.toString());
    }




}