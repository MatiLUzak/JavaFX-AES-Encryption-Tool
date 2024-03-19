package org.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Key;
import org.example.TextConverter;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class HelloController {
    public RadioButton keySize128;
    public RadioButton keySize256;
    public RadioButton keySize192;
    public Button generateKeyButton;
    public TextField keyDisplayField;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextArea outputTextArea;

    private TextConverter converter=new TextConverter();
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
            SecretKey secretKey = Key.generateAESKey(keySize); // Wywołanie metody generującej klucz
            String keyString = Key.keyToString(secretKey); // Konwersja klucza na ciąg znaków
            keyDisplayField.setText(keyString); // Wyświetlenie zakodowanego klucza
        } catch (NoSuchAlgorithmException e) {
            keyDisplayField.setText("Error generating key: " + e.getMessage());
        }
    }
}