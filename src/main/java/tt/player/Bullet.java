package tt.player;

public class Bullet {
    private char symbol;
    private int x;
    private int y;
    private double angle;

    public Bullet(char symbol, int x, int y, double angle) {
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void move() {
        x += Math.cos(Math.toRadians(angle));
        y += Math.sin(Math.toRadians(angle));
    }

    public boolean isOutOfMap() {
        return x < 0 || x > 86 || y < 0 || y > 64;
    }

}
