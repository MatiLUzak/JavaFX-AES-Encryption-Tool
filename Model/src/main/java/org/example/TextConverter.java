package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextConverter {
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    private final int BLOCK_SIZE;

    public TextConverter() {
        this.BLOCK_SIZE=16;
    }
    public TextConverter(int blockSize){
        this.BLOCK_SIZE=blockSize;
    }

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
    public String[] divideIntoBlocks(String text) {
        byte[] textBytes = text.getBytes(); // Pobieranie bajtów tekstu
        int blockCount = (int) Math.ceil((double) textBytes.length / BLOCK_SIZE);
        String[] blocks = new String[blockCount];

        for (int i = 0; i < blockCount; i++) {
            int from = i * BLOCK_SIZE;
            int to = Math.min(from + BLOCK_SIZE, textBytes.length);
            byte[] block = java.util.Arrays.copyOfRange(textBytes, from, to);
            blocks[i] = new String(block);
        }

        return blocks;
    }


    public byte[][] transformToByteMatrix(byte[] byteText) {
        byte[][] bytes = new byte[4][4]; // Dla 128-bitowego bloku
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                bytes[i][j] = index < byteText.length ? byteText[index++] : 0;
            }
        }
        return bytes;
    }
    public List<byte[]> divineIntoBlocksFile(byte[]data){
        int blockCount=(int) Math.ceil((double) data.length/BLOCK_SIZE);
        List<byte[]>blocks=new ArrayList<>(blockCount);
        for(int i=0;i<blockCount;i++){
            int from=i*BLOCK_SIZE;
            int to=Math.min(from+BLOCK_SIZE,data.length);
            byte[] block= Arrays.copyOfRange(data,from,to);
            blocks.add(block);
        }

        return blocks;
    }
    public List<byte[]> divideIntoRSABlocks(byte[] data) {
        List<byte[]> blocks = new ArrayList<>();
        for (int start = 0; start < data.length; start += BLOCK_SIZE) {
            int end = Math.min(start + BLOCK_SIZE, data.length);
            blocks.add(Arrays.copyOfRange(data, start, end));
        }
        return blocks;
    }
}
