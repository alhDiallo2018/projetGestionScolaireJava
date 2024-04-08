package gestionAdministration;

import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
import java.awt.*;

public class AffichageEleve extends JFrame {

    public AffichageEleve() {
        try {
            String[] column = {"ID", "PRENOMS", "NOMS", "DATE DE NAISSANCE", "LIEU DE NAISSANCE", "SEXE",
                    "TELEPHONE", "MATRICULE", "CNI", "EMAIL", "Modifier", "Supprimer"};

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionScolaire", "root", "");
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String afficherEleve = "SELECT * FROM utilisateur WHERE role_id = 2";
            ResultSet res = stmt.executeQuery(afficherEleve);

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(column);
            while (res.next()) {
                Object[] rowData = new Object[column.length];
                rowData[0] = res.getInt("id");
                rowData[1] = res.getString("prenom");
                rowData[2] = res.getString("nom");
                rowData[3] = res.getString("date_naissance");
                rowData[4] = res.getString("lieu_naissance");
                rowData[5] = res.getString("sexe");
                rowData[6] = res.getString("telephone"); // Mettre le téléphone à l'index 6
                rowData[7] = res.getString("matricule");
                rowData[8] = res.getString("cni");
                rowData[9] = res.getString("email");
                rowData[10] = "Modifier";
                rowData[11] = "Supprimer";
                model.addRow(rowData);
            }
            

            JTable table = new JTable(model);
            table.setShowGrid(true);
            table.setShowVerticalLines(true);
            table.getColumnModel().getColumn(column.length - 2).setCellRenderer(new ButtonRenderer("Modifier"));
            table.getColumnModel().getColumn(column.length - 2).setCellEditor(new ButtonEditor(new JCheckBox()));
            table.getColumnModel().getColumn(column.length - 1).setCellRenderer(new ButtonRenderer("Supprimer"));
            table.getColumnModel().getColumn(column.length - 1).setCellEditor(new ButtonEditor(new JCheckBox()));

            JScrollPane pane = new JScrollPane(table);
            add(pane);

            setSize(1000, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AffichageEleve::new);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            // Définir le texte du bouton
            setText((value == null) ? "" : value.toString());

            // Appliquer la couleur de fond en fonction de la sélection
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }

            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.addActionListener(e -> fireEditingStopped());
            button.setOpaque(true);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                JOptionPane.showMessageDialog(button, label + " clicked");
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
