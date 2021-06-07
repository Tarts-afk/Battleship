import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Game {
    private final int boardsize; //Mängulaua suurus mängus
    private int[][] boardmatrix; //Mängulaual asuvad laevad (massiiv)
    private final Random random = new Random(); //Laevade juhuslik paigutus
    private final int[] ships = {4,3,3,2,2,2,1}; //Laevade suurused suuuremast väiksemaks. 7 Laeva. 17 ruutu
    private int shipsCounter = 0; // Laevu kokku (17 lõpus)
    private int clickCounter = 0; //Mitu klikki on tehtud laevade otsimisel
    private final List<Point> points = new ArrayList<>(); //Laevade paigutamisel vajalik


    public Game(int boardsize) { //Konstruktor
        this.boardsize = boardsize;
        this.boardmatrix = new int[boardsize][boardsize];
        //TEST EESMÄRGIL
        //setupNewGame();
        //showGameboardMatrix();
    }

    public void setupNewGame() {
        boardmatrix = new int[boardsize][boardsize]; //Algava mängu mängulaua suurus
        int shipTotal = ships.length; //Laevu kokku
        int shipsPlaced = 0; //Mitu laeva juba paigutatud

        while (shipsPlaced < shipTotal) { //Käib nii kaua, kuni sulgudes tingimus on tõene. shipsTotal = 7
            int row = random.nextInt(boardsize);
            int col = random.nextInt(boardsize);
            boolean vertical = random.nextBoolean();
            boolean placed = false; //Kas läks laev paika

            if(vertical) { //Proovib paigutada vertikaalselt
                placed = checkVerticalPlace(row, col, ships[shipsPlaced]);
            } else { //Proovib paigutada horisontaalselt
                placed = checkHorizontalPlace(row, col, ships[shipsPlaced]);
            }
            //Kas laev paigas
            if(placed) {
                shipsPlaced++; //Võta järgmine laev
            }
        }
        replaceNineToZero();
    }

    private boolean checkVerticalPlace(int row, int col, int length) {
        points.clear();
        boolean placed = false; //See laev pole veel paigas
        if(row + length < boardsize) { //Kas laev mahub siia kohta mänguväljal
            for(int i = row; i < row + length; i++) {
                if(boardmatrix[i][col] == 0) {
                    points.add(new Point(i, col)); //Laeva kast paigas
                } else {
                    points.clear();
                    points.add(new Point(-1,-1));
                }
            }
        } else {
            points.add(new Point(-1,-1));
        }

        if(points.get(0).getRow() !=-1) { //Me saime laeva paika
            for(int i = row; i < row + length; i++) {
                boardmatrix[i][col] = length;
            }
            placed = true;
            fillVerticalProtection(row, col, length);
        }
        return placed;
    }

    private boolean checkHorizontalPlace(int row, int col, int length) {
        points.clear();
        boolean placed = false;
        if(col + length < boardsize) {
           for(int i = col; i < col + length; i++) {
               if(boardmatrix[row][i]==0) {
                   points.add(new Point(row, i));
               } else {
                   points.clear();
                   points.add(new Point(-1,-1));
               }

           }
        } else {
            points.add(new Point(-1,-1));
        }
        if (points.get(0).getCol() != -1) {
            for(int i = col; i < col + length; i++){
                boardmatrix[row][i] = length;
            }
            placed = true;
            fillHorizontalProtection(row, col, length);
        }
        return placed;
    }

    //Et laevad ei oleks vertikaalselt külgipidi koos, paneb laevade ümber 9sad.
    private void fillVerticalProtection(int row, int col, int length) {
        //Vasakule ja paremale kaitse
        for(int i = row; i < row + length; i++) {
            if(col - 1 >= 0) { //Vasakule poole laevast paneb 9
                boardmatrix[i][col-1] = 9; //Kaitse
            }
            if(col + 1 < boardsize) { //Paremale poole laevast paneb 9
                boardmatrix[i][col+1] = 9;
            }
        }
        // Ülemise rea kaitse
        if(row-1 >= 0){
            boardmatrix[row-1][col] = 9; //Laeva nina
            if(col - 1 >= 0){
                boardmatrix[row-1][col-1] = 9; //Ülemine vasak nurk
            }
            if(col + 1 < boardsize) {
                boardmatrix[row-1][col+1] = 9;//Ülemine parem nurk
            }
        }
        //Alumise rea kaitse
        if(row + length < boardsize) {
            boardmatrix[row+length][col] = 9; //Laeva alumine nina
            if(col - 1 >=0) {
                boardmatrix[row+length][col-1] = 9; //Alumine vasak nurk
            }
            if(col + 1<boardsize) {
                boardmatrix[row+length][col+1] = 9; //Alumine parem nurk
            }
        }
    }

    //Et laevad ei oleks horisontaalselt külgipidi koos, paneb laevade ümber 9sad.
    private void fillHorizontalProtection(int row, int col, int length){
        for(int i = col; i<col+length;i++) {
            if(row-1>=0) {
                boardmatrix[row-1][i] =9;
            }
            if(row+1<boardsize) {
                boardmatrix[row+1][i]=9;
            }
        }//for lõpp
        //Vasakpoolne ots
        if(col-1>=0) {
            boardmatrix[row][col-1] = 9; //Laevanina
            if(row-1>=0) {
                boardmatrix[row-1][col-1]=9; //Ülemine nurk
            }
            if(row+1<boardsize) {
                boardmatrix[row+1][col-1]=9; //Alumine nurk
            }
        }
        //Parem ots
        if(col+length<boardsize) {
            boardmatrix[row][col+length]=9;//Nina
            if(row-1>=0) {
                boardmatrix[row-1][col+length]=9;
            }
            if(row+1<boardsize) {
                boardmatrix[row+1][col+length]=9;
            }
        }
    } //fillHorizontalProtection lõpp

    //9-ad asendab 0-idega
    private void replaceNineToZero() {
        for(int row=0; row < boardsize ; row++) {
            for(int col =0; col<boardsize; col++) {
                if(boardmatrix[row][col] == 9) {
                    boardmatrix[row][col] = 0;
                }
            }
        }
    }

    //Konsoolis mängu kuvamiseks
    public void showGameboardMatrix() {
        System.out.println(); //Reavahetus
        for(int row = 0; row < boardsize; row++) {
            for(int col = 0; col < boardsize; col++) {
                System.out.print(boardmatrix[row][col] + " ");
            }
            System.out.println(); //Reavahetus peale rea lõppu
        }
    }

    public int[][] getBoardmatrix() {
        return boardmatrix;
    }

    public int getShipsCounter() {
        return shipsCounter;
    }

    public void setShipsCounter(int shipsCounter) {
        this.shipsCounter += shipsCounter;
    }

    public int getClickCounter() {
        return clickCounter;
    }

    public void setClickCounter(int clickCounter) {
        this.clickCounter += clickCounter;
    }

    //Uue mänguga laevade lugemine reset
    public void resetClickCounter() {
        this.clickCounter = 0;
    }

    //Liidab kokku laevade massiivi numbrid
    public int getShipsParts() {
        return IntStream.of(ships).sum();
    }

    //Kus kasutaja klikkis. Pihta või mööda
    public void setUserClick(int row, int col, int what) {
        if(what == 7) {
            boardmatrix[row][col] = 7; //Saadi pihta
        } else {
            boardmatrix[row][col] = 8; //Kliki mööda
        }
    }

    public boolean isGameOver() {
        return getShipsParts() == getShipsCounter();
    }

    public void setGameOver() {
        shipsCounter = getShipsParts();
    }
}
