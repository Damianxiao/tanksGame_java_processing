package tt.player;

import tt.map.Map;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static processing.core.PApplet.map;
import static processing.core.PApplet.radians;

public class Bullet {
    private char symbol;
    private int x;
    private int y;
    private float vx;
    private float vy;
    private int angle;
    private int power;
    private final float gravity = 3.6f/60;
    private boolean active = true;
    private boolean isExploded = false;

    public Bullet(char symbol, int x, int y, int  angle) {
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.power = 50;
        this.active = true;
        this.isExploded = false;
    }


    public Bullet(char symbol, int x, int y, int  angle, int power) {
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.power = power;
        float initialVelocity = map(power, 0, 100, 1, 9);
        this.vx = (float) (initialVelocity * cos(radians(angle)));
        this.vy = -(float) (initialVelocity * sin(radians(angle)));
        this.active = true;
        this.isExploded = false;
    }


    // angle convert
    public static int angleConvert(int angle) {
        if(angle>=90){
            return  270-angle;
        }
        else {
            return angle;
        }
    }

    // update bullet
    public void update() {
        if(active){
            vy += gravity;
            x += vx;
            y += vy;
        }
        // if bullet out of map
        if (isOutOfMap()) {
           active = false;
        }

    }

    // check if bullet is exploded
    public boolean isExploded(Map map) {
        int x = (int) this.x;
        int y = (int) this.y;
        int row = (int) this.y/32;

        if (x >= 0 && x < map.getHeightsArray().length && row >= 0 && row < map.getTerrain()[0].length) {
            int terrainHeight = map.getHeightsArray()[x];
            if (y >= 640-terrainHeight) {
                return true;
            }
        }
        return false;
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

    public void setAngle(int  angle) {
        this.angle = angle;
    }

    public boolean isOutOfMap() {
        return x < 0 || x > 864 || y < 0 || y > 640;
//        return x < -100 || x > 1000 || y < -100 || y > 800;
    }

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public float getGravity() {
        return gravity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
