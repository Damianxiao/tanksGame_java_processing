package tt.player;

import tt.map.Map;
import tt.map.Wind;

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
    private boolean ex = false;

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

    public Bullet(char symbol, int x, int y, int  angle, int power,boolean ex) {
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
        this.ex = ex;
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
    public void update(Wind wind) {
        // wind affect the bullet x
        float windAcceleration = (wind.getStrength() * 0.03f/60);
        if(active){
            vy += gravity;
            vx += windAcceleration;
            x += vx;
            y += vy;
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

    public boolean isOutOfMap() {
        return x < 0 || x > 950 || y < 0 || y > 750;
//        return x < -100 || x > 1000 || y < -100 || y > 800;
    }

    public int getPower() {
        return power;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public boolean isEx() {
        return ex;
    }

}
