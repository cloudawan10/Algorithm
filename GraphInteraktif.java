package cls.grap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GraphInteraktif extends JPanel {
    private Map<String, Point> posisiNode = new HashMap<>();
    private Map<String, List<String>> adjacencyList = new HashMap<>();
    private Set<String> visited = new HashSet<>();
    private String startNode = "A";
    private String selectedNode = null;
    private boolean addingEdgeMode = false;

    public GraphInteraktif() {
        // Posisi awal node
        posisiNode.put("A", new Point(100, 100));
        posisiNode.put("B", new Point(250, 80));
        posisiNode.put("C", new Point(200, 200));
        posisiNode.put("D", new Point(350, 150));
        posisiNode.put("E", new Point(120, 250));

        // Tambahkan edge default
        tambahEdge("A", "B");
        tambahEdge("A", "C");
        tambahEdge("B", "D");

        // Tambahkan listener klik mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (addingEdgeMode) {
                    String clickedNode = getNodeAt(e.getPoint());
                    if (clickedNode != null) {
                        if (selectedNode == null) {
                            selectedNode = clickedNode;
                        } else {
                            tambahEdge(selectedNode, clickedNode);
                            selectedNode = null;
                            addingEdgeMode = false;
                            repaint();
                            JOptionPane.showMessageDialog(null, "Edge ditambahkan!");
                        }
                    }
                }
            }
        });
    }

    // Menambahkan edge berarah
    private void tambahEdge(String from, String to) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        if (!adjacencyList.get(from).contains(to)) {
            adjacencyList.get(from).add(to);
        }
    }

    // Cek apakah klik mengenai node
    private String getNodeAt(Point point) {
        for (Map.Entry<String, Point> entry : posisiNode.entrySet()) {
            Point p = entry.getValue();
            if (point.distance(p) <= 20) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Gambar graf
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        // Gambar edge
        g2.setColor(Color.GRAY);
        for (String node : adjacencyList.keySet()) {
            Point p1 = posisiNode.get(node);
            for (String tetangga : adjacencyList.get(node)) {
                Point p2 = posisiNode.get(tetangga);
                drawArrowLine(g2, p1.x, p1.y, p2.x, p2.y, 10, 7);
            }
        }

        // Gambar node
        for (String node : posisiNode.keySet()) {
            Point p = posisiNode.get(node);
            if (visited.contains(node)) {
                g2.setColor(new Color(102, 205, 170)); // hijau saat dikunjungi
            } else if (node.equals(selectedNode)) {
                g2.setColor(Color.ORANGE); // node yang dipilih
            } else {
                g2.setColor(new Color(100, 150, 255)); // default
            }
            g2.fillOval(p.x - 20, p.y - 20, 40, 40);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - 20, p.y - 20, 40, 40);
            g2.drawString(node, p.x - 5, p.y + 5);
        }
    }

    // Gambar garis berarah (dengan panah)
    private void drawArrowLine(Graphics2D g2, int x1, int y1, int x2, int y2, int panahLebar, int panahTinggi) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double jarak = Math.sqrt(dx * dx + dy * dy);
        if (jarak == 0) return;
        double xm = jarak - panahLebar;
        double xn = xm;
        double ym = panahTinggi;
        double yn = -panahTinggi;
        double sin = dy / jarak;
        double cos = dx / jarak;

        double x;
        double y;

        x = xm * cos - ym * sin + x1;
        y = xm * sin + ym * cos + y1;
        int xM = (int) x;
        int yM = (int) y;

        x = xn * cos - yn * sin + x1;
        y = xn * sin + yn * cos + y1;
        int xN = (int) x;
        int yN = (int) y;

        int[] xPoints = {x2, xM, xN};
        int[] yPoints = {y2, yM, yN};

        g2.drawLine(x1, y1, x2, y2);
        g2.fillPolygon(xPoints, yPoints, 3);
    }

    // ======== Traversal BFS =========
    private void runBFS(String start) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                visited.clear();
                repaint();
            });
            delay();
            Queue<String> queue = new LinkedList<>();
            queue.add(start);
            visited.add(start);
            repaint();
            delay();
            while (!queue.isEmpty()) {
                String node = queue.poll();
                for (String tetangga : adjacencyList.getOrDefault(node, Collections.emptyList())) {
                    if (!visited.contains(tetangga)) {
                        visited.add(tetangga);
                        repaint();
                        delay();
                        queue.add(tetangga);
                    }
                }
            }
        }).start();
    }

    // ======== Traversal DFS =========
    private void runDFS(String start) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                visited.clear();
                repaint();
            });
            delay();
            dfsRecursive(start);
        }).start();
    }

    private void dfsRecursive(String node) {
        visited.add(node);
        repaint();
        delay();
        for (String tetangga : adjacencyList.getOrDefault(node, Collections.emptyList())) {
            if (!visited.contains(tetangga)) {
                dfsRecursive(tetangga);
            }
        }
    }

    private void delay() {
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ======== Main GUI ========
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Graf Berarah Interaktif (Tambah Edge & Animasi DFS/BFS)");
            GraphInteraktif panel = new GraphInteraktif();

            JButton btnBFS = new JButton("Mulai BFS");
            JButton btnDFS = new JButton("Mulai DFS");
            JButton btnAddEdge = new JButton("Tambah Edge");

            btnBFS.addActionListener(e -> panel.runBFS(panel.startNode));
            btnDFS.addActionListener(e -> panel.runDFS(panel.startNode));
            btnAddEdge.addActionListener(e -> {
                panel.addingEdgeMode = true;
                panel.selectedNode = null;
                JOptionPane.showMessageDialog(frame, 
                    "Klik dua simpul secara berurutan untuk menambahkan edge berarah (dari node pertama ke node kedua).");
            });

            JPanel controlPanel = new JPanel();
            controlPanel.add(btnAddEdge);
            controlPanel.add(btnBFS);
            controlPanel.add(btnDFS);

            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);
            frame.add(controlPanel, BorderLayout.SOUTH);
            frame.setSize(600, 450);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}