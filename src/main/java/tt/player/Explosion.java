package tt.player;

import processing.core.PApplet;

import java.util.Date;

public class Explosion extends PApplet {
    private float x, y;
    private float maxRadius;
    private float currentRadiusRed;
    private float currentRadiusOrange;
    private float currentRadiusYellow;
    private float growthRate;
    private int s;


    // Constructor
    public Explosion(float x, float y, float maxRadius) {
        this.x = x;
        this.y = y;
        this.maxRadius = maxRadius;
        this.s = millis();
    }


    public void update() {
        float elapsedTime = millis() - s;
        float percentComplete = elapsedTime / 200;
        this.currentRadiusRed = min(maxRadius, percentComplete * maxRadius);
        this.currentRadiusOrange = min((int) (maxRadius * 0.5), (int) (percentComplete * maxRadius * 0.5));
        this.currentRadiusYellow = min((int) (maxRadius * 0.2), (int) (percentComplete * maxRadius * 0.2));
        // explosion is finished
        if(currentRadiusRed == maxRadius && currentRadiusOrange == (int) (maxRadius * 0.5) && currentRadiusYellow == (int) (maxRadius * 0.2)){
            this.currentRadiusRed = 0;
            this.currentRadiusOrange = 0;
            this.currentRadiusYellow = 0;
        }
    }

    public boolean isFinished() {
        return millis() - s >= 1500;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
    }

    public float getCurrentRadiusRed() {
        return currentRadiusRed;
    }

    public void setCurrentRadiusRed(float currentRadiusRed) {
        this.currentRadiusRed = currentRadiusRed;
    }

    public float getCurrentRadiusOrange() {
        return currentRadiusOrange;
    }

    public void setCurrentRadiusOrange(float currentRadiusOrange) {
        this.currentRadiusOrange = currentRadiusOrange;
    }

    public float getCurrentRadiusYellow() {
        return currentRadiusYellow;
    }

    public void setCurrentRadiusYellow(float currentRadiusYellow) {
        this.currentRadiusYellow = currentRadiusYellow;
    }

    public float getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(float growthRate) {
        this.growthRate = growthRate;
    }

}
