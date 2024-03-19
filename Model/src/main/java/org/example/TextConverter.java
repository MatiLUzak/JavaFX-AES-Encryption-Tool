package org.example;

public class TextConverter {

    public  String convertToBinary(String text) {
        byte[] bytes = text.getBytes();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int a = 0; a < 8; a++) {
                stringBuilder.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}
