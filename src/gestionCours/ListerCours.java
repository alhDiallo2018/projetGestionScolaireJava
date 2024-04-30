package gestionCours;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListerCours extends JFrame {
    private static final String[] COLUMN_NAMES = {"ID", "Nom du cours", "Description", "Professeur", "Niveau", "Matière", "Date de début", "Date de fin", "Modifier", "Supprimer", "Voir Plus"};
    private static final String SELECT_QUERY = "SELECT cours.cours_id, cours.nom_cours, cours.description_cours, utilisateur.prenom AS professeur, niveau.nom_niveau, matiere.nom_matiere, cours.debut_cours, cours.fin_cours " +
            "FROM cours " +
            "INNER JOIN utilisateur ON cours.utilisateur_id = utilisateur.id " +
            "INNER JOIN niveau ON cours.niveau_id = niveau.id_niveau " +
            "INNER JOIN matiere ON cours.matiere_id = matiere.id";


    private Connection conn;

    public ListerCours() {
        super("Gestion des cours");
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
                    return column >= 8; // Seules les colonnes "Modifier", "Supprimer" et "Voir Plus" sont éditables
                }
            };
            model.setColumnIdentifiers(COLUMN_NAMES);

            while (rs.next()) {
                Object[] rowData = new Object[COLUMN_NAMES.length];
                for (int i = 0; i < COLUMN_NAMES.length - 3; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                rowData[COLUMN_NAMES.length - 3] = "Modifier";
                rowData[COLUMN_NAMES.length - 2] = "Supprimer";
                rowData[COLUMN_NAMES.length - 1] = "Voir Plus";
                model.addRow(rowData);
            }

            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            int[] columnWidths = {50, 150, 200, 100, 100, 80, 80, 80, 80, 80, 80};
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
                                supprimerCours(id);
                            } else if (strValue.equals("Voir Plus")) {
                                int id = (int) table.getValueAt(row, 0);
                                afficherInfosCours(id);
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
            String query = "SELECT cours.*, utilisateur.prenom AS professeur_nom, niveau.nom_niveau, matiere.nom_matiere, salle.numero_salle " +
                    "FROM cours " +
                    "INNER JOIN utilisateur ON cours.utilisateur_id = utilisateur.id " +
                    "INNER JOIN niveau ON cours.niveau_id = niveau.id_niveau " +
                    "INNER JOIN matiere ON cours.matiere_id = matiere.id " +
                    "INNER JOIN salle ON cours.salle_id = salle.id " +
                    "WHERE cours_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JFrame frame = new JFrame("Modification du cours");
                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

                panel.add(new JLabel("Nom du cours:"));
                JTextField nomCoursField = new JTextField(rs.getString("nom_cours"));
                panel.add(nomCoursField);

                panel.add(new JLabel("Professeur:"));
                JComboBox<String> professeurComboBox = createComboBox(getProfesseurs());
                professeurComboBox.setSelectedItem(rs.getString("professeur_nom"));
                panel.add(professeurComboBox);

                panel.add(new JLabel("Niveau:"));
                JComboBox<String> niveauComboBox = createComboBox(getNiveaux());
                niveauComboBox.setSelectedItem(rs.getString("nom_niveau"));
                panel.add(niveauComboBox);

                panel.add(new JLabel("Matière:"));
                JComboBox<String> matiereComboBox = createComboBox(getMatieres());
                matiereComboBox.setSelectedItem(rs.getString("nom_matiere"));
                panel.add(matiereComboBox);


                panel.add(new JLabel("Description du cours:"));
                JTextField descriptionCoursField = new JTextField(rs.getString("description_cours"));
                panel.add(descriptionCoursField);

                panel.add(new JLabel("Salle:"));
                JComboBox<String> salleComboBox = createComboBox(getSalles());
                salleComboBox.setSelectedItem(rs.getString("numero_salle")); // Remplacez par la colonne appropriée
                panel.add(salleComboBox);



                int result = JOptionPane.showConfirmDialog(frame, panel, "Modifier le cours", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    // Récupérer l'ID de l'utilisateur sélectionné
                    String selectedProfesseur = (String) professeurComboBox.getSelectedItem();
                    int userId = getUserId(selectedProfesseur);


                    String selectedNiveau = (String) niveauComboBox.getSelectedItem();
                    int niveauId = getNiveauId(selectedNiveau);

                    String selectedMatiere = (String) matiereComboBox.getSelectedItem();
                    int matiereId = getMatiereId(selectedMatiere);

                    String selectedSalle = (String) salleComboBox.getSelectedItem();
                    int salleId = getSalleId(selectedSalle);

                    modifierCours(id, nomCoursField.getText(), descriptionCoursField.getText(), userId, niveauId, matiereId, salleId);


                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucun cours trouvé avec l'ID : " + id, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données du cours", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getUserId(String userName) throws SQLException {
        String query = "SELECT id FROM utilisateur WHERE prenom = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Retourne -1 si aucun ID n'est trouvé
    }

    private int getNiveauId(String niveauNom) throws SQLException {
        String query = "SELECT id_niveau FROM niveau WHERE nom_niveau = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, niveauNom);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_niveau");
                }
            }
        }
        return -1; // Retourne -1 si aucun ID de niveau n'est trouvé
    }

    private int getMatiereId(String matiereNom) throws SQLException {
        String query = "SELECT id FROM matiere WHERE nom_matiere = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, matiereNom);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Retourne -1 si aucun ID de matière n'est trouvé
    }

    private int getSalleId(String numeroSalle) throws SQLException {
        String query = "SELECT id FROM salle WHERE numero_salle = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1,numeroSalle);
            try (ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }


    private void modifierCours(int id, String nomCours, String descriptionCours, int utilisateurId, int niveauId, int matiereId, int salleId) {
        try {
            String query = "UPDATE cours SET nom_cours = ?, description_cours = ?, utilisateur_id = ?, niveau_id = ?, matiere_id = ?, salle_id = ? WHERE cours_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nomCours);
            pstmt.setString(2, descriptionCours);
            // Convertir l'ID de l'utilisateur en une chaîne de caractères avant de l'utiliser
            pstmt.setString(3, String.valueOf(utilisateurId));
            pstmt.setString(4, String.valueOf(niveauId));
            pstmt.setString(5, String.valueOf(matiereId));
            pstmt.setString(6, String.valueOf(salleId));
            pstmt.setInt(7, id);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Cours mis à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la mise à jour du cours.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du cours", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComboBox<String> createComboBox(List<String> items) {
        JComboBox<String> comboBox = new JComboBox<>();
        for (String item : items) {
            comboBox.addItem(item);
        }
        return comboBox;
    }

    // Méthode pour récupérer la liste des professeurs depuis la base de données
    private List<String> getProfesseurs() throws SQLException {
        List<String> professeurs = new ArrayList<>();
        String query = "SELECT prenom FROM utilisateur WHERE role_id = 5";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                professeurs.add(rs.getString("prenom"));
            }
        }
        return professeurs;
    }
    // Méthode pour récupérer la liste des salles depuis la base de données
    private List<String> getSalles() throws SQLException {
        List<String> salles = new ArrayList<>();
        String query = "SELECT numero_salle FROM salle";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                salles.add(rs.getString("numero_salle"));
            }
        }
        return salles;
    }


    // Méthode pour récupérer la liste des niveaux depuis la base de données
    private List<String> getNiveaux() throws SQLException {
        List<String> niveaux = new ArrayList<>();
        String query = "SELECT nom_niveau FROM niveau";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                niveaux.add(rs.getString("nom_niveau"));
            }
        }
        return niveaux;
    }

    // Méthode pour récupérer la liste des matières depuis la base de données
    private List<String> getMatieres() throws SQLException {
        List<String> matieres = new ArrayList<>();
        String query = "SELECT nom_matiere FROM matiere";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                matieres.add(rs.getString("nom_matiere"));
            }
        }
        return matieres;
    }

    private void afficherInfosCours(int id) {
        try {
            String query = "SELECT cours.*, utilisateur.prenom AS professeur_nom, niveau.nom_niveau, matiere.nom_matiere " +
                    "FROM cours " +
                    "INNER JOIN utilisateur ON cours.utilisateur_id = utilisateur.id " +
                    "INNER JOIN niveau ON cours.niveau_id = niveau.id_niveau " +
                    "INNER JOIN matiere ON cours.matiere_id = matiere.id " +
                    "WHERE cours_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JFrame frame = new JFrame("Informations du cours");
                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

                panel.add(new JLabel("Nom du cours:"));
                panel.add(new JLabel(rs.getString("nom_cours")));
                panel.add(new JLabel("Description du cours:"));
                panel.add(new JLabel(rs.getString("description_cours")));
                panel.add(new JLabel("Professeur:"));
                panel.add(new JLabel(rs.getString("professeur_nom")));
                panel.add(new JLabel("Niveau:"));
                panel.add(new JLabel(rs.getString("nom_niveau")));
                panel.add(new JLabel("Matière:"));
                panel.add(new JLabel(rs.getString("nom_matiere")));
                panel.add(new JLabel("Début du cours:"));
                panel.add(new JLabel(rs.getString("debut_cours")));
                panel.add(new JLabel("Fin du cours:"));
                panel.add(new JLabel(rs.getString("fin_cours")));

                JOptionPane.showMessageDialog(frame, panel, "Informations du cours", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Aucun cours trouvé avec l'ID : " + id, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données du cours", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerCours(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce cours ?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM cours WHERE cours_id = ?");
                pstmt.setInt(1, id);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Cours supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Échec de la suppression du cours.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du cours", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ListerCours::new);
    }
}











