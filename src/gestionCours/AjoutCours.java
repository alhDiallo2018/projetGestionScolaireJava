package gestionCours;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;

public class AjoutCours extends JFrame {
    private JTextField textFieldNomCours;
    private JTextField textFieldDescriptionCours;
    private JComboBox<String> comboBoxMatieres;
    private JComboBox<String> comboBoxSalles;
    private JComboBox<String> comboBoxProfesseurs;
    private JComboBox<String> comboBoxClasses;
    private JComboBox<String> comboBoxNiveaux;
    private JSpinner spinnerDebutCours;
    private JSpinner spinnerFinCours;
    private Connection connexion;

    public AjoutCours() {
        setTitle("Ajouter un cours");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        JPanel inputPanel = new JPanel(new GridLayout(9, 2));
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JLabel labelNomCours = new JLabel("Nom du cours:");
        inputPanel.add(labelNomCours);

        textFieldNomCours = new JTextField();
        inputPanel.add(textFieldNomCours);

        JLabel labelDescriptionCours = new JLabel("Description du cours:");
        inputPanel.add(labelDescriptionCours);

        textFieldDescriptionCours = new JTextField();
        inputPanel.add(textFieldDescriptionCours);

        JLabel labelMatiere = new JLabel("Matière:");
        inputPanel.add(labelMatiere);

        comboBoxMatieres = new JComboBox<>();
        inputPanel.add(comboBoxMatieres);

        JLabel labelSalle = new JLabel("Salle:");
        inputPanel.add(labelSalle);

        comboBoxSalles = new JComboBox<>();
        inputPanel.add(comboBoxSalles);

        JLabel labelProfesseur = new JLabel("Professeur:");
        inputPanel.add(labelProfesseur);

        comboBoxProfesseurs = new JComboBox<>();
        inputPanel.add(comboBoxProfesseurs);

        JLabel labelClasse = new JLabel("Classe:");
        inputPanel.add(labelClasse);

        comboBoxClasses = new JComboBox<>();
        inputPanel.add(comboBoxClasses);

        JLabel labelNiveau = new JLabel("Niveau:");
        inputPanel.add(labelNiveau);

        comboBoxNiveaux = new JComboBox<>();
        inputPanel.add(comboBoxNiveaux);

        JLabel labelDebutCours = new JLabel("Début du cours:");
        inputPanel.add(labelDebutCours);

        spinnerDebutCours = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorDebut = new JSpinner.DateEditor(spinnerDebutCours, "yyyy-MM-dd HH:mm:ss");
        spinnerDebutCours.setEditor(dateEditorDebut);
        spinnerDebutCours.setValue(new Date());
        inputPanel.add(spinnerDebutCours);

        JLabel labelFinCours = new JLabel("Fin du cours:");
        inputPanel.add(labelFinCours);

        spinnerFinCours = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorFin = new JSpinner.DateEditor(spinnerFinCours, "yyyy-MM-dd HH:mm:ss");
        spinnerFinCours.setEditor(dateEditorFin);
        spinnerFinCours.setValue(new Date());
        inputPanel.add(spinnerFinCours);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/gestionScolaire";
            String utilisateur = "root";
            String motDePasse = "";

            connexion = DriverManager.getConnection(url, utilisateur, motDePasse);

            remplirListeMatieres();
            remplirListeSalles();
            remplirListeProfesseurs();
            remplirListeClasses();
            remplirListeNiveaux();

            JButton boutonAjouter = new JButton("Ajouter");
            buttonPanel.add(boutonAjouter);

            boutonAjouter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String nomCours = textFieldNomCours.getText();
                    String descriptionCours = textFieldDescriptionCours.getText();
                    String matiereSelectionnee = (String) comboBoxMatieres.getSelectedItem();
                    String salleSelectionnee = (String) comboBoxSalles.getSelectedItem();
                    String professeurSelectionne = (String) comboBoxProfesseurs.getSelectedItem();
                    String classeSelectionnee = (String) comboBoxClasses.getSelectedItem();
                    String niveauSelectionne = (String) comboBoxNiveaux.getSelectedItem();
                    Date debutCours = (Date) spinnerDebutCours.getValue();
                    Date finCours = (Date) spinnerFinCours.getValue();


                    int idSalle = getIdSalle(salleSelectionnee);

                    int idProfesseur = getIdProfesseur(professeurSelectionne);
                    int idClasse = getIdClasse(classeSelectionnee);
                    int idNiveau = getIdNiveau(niveauSelectionne);

                    ajouterCours(nomCours, descriptionCours, matiereSelectionnee, idSalle, idProfesseur, idClasse, idNiveau, debutCours, finCours);
                }
            });

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }

        setVisible(true);
    }

    private void remplirListeMatieres() {
        String sql = "SELECT nom_matiere FROM matiere";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String nomMatiere = resultSet.getString("nom_matiere");
                comboBoxMatieres.addItem(nomMatiere);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    private void remplirListeSalles() {
        String sql = "SELECT id, CONCAT(numero_salle) AS salle_info FROM salle";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int idSalle = resultSet.getInt("id");
                String salleInfo = resultSet.getString("salle_info");
                comboBoxSalles.addItem(idSalle+"-"+salleInfo);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    private void remplirListeProfesseurs() {
        String sql = "SELECT prenom FROM utilisateur WHERE role_id = 1";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String nomProfesseur = resultSet.getString("prenom");
                comboBoxProfesseurs.addItem(nomProfesseur);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    private void remplirListeClasses() {
        String sql = "SELECT surnom_classe FROM classe";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String nomClasse = resultSet.getString("surnom_classe");
                comboBoxClasses.addItem(nomClasse);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    private void remplirListeNiveaux() {
        String sql = "SELECT nom_niveau FROM niveau";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String nomNiveau = resultSet.getString("nom_niveau");
                comboBoxNiveaux.addItem(nomNiveau);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    private int getIdSalle(String salleSelectionnee) {
        String sql = "SELECT id FROM salle WHERE CONCAT(id, '-', numero_salle) = ?";
        int idSalle = -1;

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, salleSelectionnee);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idSalle = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        return idSalle;
    }

    private int getIdProfesseur(String nomProfesseur) {
        String sql = "SELECT id FROM utilisateur WHERE prenom = ? AND role_id = 1";
        int idProfesseur = -1;

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nomProfesseur);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idProfesseur = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        return idProfesseur;
    }

    private int getIdClasse(String nomClasse) {
        String sql = "SELECT id FROM classe WHERE surnom_classe = ?";
        int idClasse = -1;

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nomClasse);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idClasse = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        return idClasse;
    }

    private int getIdNiveau(String nomNiveau) {
        String sql = "SELECT id_niveau FROM niveau WHERE nom_niveau = ?";
        int idNiveau = -1;

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nomNiveau);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idNiveau = resultSet.getInt("id_niveau");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        return idNiveau;
    }


    private void ajouterCours(String nomCours, String descriptionCours, String nomMatiere, int idSalle, int idProfesseur, int idClasse, int idNiveau, Date debutCours, Date finCours) {
        String sql = "INSERT INTO cours (nom_cours, description_cours, salle_id, matiere_id, utilisateur_id, classe_id, niveau_id, debut_cours, fin_cours) VALUES (?, ?, ?, (SELECT id FROM matiere WHERE nom_matiere = ?), ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nomCours);
            preparedStatement.setString(2, descriptionCours);
            preparedStatement.setInt(3, idSalle);
            preparedStatement.setString(4, nomMatiere);
            preparedStatement.setInt(5, idProfesseur);
            preparedStatement.setInt(6, idClasse);
            preparedStatement.setInt(7, idNiveau);
            preparedStatement.setTimestamp(8, new Timestamp(debutCours.getTime()));
            preparedStatement.setTimestamp(9, new Timestamp(finCours.getTime()));

            int lignesAffectees = preparedStatement.executeUpdate();

            if (lignesAffectees > 0) {
                JOptionPane.showMessageDialog(this, "Le cours a été ajouté avec succès !");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : Le cours n'a pas pu être ajouté.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AjoutCours::new);
    }
}
