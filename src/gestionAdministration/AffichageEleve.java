package gestionAdministration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

public class AffichageEleve extends JFrame {
    private static final String[] COLUMN_NAMES = {"ID", "Prénoms", "Noms", "Date de naissance", "Lieu de naissance", "Sexe", "Photo", "Téléphone", "Matricule", "CNI", "Email", "Modifier", "Supprimer", "Voir Plus"};
    private static final String SELECT_QUERY = "SELECT * FROM utilisateur WHERE role_id = 2";
    private boolean nouvellePhotoSelectionnee = false;

    private Connection conn;

    public AffichageEleve() {
        super("Gestion des élèves");
        initializeDatabase();
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionScolaire", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initComponents() {
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(SELECT_QUERY);

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column >= 12; // Seules les colonnes "Modifier" et "Supprimer" sont éditables
                }
            };
            model.setColumnIdentifiers(new Object[]{"ID", "Prénom", "Nom", "Date de naissance", "Lieu de naissance", "Sexe", "photo", "Téléphone", "Matricule", "CNI", "Email", "Modifier", "Supprimer", "Voir plus"});

            model.setColumnIdentifiers(COLUMN_NAMES);

            while (rs.next()) {
                Object[] rowData = new Object[COLUMN_NAMES.length]; // Pas besoin d'ajouter d'élément supplémentaire
                for (int i = 0; i < COLUMN_NAMES.length - 3; i++) { // Supprimer les deux dernières colonnes
                    rowData[i] = rs.getObject(i + 1); // Les données de la base de données commencent à l'index 1
                }
                rowData[COLUMN_NAMES.length - 3] = "Modifier";
                rowData[COLUMN_NAMES.length - 2] = "Supprimer";
                rowData[COLUMN_NAMES.length - 1] = "Voir plus"; // Ajout du bouton "Voir plus" après la colonne "Supprimer"
                model.addRow(rowData);
            }

            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            int[] columnWidths = {50, 100, 100, 120, 150, 50, 100, 100, 100, 150, 80, 80, 80};
            for (int i = 0; i < columnWidths.length; i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                    int row = e.getY() / table.getRowHeight();

                    if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                        Object value = table.getValueAt(row, column);
                        if (value instanceof String) {
                            String strValue = (String) value;
                            if (strValue.equals("Modifier")) {
                                int id = (int) table.getValueAt(row, 0);
                                showEditDialog(id);
                            } else if (strValue.equals("Supprimer")) {
                                int id = (int) table.getValueAt(row, 0);
                                supprimerElement(id);
                            } else if (strValue.equals("Voir plus")) {
                                int id = (int) table.getValueAt(row, 0);
                                afficherInfosEleve(id);

                            }
                        }
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDialog(int id) {
        try {
            String query = "SELECT * FROM utilisateur WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JFrame frame = new JFrame("Modification de l'élève");
                JPanel panel = new JPanel(new BorderLayout(5, 5));

                panel.add(createInfoPanel(rs), BorderLayout.CENTER);

                // Ajout d'un bouton pour modifier la photo
                JButton editPhotoButton = new JButton("Modifier photo");
                editPhotoButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    int returnValue = fileChooser.showOpenDialog(frame);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        updatePhoto(id, selectedFile);
                    }

                });
                panel.add(editPhotoButton, BorderLayout.SOUTH);

                // Affichage de la photo actuelle
                JPanel photoPanel = createPhotoPanel(rs);
                panel.add(photoPanel, BorderLayout.EAST);

                int result = JOptionPane.showConfirmDialog(frame, panel, "Modifier l'élève", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    modifier(id, panel);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucun élève trouvé avec l'ID : " + id, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données de l'élève", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void updatePhoto(int id, File selectedFile) {
        try {
            String query = "UPDATE utilisateur SET photo = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, selectedFile.getAbsolutePath());
            pstmt.setInt(2, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Photo mise à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la mise à jour de la photo.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de la photo de l'élève", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

    }


    private JPanel createInfoPanel(ResultSet rs) throws SQLException {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        panel.add(new JLabel("Prénoms:"));
        panel.add(new JTextField(rs.getString("prenom")));
        panel.add(new JLabel("Noms:"));
        panel.add(new JTextField(rs.getString("nom")));
        panel.add(new JLabel("Date de naissance:"));
        panel.add(new JTextField(rs.getString("date_naissance")));
        panel.add(new JLabel("Lieu de naissance:"));
        panel.add(new JTextField(rs.getString("lieu_naissance")));
        panel.add(new JLabel("Sexe:"));
        panel.add(new JTextField(rs.getString("sexe")));
        panel.add(new JLabel("Téléphone:"));
        panel.add(new JTextField(rs.getString("telephone")));
        panel.add(new JLabel("Matricule:"));
        panel.add(new JTextField(rs.getString("matricule")));
        panel.add(new JLabel("CNI:"));
        panel.add(new JTextField(rs.getString("cni")));
        panel.add(new JLabel("Email:"));
        panel.add(new JTextField(rs.getString("email")));

        return panel;
    }

    private JPanel createPhotoPanel(ResultSet rs) throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());

        try {
            String imagePath = rs.getString("photo");
            URL imageURL;
            if (imagePath.startsWith("file://")) {
                imageURL = new URL(imagePath);
            } else {
                imageURL = new URL("file://" + imagePath); // Ajoute le préfixe "file://" si nécessaire
            }
            ImageIcon imageIcon = new ImageIcon(imageURL);
            Image image = imageIcon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
            JLabel photoLabel = new JLabel(new ImageIcon(image));
            panel.add(photoLabel, BorderLayout.CENTER);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return panel;
    }


    private void afficherInfosEleve(int id) {
        try {
            String query = "SELECT * FROM utilisateur WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JFrame frame = new JFrame("Informations de l'élève");
                JPanel panel = new JPanel(new BorderLayout(5, 5));

                panel.add(createInfoPanel(rs), BorderLayout.CENTER);

                JPanel photoPanel = createPhotoPanel(rs);
                panel.add(photoPanel, BorderLayout.EAST);

                JOptionPane.showMessageDialog(frame, panel, "Informations de l'élève", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Aucun élève trouvé avec l'ID : " + id, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données de l'élève", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerElement(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet élève ?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM utilisateur WHERE id = ?");
                pstmt.setInt(1, id);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Élève supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Échec de la suppression de l'élève.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'élève", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifier(int id, JPanel editPanel) {
        try {
            String prenom = "", nom = "", dateNaissance = "", lieuNaissance = "", sexe = "", telephone = "", matricule = "", cni = "", email = "", photoPath = "";

            Component[] components = editPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JTextField) {
                    JTextField textField = (JTextField) component;
                    switch (textField.getName()) {
                        case "prenom":
                            prenom = textField.getText();
                            break;
                        case "nom":
                            nom = textField.getText();
                            break;
                        case "dateNaissance":
                            dateNaissance = textField.getText();
                            break;
                        case "lieuNaissance":
                            lieuNaissance = textField.getText();
                            break;
                        case "sexe":
                            sexe = textField.getText();
                            break;
                        case "telephone":
                            telephone = textField.getText();
                            break;
                        case "matricule":
                            matricule = textField.getText();
                            break;
                        case "cni":
                            cni = textField.getText();
                            break;
                        case "email":
                            email = textField.getText();
                            break;
                        case "photoPath":
                            photoPath = textField.getText(); // Récupérer le chemin d'accès actuel de la photo
                            break;
                        // Ajoutez d'autres cas pour les autres champs si nécessaire
                    }
                }
            }

            // Ouvrir un sélecteur de fichiers pour permettre à l'utilisateur de choisir une nouvelle photo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // Enregistrer le chemin d'accès de la nouvelle photo dans la base de données
                photoPath = selectedFile.getAbsolutePath();
            }

            // Exécuter la mise à jour de la base de données avec les valeurs récupérées
            String query = "UPDATE utilisateur SET prenom = ?, nom = ?, date_naissance = ?, lieu_naissance = ?, sexe = ?, telephone = ?, matricule = ?, cni = ?, email = ?, photo = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, prenom);
            pstmt.setString(2, nom);
            pstmt.setString(3, dateNaissance);
            pstmt.setString(4, lieuNaissance);
            pstmt.setString(5, sexe);
            pstmt.setString(6, telephone);
            pstmt.setString(7, matricule);
            pstmt.setString(8, cni);
            pstmt.setString(9, email);
            pstmt.setString(10, photoPath); // Utiliser le nouveau chemin d'accès de la photo
            pstmt.setInt(11, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Données mises à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la mise à jour des données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour des données de l'élève", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }



    public static void main (String[] args){

            SwingUtilities.invokeLater(AffichageEleve::new);


        }
    }
