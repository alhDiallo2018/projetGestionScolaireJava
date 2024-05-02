package gestionNiveau;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class listerNiveau extends JFrame {
    private Connection connexion;
    private JList<String> niveauList;
    private DefaultListModel<String> listModel;

    public listerNiveau() {
        setTitle("Liste des niveaux");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        listModel = new DefaultListModel<>();
        niveauList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(niveauList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("Ajouter");
        buttonPanel.add(addButton);
        addButton.addActionListener(e -> ajouterNiveau());

        JButton editButton = new JButton("Modifier");
        buttonPanel.add(editButton);
        editButton.addActionListener(e -> modifierNiveau());

        JButton deleteButton = new JButton("Supprimer");
        buttonPanel.add(deleteButton);
        deleteButton.addActionListener(e -> supprimerNiveau());

        chargerNiveaux();

        setVisible(true);
    }

    private void chargerNiveaux() {
        listModel.clear();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/gestionScolaire";
            String utilisateur = "root";
            String motDePasse = "";
            connexion = DriverManager.getConnection(url, utilisateur, motDePasse);

            String sql = "SELECT nom_niveau FROM niveau";
            PreparedStatement preparedStatement = connexion.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String nomNiveau = resultSet.getString("nom_niveau");
                listModel.addElement(nomNiveau);
            }
            preparedStatement.close();
            connexion.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void ajouterNiveau() {
        String nomNiveau = JOptionPane.showInputDialog(this, "Entrez le nom du nouveau niveau :", "Ajouter un niveau", JOptionPane.PLAIN_MESSAGE);
        if (nomNiveau != null && !nomNiveau.isEmpty()) {
            try (Connection connexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionScolaire", "root", "");
                 PreparedStatement preparedStatement = connexion.prepareStatement("INSERT INTO niveau (nom_niveau) VALUES (?)")) {
                preparedStatement.setString(1, nomNiveau);
                int lignesAffectees = preparedStatement.executeUpdate();
                if (lignesAffectees > 0) {
                    JOptionPane.showMessageDialog(this, "Le niveau a été ajouté avec succès !");
                    chargerNiveaux();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur : Le niveau n'a pas pu être ajouté.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        }
    }

    private void modifierNiveau() {
        int selectedIndex = niveauList.getSelectedIndex();
        if (selectedIndex != -1) {
            String ancienNom = niveauList.getSelectedValue();
            String nouveauNom = JOptionPane.showInputDialog(this, "Entrez le nouveau nom pour le niveau :", "Modifier le niveau", JOptionPane.PLAIN_MESSAGE);
            if (nouveauNom != null && !nouveauNom.isEmpty()) {
                try (Connection connexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionScolaire", "root", "");
                     PreparedStatement preparedStatement = connexion.prepareStatement("UPDATE niveau SET nom_niveau = ? WHERE nom_niveau = ?")) {
                    preparedStatement.setString(1, nouveauNom);
                    preparedStatement.setString(2, ancienNom);
                    int lignesAffectees = preparedStatement.executeUpdate();
                    if (lignesAffectees > 0) {
                        JOptionPane.showMessageDialog(this, "Le niveau a été modifié avec succès !");
                        chargerNiveaux();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur : Le niveau n'a pas pu être modifié.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un niveau à modifier.");
        }
    }

    private void supprimerNiveau() {
        int selectedIndex = niveauList.getSelectedIndex();
        if (selectedIndex != -1) {
            String nomNiveau = niveauList.getSelectedValue();
            int confirmation = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer le niveau \"" + nomNiveau + "\" ?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                try (Connection connexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionScolaire", "root", "");
                     PreparedStatement preparedStatement = connexion.prepareStatement("DELETE FROM niveau WHERE nom_niveau = ?")) {
                    preparedStatement.setString(1, nomNiveau);
                    int lignesAffectees = preparedStatement.executeUpdate();
                    if (lignesAffectees > 0) {
                        JOptionPane.showMessageDialog(this, "Le niveau a été supprimé avec succès !");
                        chargerNiveaux();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur : Le niveau n'a pas pu être supprimé.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un niveau à supprimer.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(listerNiveau::new);
    }
}
