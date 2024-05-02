package gestionExamen;

import javax.swing.*;

public class planifierExamen extends JFrame {
    public planifierExamen() {
        setTitle("Planifier Examen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);
        setVisible(true);
        JPanel panel = new JPanel();
    }

    public static void main(String[] args) {
        new planifierExamen();
    }
}
