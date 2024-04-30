package gestionSalle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class listerSalle extends JFrame {
    private static final String[] COLUMN_NAMES = {"ID", "Numéro de salle", "Capacité", "Modifier", "Supprimer"};
    private static final String SELECT_QUERY = "SELECT id, numero_salle, capacite FROM salle";

    private Connection conn;

    public listerSalle() {
        super("Gestion des salles");
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
                    return column >= 3; // Seules les colonnes "Modifier" et "Supprimer" sont éditables
                }
            };
            model.setColumnIdentifiers(COLUMN_NAMES);

            while (rs.next()) {
                Object[] rowData = new Object[COLUMN_NAMES.length];
                for (int i = 0; i < COLUMN_NAMES.length - 2; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                rowData[COLUMN_NAMES.length - 2] = "Modifier";
                rowData[COLUMN_NAMES.length - 1] = "Supprimer";
                model.addRow(rowData);
            }

            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            int[] columnWidths = {50, 150, 100, 80, 80};
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
                                supprimerSalle(id);
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
            String query = "SELECT * FROM salle WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JFrame frame = new JFrame("Modifier la salle");
                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

                panel.add(new JLabel("Numéro de salle:"));
                JTextField numeroSalleField = new JTextField(rs.getString("numero_salle"));
                panel.add(numeroSalleField);

                panel.add(new JLabel("Capacité:"));
                JTextField capaciteField = new JTextField(rs.getString("capacite"));
                panel.add(capaciteField);

                int result = JOptionPane.showConfirmDialog(frame, panel, "Modifier la salle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    modifierSalle(id, numeroSalleField.getText(), capaciteField.getText());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucune salle trouvée avec l'ID : " + id, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données de la salle", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierSalle(int id, String numeroSalle, String capacite) {
        try {
            String query = "UPDATE salle SET numero_salle = ?, capacite = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, numeroSalle);
            pstmt.setString(2, capacite);
            pstmt.setInt(3, id);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Salle mise à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la mise à jour de la salle.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de la salle", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerSalle(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette salle ?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM salle WHERE id = ?");
                pstmt.setInt(1, id);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Salle supprimée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Échec de la suppression de la salle.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la salle", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(listerSalle::new);
    }
}
