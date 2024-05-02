package gestionEleve;

/*import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AfficherMesCours extends JFrame {
    private static final String[] COLUMN_NAMES = {"ID", "Nom du cours", "Description", "Professeur", "Niveau", "Matière", "Date de début", "Date de fin", "Modifier", "Supprimer", "Voir Plus"};
    private static final String SELECT_QUERY = "SELECT cours.cours_id, cours.nom_cours, cours.description_cours, utilisateur.prenom AS professeur, niveau.nom_niveau, matiere.nom_matiere, cours.debut_cours, cours.fin_cours " +
            "FROM cours " +
            "INNER JOIN utilisateur ON cours.utilisateur_id = utilisateur.id " +
            "INNER JOIN niveau ON cours.niveau_id = niveau.id_niveau " +
            "INNER JOIN matiere ON cours.matiere_id = matiere.id";


    private Connection conn;

    public AfficherMesCours() {
        super("Mes Cours");
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

    private JComboBox<String> createComboBox(java.util.List<String> items) {
        JComboBox<String> comboBox = new JComboBox<>();
        for (String item : items) {
            comboBox.addItem(item);
        }
        return comboBox;
    }

    // Méthode pour récupérer la liste des professeurs depuis la base de données
    private java.util.List<String> getProfesseurs() throws SQLException {
        java.util.List<String> professeurs = new ArrayList<>();
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
    private java.util.List<String> getSalles() throws SQLException {
        java.util.List<String> salles = new ArrayList<>();
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
    private java.util.List<String> getNiveaux() throws SQLException {
        java.util.List<String> niveaux = new ArrayList<>();
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
    private java.util.List<String> getMatieres() throws SQLException {
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
        SwingUtilities.invokeLater(gestionEleve.AfficherMesCours::new);
    }
}
*/
/*
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AfficherMesCours extends JFrame {
    private static final String[] COLUMN_NAMES = {"ID", "Nom du cours", "Description", "Professeur", "Niveau", "Matière", "Date de début", "Date de fin", "Modifier", "Supprimer", "Voir Plus"};

    private Connection conn;
    private int eleveId; // ID de l'élève actuellement connecté

    public AfficherMesCours() {
        super("Mes Cours");
        this.eleveId = eleveId;
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
        int idElebe = getUserId();
        try {
            String SELECT_QUERY = "SELECT cours.cours_id, cours.nom_cours, cours.description_cours, utilisateur.prenom AS professeur, niveau.nom_niveau, matiere.nom_matiere, cours.debut_cours, cours.fin_cours " +
                    "FROM cours " +
                    "INNER JOIN utilisateur ON cours.utilisateur_id = utilisateur.id " +
                    "INNER JOIN niveau ON cours.niveau_id = niveau.id_niveau " +
                    "INNER JOIN matiere ON cours.matiere_id = matiere.id " +
                    "INNER JOIN eleve ON eleve.niveau_id = niveau.id_niveau AND eleve.classe_id = cours.classe_id " +
                    "WHERE eleve.id = ?";

            PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
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

    private JComboBox<String> createComboBox(java.util.List<String> items) {
        JComboBox<String> comboBox = new JComboBox<>();
        for (String item : items) {
            comboBox.addItem(item);
        }
        return comboBox;
    }

    // Méthode pour récupérer la liste des professeurs depuis la base de données
    private java.util.List<String> getProfesseurs() throws SQLException {
        java.util.List<String> professeurs = new ArrayList<>();
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
    private java.util.List<String> getSalles() throws SQLException {
        java.util.List<String> salles = new ArrayList<>();
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
    private java.util.List<String> getNiveaux() throws SQLException {
        java.util.List<String> niveaux = new ArrayList<>();
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
    private java.util.List<String> getMatieres() throws SQLException {
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
        SwingUtilities.invokeLater(gestionEleve.AfficherMesCours::new);
    }
}*/




import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AfficherMesCours extends JFrame {
    private Connection conn;
    private int eleveId;
    private int classeId;
    private int niveauId;

    public AfficherMesCours() {
        super("Espace Élève");
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
        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Mot de passe:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Se connecter");

        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Empty label for spacing
        loginPanel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                eleveId = authenticateEleve(username, password);
                if (eleveId != -1) {
                    showCoursForEleve();
                } else {
                    JOptionPane.showMessageDialog(AfficherMesCours.this, "Nom d'utilisateur ou mot de passe incorrect", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(loginPanel, BorderLayout.CENTER);
    }

    private int authenticateEleve(String username, String password) {
        try {
            String query = "SELECT id, classe_id, niveau_id FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                eleveId = rs.getInt("id");
                classeId = rs.getInt("classe_id");
                niveauId = rs.getInt("niveau_id");
                return eleveId;

            }
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'authentification", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    private void showCoursForEleve() {
        try {
            String SELECT_QUERY = "SELECT cours.cours_id, cours.nom_cours, cours.description_cours, utilisateur.prenom AS professeur, niveau.nom_niveau, matiere.nom_matiere, cours.debut_cours, cours.fin_cours " +
                    "FROM cours " +
                    "INNER JOIN utilisateur ON cours.utilisateur_id = utilisateur.id " +
                    "INNER JOIN niveau ON cours.niveau_id = niveau.id_niveau " +
                    "INNER JOIN matiere ON cours.matiere_id = matiere.id " +
                    "WHERE cours.classe_id = ? AND cours.niveau_id = ?";

            PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY);
            stmt.setInt(1, classeId);
            stmt.setInt(2, niveauId);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"ID", "Nom du cours", "Description", "Professeur", "Niveau", "Matière", "Date de début", "Date de fin"});

            while (rs.next()) {
                Object[] rowData = new Object[8];
                rowData[0] = rs.getInt("cours_id");
                rowData[1] = rs.getString("nom_cours");
                rowData[2] = rs.getString("description_cours");
                rowData[3] = rs.getString("professeur");
                rowData[4] = rs.getString("nom_niveau");
                rowData[5] = rs.getString("nom_matiere");
                rowData[6] = rs.getString("debut_cours");
                rowData[7] = rs.getString("fin_cours");
                model.addRow(rowData);
            }

            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane scrollPane = new JScrollPane(table);

            JFrame frame = new JFrame("Cours de l'élève");
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données des cours de l'élève", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AfficherMesCours::new);
    }
}









