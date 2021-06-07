public class Griddata {
    private final int row;
    private final int col;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    //Konstruktor
    public Griddata(int row, int col, int x, int y, int width, int height) {
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //Getterid
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
