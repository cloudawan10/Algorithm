package cls.grap;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Point;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GraphGUI extends JPanel {
    // Menyimpan koordinat setiap simpul
    private Map<String, Point> posisiNode = new HashMap<>();

    // Menyimpan keterhubungan antar simpul (graf tak berarah)
    private Map<String, List<String>> adjacencyList = new HashMap<>();

    // Konstruktor
    public GraphGUI() {
        // Menentukan posisi node secara manual (koordinat x, y)
        posisiNode.put("A", new Point(100, 100));
        posisiNode.put("B", new Point(250, 80));
        posisiNode.put("C", new Point(200, 200));
        posisiNode.put("D", new Point(350, 150));
        posisiNode.put("E", new Point(120, 250));

        // Membuat adjacency list
        tambahEdge("A", "B");
        tambahEdge("A", "C");
        tambahEdge("B", "D");
        tambahEdge("C", "D");
        tambahEdge("C", "E");
        tambahEdge("D", "E");
    }

    // Menambahkan edge (graf tak berarah)
    private void tambahEdge(String from, String to) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.putIfAbsent(to, new ArrayList<>());
        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from);
    }

    // Menggambar graf
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        // Gambar edge (garis antar node)
        g2.setColor(Color.GRAY);
        for (String node : adjacencyList.keySet()) {
            Point p1 = posisiNode.get(node);
            for (String tetangga : adjacencyList.get(node)) {
                Point p2 = posisiNode.get(tetangga);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // Gambar node (lingkaran dan label)
        for (String node : posisiNode.keySet()) {
            Point p = posisiNode.get(node);
            g2.setColor(new Color(100, 150, 255));
            g2.fillOval(p.x - 20, p.y - 20, 40, 40);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - 20, p.y - 20, 40, 40);
            g2.drawString(node, p.x - 5, p.y + 5);
        }
    }

    // Menjalankan GUI
    public static void main(String[] args) {
        JFrame frame = new JFrame("Visualisasi Graph Sederhana");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.add(new GraphGUI());
        frame.setLocationRelativeTo(null); // Tengah layar
        frame.setVisible(true);
    }
}