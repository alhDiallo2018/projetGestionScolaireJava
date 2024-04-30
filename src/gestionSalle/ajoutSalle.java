package gestionSalle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ajoutSalle extends JFrame {
    private JTextField textFieldNumeroSalle;
    private JTextField textFieldCapacite;
    private Connection connexion;

    public ajoutSalle() {
        setTitle("Ajouter une salle");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JLabel labelNumeroSalle = new JLabel("Numéro de salle:");
        inputPanel.add(labelNumeroSalle);

        textFieldNumeroSalle = new JTextField();
        inputPanel.add(textFieldNumeroSalle);

        JLabel labelCapacite = new JLabel("Capacité:");
        inputPanel.add(labelCapacite);

        textFieldCapacite = new JTextField();
        inputPanel.add(textFieldCapacite);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/gestionScolaire";
            String utilisateur = "root";
            String motDePasse = "";

            connexion = DriverManager.getConnection(url, utilisateur, motDePasse);

            JButton boutonAjouter = new JButton("Ajouter");
            buttonPanel.add(boutonAjouter);

            boutonAjouter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String numeroSalle = textFieldNumeroSalle.getText();
                    int capacite = Integer.parseInt(textFieldCapacite.getText());

                    ajouterSalle(numeroSalle, capacite);
                }
            });

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }

        setVisible(true);
    }

    private void ajouterSalle(String numeroSalle, int capacite) {
        String sql = "INSERT INTO salle (numero_salle, capacite) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, numeroSalle);
            preparedStatement.setInt(2, capacite);

            int lignesAffectees = preparedStatement.executeUpdate();

            if (lignesAffectees > 0) {
                JOptionPane.showMessageDialog(this, "La salle a été ajoutée avec succès !");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : La salle n'a pas pu être ajoutée.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ajoutSalle::new);
    }
}
