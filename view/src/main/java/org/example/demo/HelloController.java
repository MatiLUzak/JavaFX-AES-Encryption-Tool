package org.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.example.Crypto;
import org.example.Key;
import org.example.TextConverter;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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


    public void encrytpfile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        TextConverter textConverter = new TextConverter();
        Crypto crypto = new Crypto(this.secretKey);
        if(file!=null){
            try{
                byte[] fileData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                List<byte[]> blocks= textConverter.divineIntoBlocksFile(fileData);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                for(byte[]block:blocks){
                    byte[][]datablock=textConverter.transformToByteMatrix(block);
                    crypto.encrypt(datablock);
                    for(byte[]row:datablock){
                        outputStream.write(row);
                    }
                }
                byte[] encryptedData = outputStream.toByteArray();
                FileChooser saveFileChooser = new FileChooser();
                File saveFile = saveFileChooser.showSaveDialog(null);
                if (saveFile != null) {
                    Files.write(Paths.get(saveFile.getAbsolutePath()), encryptedData, StandardOpenOption.CREATE);
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void decryptfile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz zaszyfrowany plik do odszyfrowania");
        File file = fileChooser.showOpenDialog(null);
        TextConverter textConverter = new TextConverter();
        Crypto crypto = new Crypto(this.secretKey);
        if (file != null) {
            try {
                byte[] encryptedData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                List<byte[]> blocks = textConverter.divineIntoBlocksFile(encryptedData);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                for (byte[] block : blocks) {
                    byte[][] dataBlock = textConverter.transformToByteMatrix(block);
                    crypto.decrypt(dataBlock);
                    for (byte[] row : dataBlock) {
                        outputStream.write(row);
                    }
                }

                byte[] decryptedData = outputStream.toByteArray();
                FileChooser saveFileChooser = new FileChooser();
                saveFileChooser.setTitle("Zapisz odszyfrowany plik");
                File saveFile = saveFileChooser.showSaveDialog(null);
                if (saveFile != null) {
                    Files.write(Paths.get(saveFile.getAbsolutePath()), decryptedData, StandardOpenOption.CREATE);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}