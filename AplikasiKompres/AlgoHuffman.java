// HuffmanCompressor.java
import java.io.*;
import java.util.*;

class HuffmanNode implements Comparable<HuffmanNode> {
    char character;
    int frequency;
    HuffmanNode left, right;
    
    public HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }
    
    public HuffmanNode(char character, int frequency, HuffmanNode left, HuffmanNode right) {
        this.character = character;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }
    
    public boolean isLeaf() {
        return left == null && right == null;
    }
    
    @Override
    public int compareTo(HuffmanNode other) {
        return this.frequency - other.frequency;
    }
}

public class AlgoHuffman {
    private Map<Character, String> huffmanCodes;
    private Map<Character, Integer> frequencyMap;
    
    public AlgoHuffman() {
        huffmanCodes = new HashMap<>();
        frequencyMap = new HashMap<>();
    }
    
    public void compress(String content, String outputFileName) throws IOException {
        // Bangun frequency map
        buildFrequencyMap(content);
        
        // Bangun pohon Huffman
        HuffmanNode root = buildHuffmanTree();
        
        // Generate kode Huffman
        generateCodes(root, "");
        
        // Tulis file terkompresi
        writeCompressedFile(content, outputFileName);
    }
    
    public void decompress(String inputFileName, String outputFileName) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(inputFileName));
        
        try {
            // Baca jumlah karakter unik
            int uniqueChars = dis.readByte() & 0xFF;
            
            // Baca frequency map
            frequencyMap.clear();
            for (int i = 0; i < uniqueChars; i++) {
                char character = dis.readChar();
                int frequency = dis.readInt();
                frequencyMap.put(character, frequency);
            }
            
            // Bangun pohon Huffman
            HuffmanNode root = buildHuffmanTree();
            
            // Baca panjang bitstream
            int bitLength = dis.readInt();
            
            // Baca data terkompresi
            int dataLength = dis.readInt();
            byte[] compressedData = new byte[dataLength];
            dis.readFully(compressedData);
            
            // Konversi ke bit string dan decode
            String bitString = convertBytesToBitString(compressedData, bitLength);
            String decodedContent = decodeData(bitString, root);
            
            // Tulis file hasil dekompresi
            writeTextFile(outputFileName, decodedContent);
            
        } finally {
            dis.close();
        }
    }
    
    private void buildFrequencyMap(String content) {
        frequencyMap.clear();
        for (char c : content.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
    }
    
    private HuffmanNode buildHuffmanTree() {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();
        
        // Buat node untuk setiap karakter
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            queue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        
        // Bangun pohon Huffman
        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency, left, right);
            queue.add(parent);
        }
        
        return queue.poll();
    }
    
    private void generateCodes(HuffmanNode node, String code) {
        if (node == null) return;
        
        if (node.isLeaf()) {
            huffmanCodes.put(node.character, code);
            return;
        }
        
        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }
    
    private void writeCompressedFile(String content, String outputFileName) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFileName));
        
        try {
            // Tulis jumlah karakter unik
            dos.writeByte(frequencyMap.size());
            
            // Tulis frequency map
            for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
                dos.writeChar(entry.getKey());
                dos.writeInt(entry.getValue());
            }
            
            // Encode konten sebagai bitstream
            StringBuilder bitString = new StringBuilder();
            for (char c : content.toCharArray()) {
                bitString.append(huffmanCodes.get(c));
            }
            
            int bitLength = bitString.length();
            dos.writeInt(bitLength);
            
            // Konversi ke byte array dan tulis
            byte[] compressedData = convertBitStringToBytes(bitString.toString());
            dos.writeInt(compressedData.length);
            dos.write(compressedData);
            
        } finally {
            dos.close();
        }
    }
    
    private byte[] convertBitStringToBytes(String bitString) {
        int byteCount = (bitString.length() + 7) / 8;
        byte[] bytes = new byte[byteCount];
        
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                int byteIndex = i / 8;
                int bitIndex = 7 - (i % 8);
                bytes[byteIndex] |= (1 << bitIndex);
            }
        }
        
        return bytes;
    }
    
    private String convertBytesToBitString(byte[] bytes, int bitLength) {
        StringBuilder bitString = new StringBuilder();
        
        for (int i = 0; i < bitLength; i++) {
            int byteIndex = i / 8;
            int bitIndex = 7 - (i % 8);
            boolean isSet = (bytes[byteIndex] & (1 << bitIndex)) != 0;
            bitString.append(isSet ? '1' : '0');
        }
        
        return bitString.toString();
    }
    
    private String decodeData(String encodedData, HuffmanNode root) {
        StringBuilder decodedData = new StringBuilder();
        HuffmanNode current = root;
        
        for (char bit : encodedData.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }
            
            if (current.isLeaf()) {
                decodedData.append(current.character);
                current = root;
            }
        }
        
        return decodedData.toString();
    }
    
    private void writeTextFile(String fileName, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        try {
            writer.write(content);
        } finally {
            writer.close();
        }
    }
}