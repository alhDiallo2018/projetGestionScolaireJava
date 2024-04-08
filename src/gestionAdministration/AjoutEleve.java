package gestionAdministration;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class AjoutEleve extends JFrame {

    public JTextField txtnom, txtprenom, txtdate_naissance, txtlieu_naissance, txtsexe, txttelephone, 
                      txtmatricule, txtcni, txtrole_id, txtemail, txtmot_de_passe, txtphoto, txtancienEcole, txtstatutecole;
    public JLabel lablnom, lablprenom, labldate_naissance, labllieu_naissance, lablsexe, labltelephone, 
                  lablmatricule, lablcni, lablrole_id, lablemail, lablmot_de_passe, photoLabel, lblancienEcole, lblstatutecole;

    public AjoutEleve() {
        setTitle("Espace Ajout Eleve");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 300); // Ajustement de la taille de la fenêtre
        setLocationRelativeTo(null);
        //setLayout(null);

        lablnom = new JLabel("Nom");
        txtnom = new JTextField();

        lablprenom = new JLabel("Prénom");
        txtprenom = new JTextField();

        labldate_naissance = new JLabel("Date de Naissance");
        txtdate_naissance = new JTextField();

        labllieu_naissance = new JLabel("Lieu de Naissance");
        txtlieu_naissance = new JTextField();

        lablsexe = new JLabel("Sexe");
        txtsexe = new JTextField();

        labltelephone = new JLabel("Téléphone");
        txttelephone = new JTextField();

        lablmatricule = new JLabel("Matricule");
        txtmatricule = new JTextField();

        lablcni = new JLabel("CNI");
        txtcni = new JTextField();

        lablrole_id = new JLabel("Rôle ID");
        txtrole_id = new JTextField();

        lablemail = new JLabel("Email");
        txtemail = new JTextField();

        lablmot_de_passe = new JLabel("Mot de Passe");
        txtmot_de_passe = new JTextField();

        //this.setTitle("Test radio boutons");
   
       

        int x = 20, y = 20, width = 120, height = 25;
        int yGap = 30;

        lablnom.setBounds(x, y, width, height);
        txtnom.setBounds(x + 150, y, width, height);

        lablprenom.setBounds(x, y + yGap, width, height);
        txtprenom.setBounds(x + 150, y + yGap, width, height);

        labldate_naissance.setBounds(x, y + 2 * yGap, width, height);
        txtdate_naissance.setBounds(x + 150, y + 2 * yGap, width, height);

        labllieu_naissance.setBounds(x, y + 3 * yGap, width, height);
        txtlieu_naissance.setBounds(x + 150, y + 3 * yGap, width, height);

        lablsexe.setBounds(x, y + 4 * yGap, width, height);
        txtsexe.setBounds(x + 150, y + 4 * yGap, width, height);

        labltelephone.setBounds(x, y + 5 * yGap, width, height);
        txttelephone.setBounds(x + 150, y + 5 * yGap, width, height);

        lablmatricule.setBounds(x, y + 6 * yGap, width, height);
        txtmatricule.setBounds(x + 150, y + 6 * yGap, width, height);

        lablcni.setBounds(x, y + 7 * yGap, width, height);
        txtcni.setBounds(x + 150, y + 7 * yGap, width, height);

        lablrole_id.setBounds(x, y + 8 * yGap, width, height);
        txtrole_id.setBounds(x + 150, y + 8 * yGap, width, height);

        lablemail.setBounds(x, y + 9 * yGap, width, height);
        txtemail.setBounds(x + 150, y + 9 * yGap, width, height);

        lablmot_de_passe.setBounds(x, y + 10 * yGap, width, height);
        txtmot_de_passe.setBounds(x + 150, y + 10 * yGap, width, height);

        int photoX = 450, photoY = 20, photoWidth = 200, photoHeight = 200;
        photoLabel = new JLabel();
        photoLabel.setBounds(photoX, photoY, photoWidth, photoHeight);

        JButton choisirPhotoBtn = new JButton("Choisir une photo");
        
        choisirPhotoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // Obtenez le chemin absolu du fichier sélectionné
                    String absolutePath = selectedFile.getAbsolutePath();
                    // Mettez à jour le champ de texte ou l'affichage de votre choix
                    txtphoto.setText(absolutePath);
                }
            }
        });
        choisirPhotoBtn.setBounds(photoX, photoY + photoHeight + 10, 150, 25);

        JButton enregistrerBtn = new JButton("Enregistrer");
        enregistrerBtn.setBounds(x, y + 11 * yGap, 150, 25);

        // Ajustement de la taille de la fenêtre en fonction des composants
        int windowHeight = (y + 12 * yGap) + 40; // Ajoute une marge de 40 pixels en bas
        int windowWidth = photoX + photoWidth + 30; // Ajoute une marge de 30 pixels à droite

        // Définition de la taille de la fenêtre
        setSize(windowWidth, windowHeight);

        // Ajout des composants à la fenêtre
        add(lablnom);
        add(txtnom);
        add(lablprenom);
        add(txtprenom);
        add(labldate_naissance);
        add(txtdate_naissance);
        add(labllieu_naissance);
        add(txtlieu_naissance);
        add(lablsexe);
        add(txtsexe);
        add(labltelephone);
        add(txttelephone);
        add(lablmatricule);
        add(txtmatricule);
        add(lablcni);
        add(txtcni);
        add(lablrole_id);
        add(txtrole_id);
        add(lablemail);
        add(txtemail);
        add(lablmot_de_passe);
        add(txtmot_de_passe);
        add(photoLabel);
        add(choisirPhotoBtn);
        



        // Création du panneau pour les boutons radio
        JPanel panel = new JPanel(new GridLayout(0,1));
        TitledBorder border = BorderFactory.createTitledBorder("Sélection");
        panel.setBorder(border);
        lblancienEcole = new JLabel("Ancienne Ecole");
        txtancienEcole = new JTextField();
        lblancienEcole.setForeground(Color.BLUE);
        
        ButtonGroup groupEcole = new ButtonGroup();
        JRadioButton radio1 = new JRadioButton("Crêche", true);
        JRadioButton radio2 = new JRadioButton("Ecole");
        JRadioButton radio3 = new JRadioButton("aucune");

        panel.add(lblancienEcole);
        panel.add(txtancienEcole);
        groupEcole.add(radio1);
        panel.add(radio1);
        groupEcole.add(radio2);
        panel.add(radio2);
        groupEcole.add(radio3);
        panel.add(radio3);


        lblstatutecole = new JLabel("Statut Ecole");
        ButtonGroup groupStatut = new ButtonGroup();
        JRadioButton radio4 = new JRadioButton("Privée", true);
        JRadioButton radio5 = new JRadioButton("Public");
        lblstatutecole.setForeground(Color.BLUE);


        panel.add(lblstatutecole);
        groupStatut.add(radio4);
        panel.add(radio4);
        groupStatut.add(radio5);
        panel.add(radio5);
        
        

            // Ajout du panneau à la fenêtre principale
        add(panel, BorderLayout.SOUTH); // Ajoute le panneau en bas de la fenêtre principale
        //ıpanel.setBackground(Color.BLUE);
        panel.add(enregistrerBtn);
    

        setVisible(true);

        enregistrerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ajouterEleve = "INSERT INTO utilisateur (prenom, nom, date_naissance, lieu_naissance, sexe, photo, telephone, matricule, cni, role_id, email, mot_de_passe) VALUES ('" +
                    txtprenom.getText() + "', '" +
                    txtnom.getText() + "', '" +
                    txtdate_naissance.getText() + "', '" +
                    txtlieu_naissance.getText() + "', '" +
                    txtsexe.getText() + "', '" +
                    // Assuming txtphoto is the file path of the selected photo
                    txtphoto.getText() + "', '" +
                    txttelephone.getText() + "', '" +
                    txtmatricule.getText() + "', '" +
                    txtcni.getText() + "', '2', '" + 
                    txtemail.getText() + "', '" +
                    txtmot_de_passe.getText() + "')";
        
                // Insérer l'élève dans la table utilisateur
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/votre_base_de_donnees", "root", "");
                    Statement stmt = conn.createStatement();
                    int rowsAffected = stmt.executeUpdate(ajouterEleve);
        
                    if (rowsAffected > 0) {
                        System.out.println(rowsAffected + " lignes ont été insérées avec succès.");
                        // Or perform any other action based on the number of rows affected
                    } else {
                        System.out.println("Aucune ligne n'a été insérée.");
                        // Or handle the case where no rows were affected
                    }
        
                    // Récupérer l'ID de l'utilisateur inséré
                    String dernierIdUtilisateur = "SELECT LAST_INSERT_ID()";
                    int utilisateurId = 0;
        
                    ResultSet rs = stmt.executeQuery(dernierIdUtilisateur);
                    if (rs.next()) {
                        utilisateurId = rs.getInt(1);
                    }
        
                    // Récupérer le type d'école sélectionné
                    String typeEcoleSelectionne = "";
                    if (radio1.isSelected()) {
                        typeEcoleSelectionne = "Crêche";
                    } else if (radio2.isSelected()) {
                        typeEcoleSelectionne = "Ecole";
                    } else if (radio3.isSelected()) {
                        typeEcoleSelectionne = "aucune";
                    }
        
                    // Récupérer le statut de l'école sélectionné
                    String statutEcoleSelectionne = "";
                    if (radio4.isSelected()) {
                        statutEcoleSelectionne = "Privée";
                    } else if (radio5.isSelected()) {
                        statutEcoleSelectionne = "Public";
                    }
        
                    String ajoutAncienneEcole = "INSERT INTO ancienne_ecole (nom_ecole, type_ecole, statut_ecole, utilisateur_id) VALUES ('" +
                        txtancienEcole.getText() + "', '" +
                        typeEcoleSelectionne + "', '" +
                        statutEcoleSelectionne + "', " +
                        utilisateurId + ")"; 
        
                    int rowsAffected2 = stmt.executeUpdate(ajoutAncienneEcole);
        
                    if (rowsAffected2 > 0) {
                        System.out.println(rowsAffected2 + " lignes ont été insérées avec succès dans la table ancienne_ecole.");
                        // Or perform any other action based on the number of rows affected
                    } else {
                        System.out.println("Aucune ligne n'a été insérée dans la table ancienne_ecole.");
                        // Or handle the case where no rows were affected
                    }
        
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    
                }
            }
        });
        
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AjoutEleve::new);
    }
}
