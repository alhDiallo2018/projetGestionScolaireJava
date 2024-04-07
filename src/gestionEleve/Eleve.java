package gestionEleve;

import javax.swing.*;

public class Eleve extends JFrame {
    public JTextField txtnom;
    public JTextField txtprenom;
    public JTextField txtdate_naissance;
    public JTextField txtlieu_naissance;
    public JTextField txtsexe;
    public JTextField txttelephone;
    public JTextField txtmatricule;
    public JTextField txtcni;
    public JTextField txtrole_id;
    public JTextField txtemail;
    public JTextField txtmot_de_passe;
    public JLabel lablnom, lablprenom;
    public JLabel labldate_naissance, labllieu_naissance;
    public JLabel lablsexe;
    public JLabel labltelephone;
    public JLabel lablmatricule;
    public JLabel lablcni;
    public JLabel lablrole_id;
    public JLabel lablemail;
    public JLabel lablmot_de_passe;

    public Eleve(){
        setTitle("Espace Eleve");
        setSize(300, 500);
        setLocationRelativeTo(null);
        setLayout(null);

        txtnom = new JTextField();
        txtprenom = new JTextField();
        lablnom = new JLabel();
        lablprenom = new JLabel();

        lablnom.setBounds(20, 120, 200, 25);
        lablprenom.setBounds(20, 180, 200, 25);

        add(lablnom);
        add(lablprenom);

        setVisible(true); // Ce serait mieux d'appeler cette méthode lorsque vous êtes prêt à afficher votre fenêtre
    }
}

