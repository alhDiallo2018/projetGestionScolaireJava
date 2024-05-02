package gestionAdministration;

/*import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AjoutEleve extends JFrame {
    private JTextField textFieldPrenom;
    private JTextField textFieldNom;
    private JTextField textFieldDateNaissance;
    private JTextField textFieldLieuNaissance;
    private JComboBox<String> comboBoxSexe;
    private JTextField textFieldTelephone;
    private JTextField textFieldMatricule;
    private JTextField textFieldCNI;
    private JComboBox<String> comboBoxClasse;
    private JComboBox<String> comboBoxNiveau;
    private JTextField textFieldNomEcole;
    private JTextField textFieldTypeEcole;
    private JTextField textFieldStatutEcole;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JTextField textFieldFrais;
    private JComboBox<String> comboBoxTypeFrais;
    private Connection connexion;
    private JLabel labelPhoto;
    private String cheminPhoto = "";

    public AjoutEleve() {
        setTitle("Ajouter un élève");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        JPanel inputPanel = new JPanel(new GridLayout(19, 2)); // Augmentation du nombre de lignes
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // JPanel pour la photo et le bouton "Ajouter une photo"
        JPanel photoPanel = new JPanel(new BorderLayout());
        mainPanel.add(photoPanel, BorderLayout.EAST);

        JPanel photoButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoPanel.add(photoButtonPanel, BorderLayout.NORTH);

        // JLabel pour afficher la photo
        labelPhoto = new JLabel("Aucune photo sélectionnée", SwingConstants.CENTER);
        photoPanel.add(labelPhoto, BorderLayout.CENTER);

        // Bouton pour ajouter une photo
        JButton boutonAjouterPhoto = new JButton("Ajouter photo");
        photoButtonPanel.add(boutonAjouterPhoto);

        boutonAjouterPhoto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    cheminPhoto = selectedFile.getAbsolutePath();
                    JOptionPane.showMessageDialog(AjoutEleve.this, "Photo sélectionnée : " + cheminPhoto);

                    // Afficher la photo sélectionnée
                    afficherPhoto();
                }
            }
        });

        // Ajout des composants pour les champs de saisie

        JLabel labelPrenom = new JLabel("Prénom:");
        inputPanel.add(labelPrenom);

        textFieldPrenom = new JTextField();
        inputPanel.add(textFieldPrenom);

        JLabel labelNom = new JLabel("Nom:");
        inputPanel.add(labelNom);

        textFieldNom = new JTextField();
        inputPanel.add(textFieldNom);

        JLabel labelDateNaissance = new JLabel("Date de naissance (AAAA-MM-JJ):");
        inputPanel.add(labelDateNaissance);

        textFieldDateNaissance = new JTextField();
        inputPanel.add(textFieldDateNaissance);

        JLabel labelLieuNaissance = new JLabel("Lieu de naissance:");
        inputPanel.add(labelLieuNaissance);

        textFieldLieuNaissance = new JTextField();
        inputPanel.add(textFieldLieuNaissance);

        JLabel labelSexe = new JLabel("Sexe:");
        inputPanel.add(labelSexe);

        comboBoxSexe = new JComboBox<>();
        comboBoxSexe.addItem("Masculin");
        comboBoxSexe.addItem("Féminin");
        inputPanel.add(comboBoxSexe);

        JLabel labelTelephone = new JLabel("Téléphone:");
        inputPanel.add(labelTelephone);

        textFieldTelephone = new JTextField();
        inputPanel.add(textFieldTelephone);

        JLabel labelMatricule = new JLabel("Matricule:");
        inputPanel.add(labelMatricule);

        textFieldMatricule = new JTextField();
        inputPanel.add(textFieldMatricule);

        JLabel labelCNI = new JLabel("CNI:");
        inputPanel.add(labelCNI);

        textFieldCNI = new JTextField();
        inputPanel.add(textFieldCNI);

        JLabel labelClasse = new JLabel("Classe:");
        inputPanel.add(labelClasse);

        comboBoxClasse = new JComboBox<>();
        inputPanel.add(comboBoxClasse);

        JLabel labelNiveau = new JLabel("Niveau:");
        inputPanel.add(labelNiveau);

        comboBoxNiveau = new JComboBox<>();
        inputPanel.add(comboBoxNiveau);

        JLabel labelNomEcole = new JLabel("Nom de l'école:");
        inputPanel.add(labelNomEcole);

        textFieldNomEcole = new JTextField();
        inputPanel.add(textFieldNomEcole);

        JLabel labelTypeEcole = new JLabel("Type de l'école:");
        inputPanel.add(labelTypeEcole);

        textFieldTypeEcole = new JTextField();
        inputPanel.add(textFieldTypeEcole);

        JLabel labelStatutEcole = new JLabel("Statut de l'école:");
        inputPanel.add(labelStatutEcole);

        textFieldStatutEcole = new JTextField();
        inputPanel.add(textFieldStatutEcole);

        JLabel labelEmail = new JLabel("Email:");
        inputPanel.add(labelEmail);

        textFieldEmail = new JTextField();
        inputPanel.add(textFieldEmail);

        JLabel labelMotDePasse = new JLabel("Mot de passe:");
        inputPanel.add(labelMotDePasse);

        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        JLabel labelFrais = new JLabel("Frais:");
        inputPanel.add(labelFrais);

        textFieldFrais = new JTextField();
        inputPanel.add(textFieldFrais);

        // Ajout des JComboBox pour les types de frais
        JLabel labelTypeFrais = new JLabel("Type de frais:");
        inputPanel.add(labelTypeFrais);

        comboBoxTypeFrais = new JComboBox<>();
        inputPanel.add(comboBoxTypeFrais);

        // Connexion à la base de données et remplissage des listes
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/gestionScolaire";
            String utilisateur = "root";
            String motDePasse = "";

            connexion = DriverManager.getConnection(url, utilisateur, motDePasse);

            remplirListeClasses();
            remplirListeNiveaux();
            remplirListeTypesFrais();

            JButton boutonAjouter = new JButton("Ajouter");
            buttonPanel.add(boutonAjouter);

            boutonAjouter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Récupération des valeurs des champs de saisie
                    String prenom = textFieldPrenom.getText();
                    String nom = textFieldNom.getText();
                    String dateNaissanceString = textFieldDateNaissance.getText();
                    String lieuNaissance = textFieldLieuNaissance.getText();
                    String sexe = (String) comboBoxSexe.getSelectedItem();
                    String telephone = textFieldTelephone.getText();
                    String matricule = textFieldMatricule.getText();
                    String cni = textFieldCNI.getText();
                    String classeSelectionnee = (String) comboBoxClasse.getSelectedItem();
                    String niveauSelectionne = (String) comboBoxNiveau.getSelectedItem();
                    String typeFraisSelectionne = (String) comboBoxTypeFrais.getSelectedItem();
                    double frais = Double.parseDouble(textFieldFrais.getText());
                    String nomEcole = textFieldNomEcole.getText();
                    String typeEcole = textFieldTypeEcole.getText();
                    String statutEcole = textFieldStatutEcole.getText();
                    String email = textFieldEmail.getText();
                    String motDePasse = new String(passwordField.getPassword());

                    // Conversion de la date de naissance en objet Date
                    java.util.Date dateNaissanceUtil = null;
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dateNaissanceUtil = dateFormat.parse(dateNaissanceString);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(AjoutEleve.this, "Format de date incorrect ! Utilisez yyyy-MM-dd");
                        return;
                    }
                    java.sql.Date dateNaissanceSQL = new java.sql.Date(dateNaissanceUtil.getTime());

                    // Récupération des ID correspondants
                    int idClasse = getIdClasse(classeSelectionnee);
                    int idNiveau = getIdNiveau(niveauSelectionne);
                    int idTypeFrais = getIdTypeFrais(typeFraisSelectionne);
                    int idEcole = ajouterAncienneEcole(nomEcole, typeEcole, statutEcole);

                    // Appel de la méthode pour ajouter l'élève
                    ajouterEleve(prenom, nom, dateNaissanceSQL, lieuNaissance, sexe, cheminPhoto, telephone, matricule, cni, idClasse, idNiveau, idEcole, email, motDePasse, frais);

                }
            });

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }

        setVisible(true);
    }

    // Méthode pour remplir la liste des types de frais à partir de la base de données
    private void remplirListeTypesFrais() {
        String sql = "SELECT type_frais FROM frais";
        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String typeFrais = resultSet.getString("type_frais");
                comboBoxTypeFrais.addItem(typeFrais);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }


    // Méthode pour afficher la photo sélectionnée
    private void afficherPhoto() {
        ImageIcon imageIcon = new ImageIcon(cheminPhoto);
        // Redimensionner l'image pour s'adapter au JLabel
        Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);
        labelPhoto.setIcon(imageIcon);
    }

    private void remplirListeClasses() {
        String sql = "SELECT surnom_classe FROM classe";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String nomClasse = resultSet.getString("surnom_classe");
                comboBoxClasse.addItem(nomClasse);
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
                comboBoxNiveau.addItem(nomNiveau);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
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

    private int getIdTypeFrais(String typeFrais) {
        String sql = "SELECT id FROM frais WHERE type_frais = ?";
        int idTypeFrais = -1;

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, typeFrais);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idTypeFrais = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        return idTypeFrais;
    }


    private int ajouterAncienneEcole(String nomEcole, String typeEcole, String statutEcole) {
        String sql = "INSERT INTO ancienne_ecole (nom_ecole, type_ecole, statut_ecole) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, nomEcole);
            preparedStatement.setString(2, typeEcole);
            preparedStatement.setString(3, statutEcole);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        return -1;
    }

    private void ajouterEleve(String prenom, String nom, Date dateNaissance, String lieuNaissance, String sexe, String photo, String telephone, String matricule, String cni, int idClasse, int idNiveau, int idEcole, String email, String motDePasse, double frais) {
        String sql = "INSERT INTO eleve (prenom, nom, date_naissance, lieu_naissance, sexe, photo, telephone, matricule, cni, classe_id, niveau_id, id_ancienne_ecole, email, mot_de_passe, frais) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, prenom);
            preparedStatement.setString(2, nom);
            preparedStatement.setObject(3, new java.sql.Date(dateNaissance.getTime()));
            preparedStatement.setString(4, lieuNaissance);
            preparedStatement.setString(5, sexe);
            preparedStatement.setString(6, photo);
            preparedStatement.setString(7, telephone);
            preparedStatement.setString(8, matricule);
            preparedStatement.setString(9, cni);
            preparedStatement.setInt(10, idClasse);
            preparedStatement.setInt(11, idNiveau);
            preparedStatement.setInt(12, idEcole);
            preparedStatement.setString(13, email);
            preparedStatement.setString(14, motDePasse);
            preparedStatement.setDouble(15, frais);

            int lignesAffectees = preparedStatement.executeUpdate();

            if (lignesAffectees > 0) {
                JOptionPane.showMessageDialog(this, "L'élève a été ajouté avec succès !");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : L'élève n'a pas pu être ajouté.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AjoutEleve::new);
    }
}
*/






import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AjoutEleve extends JFrame {
    private JTextField textFieldPrenom;
    private JTextField textFieldNom;
    private JTextField textFieldDateNaissance;
    private JTextField textFieldLieuNaissance;
    private JComboBox<String> comboBoxSexe;
    private JTextField textFieldTelephone;
    private JTextField textFieldMatricule;
    private JTextField textFieldCNI;
    private JComboBox<String> comboBoxClasse;
    private JComboBox<String> comboBoxNiveau;
    private JTextField textFieldNomEcole;
    private JTextField textFieldTypeEcole;
    private JTextField textFieldStatutEcole;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
   // private JTextField textFieldFrais;
    private JComboBox<String> comboBoxTypeFrais;
    private Connection connexion;
    private JLabel labelPhoto;
    private String cheminPhoto = "";

    public AjoutEleve() {
        setTitle("Ajouter un élève");
        setSize(800, 400); // Augmenter la largeur pour le formulaire
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(220); // Réglez la proportion du split
        getContentPane().add(splitPane, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout());
        splitPane.setLeftComponent(leftPanel);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        leftPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // JPanel pour la photo
        JPanel rightPanel = new JPanel(new BorderLayout());
        splitPane.setRightComponent(rightPanel);

        // JPanel pour la photo et le bouton "Ajouter une photo"
        JPanel photoPanel = new JPanel(new BorderLayout());
        rightPanel.add(photoPanel, BorderLayout.CENTER);

        // JLabel pour afficher la photo
        labelPhoto = new JLabel("Aucune photo sélectionnée", SwingConstants.CENTER);
        photoPanel.add(labelPhoto, BorderLayout.WEST);

        // Bouton pour ajouter une photo
        JButton boutonAjouterPhoto = new JButton("Ajouter photo");
        photoPanel.add(boutonAjouterPhoto, BorderLayout.EAST);
        boutonAjouterPhoto.setBackground(Color.BLUE);

        boutonAjouterPhoto.setPreferredSize(new Dimension(102, 30)); // Définir une taille spécifique au bouton

        boutonAjouterPhoto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    cheminPhoto = selectedFile.getAbsolutePath();
                    JOptionPane.showMessageDialog(AjoutEleve.this, "Photo sélectionnée : " + cheminPhoto);

                    // Afficher la photo sélectionnée
                    afficherPhoto();
                }
            }
        });

        // Ajout des composants pour les champs de saisie

        JLabel labelPrenom = new JLabel("Prénom:");
        inputPanel.add(labelPrenom);

        textFieldPrenom = new JTextField();
        inputPanel.add(textFieldPrenom);

        JLabel labelNom = new JLabel("Nom:");
        inputPanel.add(labelNom);

        textFieldNom = new JTextField();
        inputPanel.add(textFieldNom);

        JLabel labelDateNaissance = new JLabel("Date de naissance (AAAA-MM-JJ):");
        inputPanel.add(labelDateNaissance);

        textFieldDateNaissance = new JTextField();
        inputPanel.add(textFieldDateNaissance);

        JLabel labelLieuNaissance = new JLabel("Lieu de naissance:");
        inputPanel.add(labelLieuNaissance);

        textFieldLieuNaissance = new JTextField();
        inputPanel.add(textFieldLieuNaissance);

        JLabel labelSexe = new JLabel("Sexe:");
        inputPanel.add(labelSexe);

        comboBoxSexe = new JComboBox<>();
        comboBoxSexe.addItem("Masculin");
        comboBoxSexe.addItem("Féminin");
        inputPanel.add(comboBoxSexe);

        JLabel labelTelephone = new JLabel("Téléphone:");
        inputPanel.add(labelTelephone);

        textFieldTelephone = new JTextField();
        inputPanel.add(textFieldTelephone);

        JLabel labelMatricule = new JLabel("Matricule:");
        inputPanel.add(labelMatricule);

        textFieldMatricule = new JTextField();
        inputPanel.add(textFieldMatricule);

        JLabel labelCNI = new JLabel("CNI:");
        inputPanel.add(labelCNI);

        textFieldCNI = new JTextField();
        inputPanel.add(textFieldCNI);

        JLabel labelClasse = new JLabel("Classe:");
        inputPanel.add(labelClasse);

        comboBoxClasse = new JComboBox<>();
        inputPanel.add(comboBoxClasse);

        JLabel labelNiveau = new JLabel("Niveau:");
        inputPanel.add(labelNiveau);

        comboBoxNiveau = new JComboBox<>();
        inputPanel.add(comboBoxNiveau);

        JLabel labelNomEcole = new JLabel("Nom de l'école:");
        inputPanel.add(labelNomEcole);

        textFieldNomEcole = new JTextField();
        inputPanel.add(textFieldNomEcole);

        JLabel labelTypeEcole = new JLabel("Type de l'école:");
        inputPanel.add(labelTypeEcole);

        textFieldTypeEcole = new JTextField();
        inputPanel.add(textFieldTypeEcole);

        JLabel labelStatutEcole = new JLabel("Statut de l'école:");
        inputPanel.add(labelStatutEcole);

        textFieldStatutEcole = new JTextField();
        inputPanel.add(textFieldStatutEcole);

        JLabel labelEmail = new JLabel("Email:");
        inputPanel.add(labelEmail);

        textFieldEmail = new JTextField();
        inputPanel.add(textFieldEmail);

        JLabel labelMotDePasse = new JLabel("Mot de passe:");
        inputPanel.add(labelMotDePasse);

        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        /*JLabel labelFrais = new JLabel("Frais:");
        inputPanel.add(labelFrais);

        textFieldFrais = new JTextField();
        inputPanel.add(textFieldFrais);*/

        // Ajout des JComboBox pour les types de frais
        JLabel labelTypeFrais = new JLabel("Type de frais:");
        inputPanel.add(labelTypeFrais);

        comboBoxTypeFrais = new JComboBox<>();
        inputPanel.add(comboBoxTypeFrais);

        // Connexion à la base de données et remplissage des listes
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/gestionScolaire";
            String utilisateur = "root";
            String motDePasse = "";

            connexion = DriverManager.getConnection(url, utilisateur, motDePasse);

            remplirListeClasses();
            remplirListeNiveaux();
            remplirListeTypesFrais();

            JButton boutonAjouter = new JButton("Ajouter");
            buttonPanel.add(boutonAjouter);

            boutonAjouter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Récupération des valeurs des champs de saisie
                    String prenom = textFieldPrenom.getText();
                    String nom = textFieldNom.getText();
                    String dateNaissanceString = textFieldDateNaissance.getText();
                    String lieuNaissance = textFieldLieuNaissance.getText();
                    String sexe = (String) comboBoxSexe.getSelectedItem();
                    String telephone = textFieldTelephone.getText();
                    String matricule = textFieldMatricule.getText();
                    String cni = textFieldCNI.getText();
                    String classeSelectionnee = (String) comboBoxClasse.getSelectedItem();
                    String niveauSelectionne = (String) comboBoxNiveau.getSelectedItem();
                    String nomEcole = textFieldNomEcole.getText();
                    String typeEcole = textFieldTypeEcole.getText();
                    String statutEcole = textFieldStatutEcole.getText();
                    String typeFraisSelectionne = (String) comboBoxTypeFrais.getSelectedItem();
                    //double frais = Double.parseDouble(textFieldFrais.getText());

                    // Conversion de la date de naissance en objet Date
                    java.util.Date dateNaissanceUtil = null;
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dateNaissanceUtil = dateFormat.parse(dateNaissanceString);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(AjoutEleve.this, "Format de date incorrect ! Utilisez yyyy-MM-dd");
                        return;
                    }
                    java.sql.Date dateNaissanceSQL = new java.sql.Date(dateNaissanceUtil.getTime());

                    // Récupération des ID correspondants
                    int idClasse = getIdClasse(classeSelectionnee);
                    int idNiveau = getIdNiveau(niveauSelectionne);
                    int idTypeFrais = getIdTypeFrais(typeFraisSelectionne);

                    // Appel de la méthode pour ajouter l'élève
                    ajouterEleve(prenom, nom, dateNaissanceSQL, lieuNaissance, sexe, cheminPhoto, telephone, matricule, cni, idClasse, idNiveau, nomEcole, typeEcole, statutEcole, textFieldEmail.getText(), new String(passwordField.getPassword()), idTypeFrais);

                }
            });

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }

        setVisible(true);
    }

    // Méthode pour remplir la liste des types de frais à partir de la base de données
    private void remplirListeTypesFrais() {
        String sql = "SELECT type_frais FROM frais";
        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String typeFrais = resultSet.getString("type_frais");
                comboBoxTypeFrais.addItem(typeFrais);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    // Méthode pour afficher la photo sélectionnée
    private void afficherPhoto() {
        ImageIcon imageIcon = new ImageIcon(cheminPhoto);
        // Redimensionner l'image pour s'adapter au JLabel
        Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);
        labelPhoto.setIcon(imageIcon);
    }

    private void remplirListeClasses() {
        String sql = "SELECT surnom_classe FROM classe";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String nomClasse = resultSet.getString("surnom_classe");
                comboBoxClasse.addItem(nomClasse);
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
                comboBoxNiveau.addItem(nomNiveau);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
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

    private int getIdTypeFrais(String typeFrais) {
        String sql = "SELECT id FROM frais WHERE type_frais = ?";
        int idTypeFrais = -1;

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, typeFrais);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idTypeFrais = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        return idTypeFrais;
    }

    private int ajouterAncienneEcole(String nomEcole, String typeEcole, String statutEcole) {
        String sql = "INSERT INTO ancienne_ecole (nom_ecole, type_ecole, statut_ecole) VALUES (?, ?, ?)";
        int idEcole = -1;

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, nomEcole);
            preparedStatement.setString(2, typeEcole);
            preparedStatement.setString(3, statutEcole);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                idEcole = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        return idEcole;
    }

    private void ajouterEleve(String prenom, String nom, Date dateNaissance, String lieuNaissance, String sexe, String photo, String telephone, String matricule, String cni, int idClasse, int idNiveau, String nomEcole, String typeEcole, String statutEcole, String email, String motDePasse, int idFrais) {
        int role_id = 2;
        String sql = "INSERT INTO utilisateur (prenom, nom, date_naissance, lieu_naissance, sexe, photo, telephone, matricule, cni, classe_id, niveau_id, anEcole_id, email, mot_de_passe, frais_id, role_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, prenom);
            preparedStatement.setString(2, nom);
            preparedStatement.setObject(3, new java.sql.Date(dateNaissance.getTime()));
            preparedStatement.setString(4, lieuNaissance);
            preparedStatement.setString(5, sexe);
            preparedStatement.setString(6, photo);
            preparedStatement.setString(7, telephone);
            preparedStatement.setString(8, matricule);
            preparedStatement.setString(9, cni);
            preparedStatement.setInt(10, idClasse);
            preparedStatement.setInt(11, idNiveau);
            preparedStatement.setInt(12, ajouterAncienneEcole(nomEcole, typeEcole, statutEcole));
            preparedStatement.setString(13, email);
            preparedStatement.setString(14, motDePasse);
            preparedStatement.setDouble(15, idFrais);
            preparedStatement.setInt(16, role_id);

            int lignesAffectees = preparedStatement.executeUpdate();

            if (lignesAffectees > 0) {
                JOptionPane.showMessageDialog(this, "L'élève a été ajouté avec succès !");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : L'élève n'a pas pu être ajouté.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AjoutEleve::new);
    }
}



