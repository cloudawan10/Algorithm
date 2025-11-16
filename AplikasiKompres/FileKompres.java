// FileCompressor.java
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

public class FileKompres {
    private static final DecimalFormat df = new DecimalFormat("#.##");
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("==========================================");
        System.out.println("    Aplikasi Kompres File Teks");
        System.out.println("==========================================");
        
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Kompresi File");
            System.out.println("2. Dekompresi File");
            System.out.println("3. Keluar");
            System.out.print("Pilih menu (1-3): ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    compressFile(scanner);
                    break;
                case "2":
                    decompressFile(scanner);
                    break;
                case "3":
                    System.out.println("Terima kasih telah menggunakan program!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Pilihan tidak valid! Silakan pilih 1-3.");
            }
        }
    }
    
    private static void compressFile(Scanner scanner) {
        System.out.println("\n--- KOMPRESI FILE ---");
        System.out.print("Masukkan nama file yang akan dikompresi: ");
        String inputFileName = scanner.nextLine();
        
        File inputFile = new File(inputFileName);
        
        // Validasi file
        if (!inputFile.exists()) {
            System.out.println("File '" + inputFileName + "' tidak ditemukan!");
            return;
        }
        
        if (inputFile.isDirectory()) {
            System.out.println(" '" + inputFileName + "' adalah direktori, bukan file!");
            return;
        }
        
        if (inputFile.length() == 0) {
            System.out.println(" File '" + inputFileName + "' kosong!");
            return;
        }
        
        String outputFileName = inputFileName + ".huff";
        File outputFile = new File(outputFileName);
        
        try {
            // Tampilkan informasi file asli
            displayFileInfo("ASLI", inputFile);
            
            System.out.println("\nMelakukan kompresi...");
            long startTime = System.currentTimeMillis();
            
            // Baca konten file
            String content = readTextFile(inputFile);
            
            // Lakukan kompresi Huffman
            AlgoHuffman compressor = new AlgoHuffman();
            compressor.compress(content, outputFileName);
            
            long endTime = System.currentTimeMillis();
            long compressionTime = endTime - startTime;
            
            // Tampilkan informasi file hasil kompresi
            displayFileInfo("HASIL KOMPRESI", outputFile);
            
            // Hitung dan tampilkan rasio kompresi
            displayCompressionRatio(inputFile, outputFile, compressionTime);
            
            System.out.println("\n Kompresi berhasil! File disimpan sebagai: " + outputFileName);
            
        } catch (IOException e) {
            System.out.println("Error selama kompresi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void decompressFile(Scanner scanner) {
        System.out.println("\n--- DEKOMPRESI FILE ---");
        System.out.print("Masukkan nama file .huff yang akan didekompresi: ");
        String inputFileName = scanner.nextLine();
        
        File inputFile = new File(inputFileName);
        
        // Validasi file
        if (!inputFile.exists()) {
            System.out.println("File '" + inputFileName + "' tidak ditemukan!");
            return;
        }
        
        if (!inputFileName.endsWith(".huff")) {
            System.out.println("File harus berformat .huff!");
            return;
        }
        
        String outputFileName = inputFileName.replace(".huff", "_decompressed.txt");
        File outputFile = new File(outputFileName);
        
        try {
            // Tampilkan informasi file terkompresi
            displayFileInfo("TERKOMPRESI", inputFile);
            
            System.out.println("\nMelakukan dekompresi...");
            long startTime = System.currentTimeMillis();
            
            // Lakukan dekompresi
            AlgoHuffman compressor = new AlgoHuffman();
            compressor.decompress(inputFileName, outputFileName);
            
            long endTime = System.currentTimeMillis();
            long decompressionTime = endTime - startTime;
            
            // Tampilkan informasi file hasil dekompresi
            displayFileInfo("HASIL DEKOMPRESI", outputFile);
            
            System.out.println("\nWaktu dekompresi: " + decompressionTime + " ms");
            System.out.println("Dekompresi berhasil! File disimpan sebagai: " + outputFileName);
            
        } catch (IOException e) {
            System.out.println("Error selama dekompresi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static String readTextFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        
        // Hapus newline terakhir jika ada
        if (content.length() > 0 && content.charAt(content.length() - 1) == '\n') {
            content.deleteCharAt(content.length() - 1);
        }
        
        return content.toString();
    }
    
    private static void displayFileInfo(String type, File file) {
        System.out.println("\n📄 INFORMASI FILE " + type + ":");
        System.out.println("   Nama File: " + file.getName());
        System.out.println("   Format: " + getFileExtension(file));
        System.out.println("   Ukuran: " + file.length() + " bytes (" + formatFileSize(file.length()) + ")");
        System.out.println("   Path: " + file.getAbsolutePath());
    }
    
    private static void displayCompressionRatio(File originalFile, File compressedFile, long compressionTime) {
        long originalSize = originalFile.length();
        long compressedSize = compressedFile.length();
        double ratio = (double) compressedSize / originalSize * 100;
        double spaceSaving = 100 - ratio;
        long sizeReduction = originalSize - compressedSize;
        
        System.out.println("\n📊 HASIL KOMPRESI:");
        System.out.println("   Ukuran Asli:      " + originalSize + " bytes (" + formatFileSize(originalSize) + ")");
        System.out.println("   Ukuran Kompresi:  " + compressedSize + " bytes (" + formatFileSize(compressedSize) + ")");
        System.out.println("   Pengurangan:      " + sizeReduction + " bytes (" + formatFileSize(sizeReduction) + ")");
        System.out.println("   Rasio Kompresi:   " + df.format(ratio) + "%");
        System.out.println("   Penghematan:      " + df.format(spaceSaving) + "%");
        System.out.println("   Waktu Kompresi:   " + compressionTime + " ms");
        
        // Analisis efektivitas
        System.out.println("\n💡 ANALISIS:");
        if (ratio < 80) {
            System.out.println("Kompresi SANGAT EFEKTIF");
        } else if (ratio < 100) {
            System.out.println("Kompresi efektif");
        } else if (ratio == 100) {
            System.out.println("Ukuran file tetap sama");
        } else {
            System.out.println("File menjadi lebih besar (tidak efektif)");
        }
    }
    
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0 && lastDot < name.length() - 1) {
            return name.substring(lastDot + 1).toUpperCase();
        }
        return "Tidak ada";
    }
    
    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return df.format(size / 1024.0) + " KB";
        } else {
            return df.format(size / (1024.0 * 1024.0)) + " MB";
        }
    }
}