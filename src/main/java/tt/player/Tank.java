package tt.player;

public class Tank {
    private char symbol;
    private int x;
    private int y;
    private int life;
    private boolean isAlive;
    private int activationTime;
    private double angle;

    // constructor
    public Tank(char symbol, int x, int y, int life, boolean isAlive, int activationTime) {
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.life = life;
        this.isAlive = isAlive;
        this.activationTime = activationTime;
        // set the default angle to 0
        this.angle = 0;
    }

    // rotateTower emsure the angle is between -90 and 90
    public void rotateTower(double angle) {
        this.angle += angle;
        if (this.angle >= 90) {
            this.angle = 90;
        } else if (this.angle <= -90) {
            this.angle = -90;
        }
    }

    // getters and setters

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
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

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(int activationTime) {
        this.activationTime = activationTime;
    }

}
