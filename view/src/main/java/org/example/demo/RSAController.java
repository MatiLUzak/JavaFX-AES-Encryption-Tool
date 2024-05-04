package org.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class RSAController {
    public void onGenerateKey(ActionEvent actionEvent) {
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
    }

    public void decrypt(ActionEvent actionEvent) {
    }

    public void encryptFile(ActionEvent actionEvent) {
    }

    public void decryptFile(ActionEvent actionEvent) {

    }
}
