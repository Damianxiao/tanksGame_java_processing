package tt.map;

public class Position {
    private char symbol;
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(int x,int y,char symbol){
        this.x  = x;
        this.y = y;
        this.symbol = symbol;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getSymbol() {
        return String.valueOf(symbol);
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }
}
