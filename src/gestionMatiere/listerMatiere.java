package gestionMatiere;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;


public class listerMatiere extends JFrame {
    private static final String[] COLUMN_NAMES = {"ID", "Nom de la matière", "Description de la matière", "Modifier", "Supprimer", "Voir Plus"};
    private static final String SELECT_QUERY = "SELECT id, nom_matiere, description_matiere FROM matiere";

    private Connection conn;

    public listerMatiere() {
        super("Gestion des matières");
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
                    return column >= 3; // Seules les colonnes "Modifier", "Supprimer" et "Voir Plus" sont éditables
                }
            };
            model.setColumnIdentifiers(COLUMN_NAMES);

            while (rs.next()) {
                Object[] rowData = new Object[COLUMN_NAMES.length];
                rowData[0] = rs.getObject("id");
                rowData[1] = rs.getObject("nom_matiere");
                rowData[2] = rs.getObject("description_matiere");
                rowData[3] = "Modifier";
                rowData[4] = "Supprimer";
                rowData[5] = "Voir Plus";
                model.addRow(rowData);
            }

            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            int[] columnWidths = {50, 150, 200, 80, 80, 80};
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
                                supprimerMatiere(id);
                            } else if (strValue.equals("Voir Plus")) {
                                int id = (int) table.getValueAt(row, 0);
                                afficherInfosMatiere(id);
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
            String query = "SELECT * FROM matiere WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JFrame frame = new JFrame("Modification de la matière");
                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

                panel.add(new JLabel("Nom de la matière:"));
                JTextField nomMatiereField = new JTextField(rs.getString("nom_matiere"));
                panel.add(nomMatiereField);

                panel.add(new JLabel("Description de la matière:"));
                JTextField descriptionMatiereField = new JTextField(rs.getString("description_matiere"));
                panel.add(descriptionMatiereField);

                int result = JOptionPane.showConfirmDialog(frame, panel, "Modifier la matière", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    modifierMatiere(id, nomMatiereField.getText(), descriptionMatiereField.getText());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucune matière trouvée avec l'ID : " + id, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données de la matière", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierMatiere(int id, String nomMatiere, String descriptionMatiere) {
        try {
            String query = "UPDATE matiere SET nom_matiere = ?, description_matiere = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nomMatiere);
            pstmt.setString(2, descriptionMatiere);
            pstmt.setInt(3, id);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Matière mise à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la mise à jour de la matière.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de la matière", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerMatiere(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette matière ?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM matiere WHERE id = ?");
                pstmt.setInt(1, id);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Matière supprimée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Échec de la suppression de la matière.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la matière", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void afficherInfosMatiere(int id) {
        try {
            String query = "SELECT * FROM matiere WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JFrame frame = new JFrame("Informations de la matière");
                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

                panel.add(new JLabel("ID de la matière:"));
                panel.add(new JLabel(rs.getString("id")));
                panel.add(new JLabel("Nom de la matière:"));
                panel.add(new JLabel(rs.getString("nom_matiere")));
                panel.add(new JLabel("Description de la matière:"));
                panel.add(new JLabel(rs.getString("description_matiere")));

                JOptionPane.showMessageDialog(frame, panel, "Informations de la matière", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Aucune matière trouvée avec l'ID : " + id, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données de la matière", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(listerMatiere::new);
    }
}
