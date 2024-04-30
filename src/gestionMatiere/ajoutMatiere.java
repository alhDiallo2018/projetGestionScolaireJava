package gestionMatiere;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ajoutMatiere extends JFrame {
    private JTextField textFieldNomMatiere;
    private JTextArea textAreaDescriptionMatiere;
    private Connection connexion;

    public ajoutMatiere() {
        setTitle("Ajouter une matière");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JLabel labelNomMatiere = new JLabel("Nom de la matière:");
        inputPanel.add(labelNomMatiere);

        textFieldNomMatiere = new JTextField();
        inputPanel.add(textFieldNomMatiere);

        JLabel labelDescriptionMatiere = new JLabel("Description:");
        inputPanel.add(labelDescriptionMatiere);

        textAreaDescriptionMatiere = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textAreaDescriptionMatiere);
        inputPanel.add(scrollPane);

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
                    String nomMatiere = textFieldNomMatiere.getText();
                    String descriptionMatiere = textAreaDescriptionMatiere.getText();

                    ajouterMatiere(nomMatiere, descriptionMatiere);
                }
            });

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }

        setVisible(true);
    }

    private void ajouterMatiere(String nomMatiere, String descriptionMatiere) {
        String sql = "INSERT INTO matiere (nom_matiere, description_matiere) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nomMatiere);
            preparedStatement.setString(2, descriptionMatiere);

            int lignesAffectees = preparedStatement.executeUpdate();

            if (lignesAffectees > 0) {
                JOptionPane.showMessageDialog(this, "La matière a été ajoutée avec succès !");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : La matière n'a pas pu être ajoutée.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ajoutMatiere::new);
    }
}
