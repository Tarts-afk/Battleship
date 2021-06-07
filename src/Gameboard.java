// Vasakpoolne väli
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Gameboard extends JPanel {
    private final Model model;
    private final int startX = 5;
    private final int startY = 5;
    private final int width = 30;
    private final int height = 30;
    private final String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};


    public Gameboard(Model model) {
        this.model = model;
        setBackground(new Color(135,205,250));//Mängulaua suuruse ja värvuse määramine (laevade plats)
    }

    @Override
    public Dimension getPreferredSize() {
        int w = (width * model.getBoardsize()) + width + (2 * startX); //Mängulaua laius
        int h = (height * model.getBoardsize()) + height + (2 * startY); //Mängulaua kõrgus
        return new Dimension(w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        g.setFont(new Font("Verdana", Font.BOLD, 14)); //Kirjastiil ja suurus abc
        //Joonista tähestiku ruudustik
        drawColAlphabet(g);
        //Joonistab rea numbrid
        drawRowNumbers(g);
        //Joonistab ülejäänud lauaosa
        drawGameGrid(g);
        //Kui on mäng siis näita laevu ka
        if(!model.getGame().isGameOver()) {
            model.drawUserBoard(g);
        }
        drawGameGrid(g);
    }

    // Joonistab mängulauale tähed
    private void drawColAlphabet(Graphics g) {
        int i = 0; //Abimuutuja tähestiku massiivist tähe saamiseks
        g.setColor(Color.BLUE); //Joone värv
        for(int x = startX; x <= (width * model.getBoardsize()) + width; x+=width) {
            g.drawRect(x, startY, width, height); //Joonistab ruudustiku tähestikule
            if(x > startX) { //Et esimene lahter jäeks tühjaks
                g.drawString(alphabet[i], x + (width/2) - 5, 2*(startY+startY)+5);
                i++; //Et lisaks tähti abc, mitte aaa
            }
        }
    }

    // Joonistab mängulauale numbrid
    private void  drawRowNumbers(Graphics g) {
        int i = 1; //Number, mis joonistatakse lauale
        g.setColor(Color.RED);
        for(int y = startY+height; y < (height * model.getBoardsize()) + height; y+=height) {
            g.drawRect(startX, y, width, height);
            if(i < 10) {
                // 1-9 numbrite kasvamine. 5 pikslit paremale
                g.drawString(String.valueOf(i), startX + (width / 2) - 5, y + 2 * (startY + startY));
            } else {
                //10-15 numbrite kasvamine. 10 ehk veel 5 pikslit paremale
                g.drawString(String.valueOf(i), startX + (width / 2) - 10, y + 2 * (startY + startY));
            }
            i++; //Numbri kasvamine järgmiseks numbriks
        }
    }

    //Ruudustiku joonistamine
    private void drawGameGrid(Graphics g) {
        ArrayList<Griddata> matrix = new ArrayList<>();
        g.setColor(Color.BLACK);
        int x = startX + width;
        int y = startY + height;
        int i = 1;
        for(int r = 0; r < model.getBoardsize(); r++) { //Ridade jaoks
            for(int c = 0; c < model.getBoardsize(); c++) { //Veergude jaoks
                g.drawRect(x, y, width, height); //Joonistab jooned lauale
                matrix.add(new Griddata(r,c,x,y,width,height));
                x += width; // Laiuse võrra x kasvab vasakult paremale
            }
            y = (startY + height) + (height * i); //Y koordinaadid ülevalt alla kasvavalt
            i +=1;
            x = startX + width;
        }
        model.setGriddata(matrix);
    }
}
