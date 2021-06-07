//Parempoolne väli
import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class Infoboard extends JPanel {

    private final JPanel pnlComponents = new JPanel(new GridBagLayout()); //Siia labelid, combobox, radiobtn
    private final GridBagConstraints gbc = new GridBagConstraints(); //Millised read või veerud on kokku tõmmatud

    private final Font fontBold = new Font("Verdana", Font.BOLD,14); //Rasvane 14 verdana
    private final Font fontNormal = new Font("Verdana", Font.PLAIN,14); //Tavaline 14 verdana

    private final String[] boardsizes = {"10","11","12","13","14","15"}; //Lauasuurused comboboxis

    //Labelid
    private JLabel lblMouseXY;
    private JLabel lblID;
    private JLabel lblRowCol; //Milline lahter, milline veerg
    private JLabel lblTime; //Mänguaeg
    private JLabel lblShip; //Palju laevu leitud, palju laevu üldse
    private JLabel lblGameboard; //Näitab mängulaua suurust

    //Combobox
    private JComboBox<String> cmbSize;

    //Nupud
    private JButton btnNewGame;
    private JButton btnScoreboard; //Edetabeli nupp

    private final JLabel lblScoreboard = new JLabel("Edetabeli varaiant");

    //Raadionupud peavad käima gruppi, et ainult üks oleks korraga valitud
    private final JRadioButton rdoTable = new JRadioButton("Lihtne tabel");
    private final JRadioButton rdoTableModel = new JRadioButton("Mudel tabel");
    private final JRadioButton rdoDatabase = new JRadioButton("Andmebaas");

    private final ButtonGroup btnGrp = new ButtonGroup(); //Raadionuppude gruppi panekuks

    public Infoboard() {
        setBackground(new Color(0,255,190)); //
        setPreferredSize(new Dimension(380, 188)); //Paneeli suurus
        setLayout(new FlowLayout(FlowLayout.LEFT));

        pnlComponents.setBackground(new Color(190,255,0));

        gbc.anchor=GridBagConstraints.WEST; //Lahtris tekst vasakul
        gbc.insets = new Insets(2,2,2,2); //Vahed

        setupLabels();
        setupCombobox();
        setupButtons();
        setupRadiobuttons();
        add(pnlComponents);


    }
    private void setupLabels() {
        //Esimene rida
        JLabel lblMouseText= new JLabel("Hiir");
        lblMouseText.setFont(fontBold);
        gbc.gridx=0; //Veerg
        gbc.gridy=0; //Rida
        pnlComponents.add(lblMouseText,gbc);

        lblMouseXY=new JLabel("X: 0 and Y: 0");
        lblMouseXY.setFont(fontNormal);
        gbc.gridx=1; //Veerg
        gbc.gridy=0; //Rida
        pnlComponents.add(lblMouseXY, gbc);

        //Teine rida
        JLabel lblIdText = new JLabel("ID");
        lblIdText.setFont(fontBold);
        gbc.gridx=0; //Veerg
        gbc.gridy=1; //Rida
        pnlComponents.add(lblIdText, gbc);

        lblID = new JLabel("Teadmata1");
        lblID.setFont(fontNormal);
        gbc.gridx=1; //Veerg
        gbc.gridy=1; //Rida
        pnlComponents.add(lblID, gbc);

        //Kolmas rida
        JLabel lblRowColText = new JLabel("Rida : Veerg");
        lblRowColText.setFont(fontBold);
        gbc.gridx=0; //Veerg
        gbc.gridy=2; //Rida
        pnlComponents.add(lblRowColText, gbc);

        lblRowCol=new JLabel("1 : 1");
        lblRowCol.setFont(fontNormal);
        gbc.gridx=1; //Veerg
        gbc.gridy=2; //Rida
        pnlComponents.add(lblRowCol, gbc);

        //Neljas rida
        JLabel lblTimeText = new JLabel("Mängu aeg");
        lblTimeText.setFont(fontBold);
        gbc.gridx=0; //Veerg
        gbc.gridy=3; //Rida
        pnlComponents.add(lblTimeText, gbc);

        lblTime = new JLabel("00:00");
        lblTime.setFont(fontNormal);
        gbc.gridx=1; //Veerg
        gbc.gridy=3; //Rida
        pnlComponents.add(lblTime, gbc);

        //Viies rida
        JLabel lblShipText = new JLabel("Laevad");
        lblShipText.setFont(fontBold);
        gbc.gridx=0; //Veerg
        gbc.gridy=4; //Rida
        pnlComponents.add(lblShipText, gbc);

        lblShip=new JLabel("0 / 0");
        lblShip.setFont(fontNormal);
        gbc.gridx=1; //Veerg
        gbc.gridy=4; //Rida
        pnlComponents.add(lblShip, gbc);

        //Kuues rida
        JLabel lblGameboardText=new JLabel("Valitud laua suurus");
        lblGameboardText.setFont(fontBold);
        gbc.gridx=0; //Veerg
        gbc.gridy=5; //Rida
        pnlComponents.add(lblGameboardText, gbc);

        lblGameboard=new JLabel("10 x 10");
        lblGameboard.setFont(fontNormal);
        gbc.gridx=1; //Veerg
        gbc.gridy=5; //Rida
        pnlComponents.add(lblGameboard, gbc);

    }

    private void setupCombobox() {
        // Seitsmes rida
        JLabel lblCmbSizeText = new JLabel("Vali laua suurus");
        lblCmbSizeText.setFont(fontBold);
        gbc.gridx=0; //Veerg
        gbc.gridy=6; //Rida
        pnlComponents.add(lblCmbSizeText, gbc);

        cmbSize = new JComboBox<>(boardsizes);
        cmbSize.setFont((fontNormal));
        cmbSize.setPreferredSize(new Dimension(106,28)); //Comboboxi laius
        gbc.gridx=1; //Veerg
        gbc.gridy=6; //Rida
        pnlComponents.add(cmbSize, gbc);
    }

    private void setupButtons() {
        //Kaheksas rida
        JLabel lblNewgameText = new JLabel("Uus mäng?");
        lblNewgameText.setFont(fontBold);
        gbc.gridx=0; //Veerg
        gbc.gridy=7; //Rida
        pnlComponents.add(lblNewgameText, gbc);

        btnNewGame = new JButton("Uus mäng");
        btnNewGame.setFont(fontNormal);
        btnNewGame.setPreferredSize(new Dimension(106,28));
        gbc.gridx=1; //Veerg
        gbc.gridy=7; //Rida
        pnlComponents.add(btnNewGame, gbc);

        //Üheksas rida
        btnScoreboard = new JButton("Edetabel");
        btnScoreboard.setFont(fontNormal);
        btnScoreboard.setPreferredSize(new Dimension(106,28));
        gbc.gridx=1; //Veerg
        gbc.gridy=8; //Rida
        pnlComponents.add(btnScoreboard, gbc);
    }

    private void setupRadiobuttons() {
        //Kümnes rida
        lblScoreboard.setFont(fontBold);
        gbc.gridwidth = 3; //Üle kolme rea kõrge
        gbc.gridx=0; //Veerg
        gbc.gridy=9; //Rida
        pnlComponents.add(lblScoreboard, gbc);

        //Raadionupud ülevalt alla

            //1.raadionupp
        rdoTable.setFont(fontNormal);
        rdoTable.setSelected(true); //Vaikimisi nupp valitud
        //Taust kui vaja
        gbc.gridx=1; //Veerg
        gbc.gridy=9; //Rida
        pnlComponents.add(rdoTable, gbc);

            //2.raadionupp
        rdoTableModel.setFont(fontNormal);
        //Taust kui vaja
        gbc.gridx=1; //Veerg
        gbc.gridy=10; //Rida
        pnlComponents.add(rdoTableModel, gbc);

            //3.raadionupp
        rdoDatabase.setFont(fontNormal);
        //Taust kui vaja
        gbc.gridx=1; //Veerg
        gbc.gridy=11; //Rida
        pnlComponents.add(rdoDatabase, gbc);

        //Lisame raadionupud ühte gruppi. Tagab, et ainult üks nupp samaaegselt valitud
        btnGrp.add(rdoTable);
        btnGrp.add(rdoTableModel);
        btnGrp.add(rdoDatabase);
    }

    //Labelite getterid
    public JLabel getLblMouseXY() {
        return lblMouseXY;
    }

    public JLabel getLblID() {
        return lblID;
    }

    public JLabel getLblRowCol() {
        return lblRowCol;
    }

    public JLabel getLblTime() {
        return lblTime;
    }

    public JLabel getLblShip() {
        return lblShip;
    }

    public JLabel getLblGameboard() {
        return lblGameboard;
    }

    public JComboBox<String> getCmbSize() {
        return cmbSize;
    }

    public JButton getBtnNewGame() {
        return btnNewGame;
    }

    public JButton getBtnScoreboard() {
        return btnScoreboard;
    }

    public JRadioButton getRdoTable() {
        return rdoTable;
    }

    public JRadioButton getRdoTableModel() {
        return rdoTableModel;
    }

    public JRadioButton getRdoDatabase() {
        return rdoDatabase;
    }
}
