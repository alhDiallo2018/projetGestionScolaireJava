package gestionNiveau;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;


public class ajouterNiveau extends JFrame {
    private JTextField textFieldNomNiveau;
    private Connection connexion;

    public ajouterNiveau() {
        setTitle("Ajouter un niveau");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme seulement la fenêtre actuelle
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JLabel labelNomNiveau = new JLabel("Nom du niveau:");
        inputPanel.add(labelNomNiveau);

        textFieldNomNiveau = new JTextField();
        inputPanel.add(textFieldNomNiveau);

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
                    ajouterNiveau();
                }
            });

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }

        setVisible(true);
    }

    private void ajouterNiveau() {
        String nomNiveau = textFieldNomNiveau.getText();

        try {
            String sql = "INSERT INTO niveau (nom_niveau) VALUES (?)";
            PreparedStatement preparedStatement = connexion.prepareStatement(sql);
            preparedStatement.setString(1, nomNiveau);

            int lignesAffectees = preparedStatement.executeUpdate();

            if (lignesAffectees > 0) {
                JOptionPane.showMessageDialog(this, "Le niveau a été ajouté avec succès !");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : Le niveau n'a pas pu être ajouté.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ajouterNiveau::new);
    }
}
