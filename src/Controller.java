import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
//import java.util.Stack;

public class Controller implements MouseListener, MouseMotionListener {
    private final Model model;
    private final View view;
    private final GameTimer gametimer;

    //Andmebaasi
    private final String dbname = "scores.db";
    String[] columnNames = new String[]{"Nimi", "Aeg", "Klikke", "Laua suurus", "Mängitud"}; //Edetabel
    private JDialog scoreboarddialog;
    private boolean doRdoTbl = true;
    private boolean doRdoTblMdl = false;
    private boolean doRdoDb = false;
    private JTable table;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        this.gametimer = new GameTimer(view);

        view.registerCombobox(new MyComboboxListener());
        view.registerNewgame(new MyNewgameListener());
        view.registerScoreButton(new MyScoreButton());
        view.registerRadiobuttons(new MyRadiobuttons()); //Raadionuppude kontrollimine
    }

    //Comboboxi valiku muutus
    private class MyComboboxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            //System.out.println(e.getItem()); //Näitab Comboboxi valiku muutust konsooli
            if(e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println(e.getItem()); //Testiks
                String number = e.getItem().toString(); // e.getitemi teeb stringiks. Näiteks suurus 15
                int size = Integer.parseInt(number);
                view.getLblGameboard().setText(number + " X " + number); //Muudab labeli teksti
                model.setBoardsize(size); //Muudab mängulaua mõõtmed
                view.pack(); //Et suurus muutuks
                view.repaint();//Joonistab laual olevad asjad uuesti
            }
        }
    }

    private class MyNewgameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!gametimer.isRunning()) { //Aeg ei käi, mäng ei käi
                model.setupNewgame(); //Lauasuurus mängus
                model.getGame().setupNewGame(); //Paneb laevad reaalselt maatriksisse
                gametimer.startTimer();
                gametimer.setRunning(true);
                view.getBtnNewGame().setText("Lõpeta");
                view.getCmbSize().setEnabled(false); //Comboboxist ei saa valida, lukku
                view.getLblShip().setText(model.getGame().getShipsCounter() + " / " + model.getGame().getShipsParts());//uue mängu korral lbl jne nulli
                //model.getGame().showGameboardMatrix(); //Nätab konsoolis laevade asukohta //Testiks
            } else { //Mäng käib
                gametimer.stopTimer();
                gametimer.setRunning(false);
                model.getGame().setGameOver();
                view.getBtnNewGame().setText("Uus mäng");
                view.getCmbSize().setEnabled(true);
                resetPlayerData();

            }
        }
    }

    //Edetabeli nupule vajutamine
    private class MyScoreButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Scoredata> result; //result defineerimine
            if(doRdoTbl) { //Lihtne tabel. Failist info lugemine .txt
                try {
                    result = view.readFromFile();
                    if(createTable(result)) {
                        scoreboarddialog.setModal(true); //Kui aken ees siis põhiaknalt ei saa valida
                        scoreboarddialog.pack();
                        scoreboarddialog.setLocationRelativeTo(null);
                        scoreboarddialog.setVisible(true);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else if(doRdoTblMdl) { //Table model. Failist info lugemine .txt
                //rdotblmdl
                try {
                    result = view.readFromFile();
                    if(createTableModel(result)) {
                        scoreboarddialog.setModal(true); //Kui aken ees siis põhiaknalt ei saa valida
                        scoreboarddialog.pack();
                        scoreboarddialog.setLocationRelativeTo(null); //Aken oleks põhiakne suhtes keskel
                        scoreboarddialog.setVisible(true);  //Tee dialoogi aken (edetabel) nähtavaks
                    }
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            } else if(doRdoDb) { //Table andmebaas. Andmebaasi failist info lugemine .db
                //rdodb
                result = readFromDatabase(model.getBoardsize());
                if(result != null && createTableDb(result)) { //Kas result sisaldab midagi
                    scoreboarddialog.setModal(true); //Kui aken ees siis põhiaknalt ei saa valida
                    scoreboarddialog.pack();
                    scoreboarddialog.setLocationRelativeTo(null); //Aken oleks põhiakne suhtes keskel
                    scoreboarddialog.setVisible(true);  //Tee dialoogi aken (edetabel) nähtavaks
                }
            }
        }
    }

    //Milline raadionupp valitud
    private class MyRadiobuttons implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JRadioButton rdoButton = (JRadioButton) e.getSource(); //cast
            if(rdoButton.equals(view.getRdoTable())) { //Kas rdoButton siin on sama, mis vormi pealt valitud. equals tekstide võrdlemiseks. Lihtne tabel
                doRdoTbl = true;
                doRdoTblMdl = false;
                doRdoDb = false;
            } else if (rdoButton.equals(view.getRdoTableModel())) { //Table model
                doRdoTbl = false;
                doRdoTblMdl = true;
                doRdoDb = false;
            } else if (rdoButton.equals(view.getRdoDatabase())){ //Table andmebaas
                doRdoTbl = false;
                doRdoTblMdl = false;
                doRdoDb = true;
                System.out.println("Andmebaas"); //Testimiseks
            }
        }
    }

    private void resetPlayerData() {
        gametimer.setMinutes(0);
        gametimer.setSeconds(0);
        model.getGame().resetClickCounter();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //Hiireklikkimine ainult siis, kui mäng käib
        if(gametimer.isRunning()) {
            int id = model.checkGridIndex(e.getX(),e.getY());
            int row = model.getRowById(id);
            int col = model.getColById(id);
            int[][] matrix = model.getGame().getBoardmatrix(); //Hetke laud
            if(matrix[row][col] == 0) { //Kas saaadi pihta. Vesi ehk mööda
                model.getGame().setUserClick(row, col, 8);
                model.getGame().setClickCounter(1);
                view.getLblShip().setText(model.getGame().getShipsCounter() + " / " + model.getGame().getShipsParts() + " möödas");
            } else if(matrix[row][col] > 0 && matrix[row][col] < 5) { //Kontrollb, kas oli laev
                model.getGame().setUserClick(row, col, 7);
                model.getGame().setClickCounter(1);
                model.getGame().setShipsCounter(1);
                view.getLblShip().setText(model.getGame().getShipsCounter() + " / " + model.getGame().getShipsParts() + " pihtas");
            }
            //model.getGame().showGameboardMatrix(); //Testiks
            view.repaint();
            //Kontrollib, kas mäng on läbi saanud
            if(model.getGame().isGameOver()) {
                gametimer.stopTimer();
                gametimer.setRunning(false); //Aeg seisma
                String name = JOptionPane.showInputDialog(view, "Kuidas on admirali nimi?");
                //Tühjad nimed ei lähe tekstifaili
                if(name.trim().isEmpty()) {
                    name = "Teadmata";
                }
                JOptionPane.showMessageDialog(view, "Mäng on läbi " + name);
                view.getBtnNewGame().setText("Uus mäng");
                view.getCmbSize().setEnabled(true);

                //Kirjuta tulemus faili
                view.writeToFile(name, model.getBoardsize(), model.getGame().getClickCounter(), gametimer.getPlayedTimeInSeconds());
                //Kirjuta tulemus andmebaasi
                addIntoDatabase(name, model.getBoardsize(), model.getGame().getClickCounter(), gametimer.formatGameTime());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //Hakkaks näitama hiire xy koordinaate
        String mouse = String.format("X: %03d and %03d", e.getX(), e.getY());
        view.getLblMouseXY().setText(mouse);
        //"Algseis"
        int id = model.checkGridIndex(e.getX(), e.getY());
        int row = model.getRowById(id);
        int col = model.getColById(id);

        //Id näitamine
        if(id != -1) {
            view.getLblID().setText(String.valueOf(id+1));
        }
        // ROw ja col näitamine
        String rowcol = String.format("%d : %d", row+1, col+1); //Inimlikus numbrid ehk +1
        if(row == -1 || col == -1) {
            rowcol = "Pole mängulaual";
        }
        view.getLblRowCol().setText(rowcol);// Näitab reaalselt

    }

    /**
     * Lisab mängija info andmebaasi
     * @param name nimi
     * @param boardsize laua suurus
     * @param clicks tehtud klikkide arv
     * @param gtime mänguaeg sekundites
     */
    private void addIntoDatabase(String name, int boardsize, int clicks, String gtime) {
        File f = new File(dbname); // Vaatame kas db on olema ja kui pole siis teeme.
        if(!f.exists()) {
            String url = "jdbc:sqlite:"+dbname;
            String sql = "CREATE TABLE \"scores\" (\n" +
                    "\t\"id\"\tINTEGER,\n" +
                    "\t\"name\"\tTEXT,\n" +
                    "\t\"board\"\tINTEGER,\n" +
                    "\t\"clicks\"\tINTEGER,\n" +
                    "\t\"gametime\"\tINTEGER,\n" +
                    "\t\"playedtime\"\tTEXT,\n" +
                    "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
                    ");";
            try(Connection con = DriverManager.getConnection(url);
                Statement stat = con.createStatement()) {
                stat.execute(sql);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                //System.out.println(e.getMessage());
            }
        }
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlite:"+dbname);
            String[] parts = gtime.split(":");
            int mtime = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            String sql = "INSERT INTO scores (name, board, clicks, gametime, playedtime) VALUES (?, ?, ?, ?, datetime('now', 'localtime'))";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, boardsize);
            statement.setInt(3, clicks);
            statement.setInt(4, mtime);
            statement.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.println("Andmebaasiga miski kamm");
            e.printStackTrace();
        }
    }

    //Esimese tabeli tegemiseks
    private boolean createTable(ArrayList<Scoredata> scoredatas) {
        if(scoredatas.size() > 0) {
            //TODO Masiivi sorteerimine kahe välja järgi SEE ON ERILINE
            //orderArrayList(scoredatas);

            String[][] data = new String[scoredatas.size()][5]; //5-mitu veergu tabelisse
            for(int i = 0; i < scoredatas.size(); i++) {
                data[i][0] = scoredatas.get(i).getName(); //Edetabel lahtrid
                data[i][1] = String.valueOf(scoredatas.get(i).convertSecToMMSS(scoredatas.get(i).getTime()));
                data[i][2] = String.valueOf(scoredatas.get(i).getClickcount());
                data[i][3] = String.valueOf(scoredatas.get(i).getBoardsize());
                data[i][4] = scoredatas.get(i).getPlayedtime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            table = new JTable(data, columnNames); //Teeb tabeli andmetega
            scoreboarddialog = new ScoreboardDialog(view);
            scoreboarddialog.add(new JScrollPane(table));
            scoreboarddialog.setTitle("Edetabel Jtable");
            return true;
        }
        return false;
    }

    //
    private boolean createTableModel(ArrayList<Scoredata> scoredatas) {
        if(scoredatas.size() > 0) {
            orderArrayList(scoredatas); //Massiivi sorteerimisekss
            DefaultTableModel tablemodel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) { //Et tabeli lahtrit ei saaks muuta
                    return false;
                }
            };
            table = new JTable(tablemodel); //Teeme tabeli
            // Lisame tabelile päise
            for(String columnName : columnNames) { //Lisatakse veeru nimi
                tablemodel.addColumn(columnName);
            }
            //Lisame tabelisse andmed
            for(Scoredata scoredata : scoredatas) {
                String name = scoredata.getName();
                int mytime = scoredata.getTime(); //Mänguaeg sekundites
                int clicks = scoredata.getClickcount();
                int board = scoredata.getBoardsize();
                String played = scoredata.getPlayedtime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                tablemodel.addRow(new Object[]{name, scoredata.convertSecToMMSS(mytime), clicks, board, played});
            }
            scoreboarddialog = new ScoreboardDialog(view);
            scoreboarddialog.add(new JScrollPane(table)); //Vajadusel näitab kerimist paremal
            scoreboarddialog.setTitle("Edetabel JTable model");
            return true;
        } else {
            return false;
        }
    }

    //
    private boolean createTableDb(ArrayList<Scoredata> scoredatas) {
        if(scoredatas.size() > 0) {
            DefaultTableModel tablemodel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; //Lahtri sisu ei saa muuta
                }
            };
            //Lahtrite joondamine
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer(); //Joondamine paremale 1
            rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT); //Joondamine paremale 2

            //Lahtri sisu keskele
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

            table = new JTable(tablemodel); //Teeb tabeli
            //Lisame tabalile klikkimise võimaluse
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    //Edetabeli pealne aken
                    JOptionPane.showMessageDialog(null, table.getValueAt(row, col));
                }
            });
            // Tabeli päis
            for(String columnName : columnNames) {
                tablemodel.addColumn(columnName);
            }
            //Tabeli sisu / andmed
            for(Scoredata scoredata : scoredatas) {
                String name = scoredata.getName();
                int mytime = scoredata.getTime();
                int clicks = scoredata.getClickcount();
                int board = scoredata.getBoardsize();
                String played = scoredata.getPlayedtime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                tablemodel.addRow(new Object[] {name, scoredata.convertSecToMMSS(mytime), clicks, board, played});
            }
            //Joonduvad keskele
            table.getColumn("Aeg").setCellRenderer(rightRenderer);
            //Joonduvad paremale
            table.getColumn("Klikke").setCellRenderer(rightRenderer);
            table.getColumn(columnNames[3]).setCellRenderer(rightRenderer); //Laua suurus
            table.getColumn(columnNames[4]).setCellRenderer(rightRenderer); //Mängimise aeg
            //Veeru laiuse määramine
            table.getColumnModel().getColumn(1).setPreferredWidth(5); //Aja veeru laius
            table.getColumnModel().getColumn(2).setPreferredWidth(10); //Klikkide veeru laius
            table.getColumnModel().getColumn(3).setPreferredWidth(20); //Laua suuruse veeru laius

            //Teeme dialoogi akne, kus näidata edetabelit
            scoreboarddialog = new ScoreboardDialog(view);
            scoreboarddialog.add(new JScrollPane(table));
            scoreboarddialog.setTitle("Edetabel Jtable andmebaas");
            return true;

        } else {
            return false;
        }
    }


    /**
     * Sorteerib massiivi
     * @param scoredatas massiiv mis loetud failist
     */
    private void orderArrayList(ArrayList<Scoredata> scoredatas) {
        scoredatas.sort((o1, o2) -> {
            // Aeg
            Integer x1 = o1.getTime();
            Integer x2 = o2.getTime();
            int sComp = x1.compareTo(x2); //Sorteerib kasvavalt. x1 ja x2 sorteerimise suund
            if (sComp != 0) {
                return sComp;
            }
            // Klikkide arv
            Integer y1 = o1.getClickcount();
            Integer y2 = o2.getClickcount();
            return y1.compareTo(y2); //Sorteerib kasvavalt. y1 ja y2 sorteerimise suund
        });
    }

    //Andmebaasist info lugemine, kus tulemust kirjutatakse arraylist scoredatasse
    private ArrayList<Scoredata> readFromDatabase(int boardsize) {
       try {
           Connection con = DriverManager.getConnection("jdbc:sqlite:" + dbname); //Andmebaasiühenduse tegemine
           Statement stat = con.createStatement();
           String sql = "SELECT * FROM scores WHERE board = " + boardsize + " ORDER BY gametime,clicks";
           ResultSet rs = stat.executeQuery(sql);
           ArrayList<Scoredata> sd = new ArrayList<>();
           if(rs.isBeforeFirst()) { //Sisaldab tulemust
               while (rs.next()) {
                   String name = rs.getString("name"); //"name"- tabelis veeru nimi scores.db
                   int board = rs.getInt("board");
                   int click = rs.getInt("clicks");
                   int seconds = rs.getInt("gametime");
                   String ptime = rs.getString("playedtime"); //Textina
                   LocalDateTime mytime = LocalDateTime.parse(ptime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                   sd.add(new Scoredata(name, seconds, board, click, mytime));
               }
               con.close(); //Andmebaasiühenduse sulgemine
               return sd;
           } else {
               return null;
           }
       } catch(SQLException e) {
           e.printStackTrace();
       }
       return null;
    }
}
