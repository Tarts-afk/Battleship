import java.awt.*;
import java.util.ArrayList;
//Siit algab klass
public class Model {

    private int boardsize = 10; //Vaikimisi algne mängulaua suurus, mida saab hiljem muuta
    private ArrayList<Griddata> griddata;
    private Game game;

    public Model() { //Konstruktor
        this.griddata = new ArrayList<>();
        this.game = new Game(boardsize); //Boardsize- mängulaua suurus
    }

    //Uue mängu tegemine
    public void setupNewgame() {
        game = new Game(boardsize);
    }

    public int getBoardsize() {
        return boardsize;
    }

    public void setBoardsize(int boardsize) {
        this.boardsize = boardsize;
    }

    public ArrayList<Griddata> getGriddata() {
        return griddata;
    }

    public Game getGame() {
        return game;
    }

    public void setGriddata(ArrayList<Griddata> griddata) {
        this.griddata = griddata;
    }

    //Hiir teaks veergu ja id-d
    public int checkGridIndex(int mouseX, int mouseY) {
        int result = -1; //Kui õiget indeksit ei leita siis on tulemuseks -1
        int index = 0;//Milline indeks on hetkel
        for(Griddata gd: griddata) { //Käib läbi griddata
            if(mouseX > gd.getX() && mouseX <= (gd.getX() + gd.getWidth())
                    && mouseY > gd.getY() && mouseY <= (gd.getY() + gd.getHeight())) {
                result = index;
                //For-loop katkestus, sest indeks on leitud
            }
            index++;
        }
        return result; //Ei leidnud ehk tulemuseks -1
    }

    //
    public int getRowById(int id) {
        //KOntrollime, et id -1 ei ole
        if(id != -1) {
            return griddata.get(id).getRow();
        }
        return -1;
    }

    //
    public int getColById(int id) {
        //KOntrollime, et id -1 ei ole
        if(id != -1) {
            return griddata.get(id).getCol();
        }
        return -1;
    }

    //Ruudustiku lahtri peal klikkimise tagajärjed
    public void drawUserBoard(Graphics g) {
        ArrayList<Griddata> gd = getGriddata();
        int[][] matrix = game.getBoardmatrix();
        int id = 0;
        for(int r = 0; r < game.getBoardmatrix().length; r++) { //MItu rida on
            for(int c = 0; c < game.getBoardmatrix()[0].length; c++) { //Mitu veergu on
                if(matrix[r][c] == 0) { //Tuvastatakse vesi
                    g.setColor(new Color(0,190,255));
                    if(gd.get(id).getRow() == r && gd.get(id).getCol() == c) {
                        g.fillRect(gd.get(id).getX(), gd.get(id).getY(), gd.get(id).getWidth(), gd.get(id).getHeight());
                    }
                } else if(matrix[r][c] == 7){ //Tuvastatakse pihtas
                    g.setColor(Color.GREEN);
                    if(gd.get(id).getRow() == r && gd.get(id).getCol() == c) {
                        g.fillRect(gd.get(id).getX()+3, gd.get(id).getY()+3, gd.get(id).getWidth()-6, gd.get(id).getHeight()-6);
                    }
                } else if(matrix[r][c] == 8) { //Tuvastatakse möödas
                    g.setColor(Color.RED);
                    if(gd.get(id).getRow() == r && gd.get(id).getCol() == c) {
                        g.fillRect(gd.get(id).getX()+3, gd.get(id).getY()+3, gd.get(id).getWidth()-6, gd.get(id).getHeight()-6);
                    }
                } else if(matrix[r][c] > 0 && matrix[r][c] < 5){ //Peida laeva asukohas
                    g.setColor(new Color(0,190,255));
                    if(gd.get(id).getRow() == r && gd.get(id).getCol() == c) {
                        g.fillRect(gd.get(id).getX(), gd.get(id).getY(), gd.get(id).getWidth(), gd.get(id).getHeight());
                    }
                }
                id++;
            }
        }
    }
}
