package tt.player;

import static java.lang.Math.abs;

public class Tank {
    private char symbol;
    private int x;
    private int y;
    private int life;
    private boolean isAlive;
    private int activationTime;
    private double angle;
    private int power;
    private int fuel;
    private int parachute = 0;
    private boolean isShooted;
    private int score;
    private int [] toolBag;
    private boolean canon;

    // constructor
    public Tank(char symbol, int x, int y, int life, boolean isAlive, int activationTime, int power,int fuel) {
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.life = life;
        this.isAlive = isAlive;
        this.activationTime = activationTime;
        // set the default angle to 0
//        this.angle = 90;
        // random between 90 and 270
        this.angle = Math.random() * 180 + 90;
        this.power = power;
        this.fuel = fuel;
        this.parachute = 3;
        this.isShooted = false;
        this.score = 0;
        this.toolBag = new int[4];
        this.canon = false;
    }

    // rotateTower emsure the angle is between 90 and 270
    public void rotateTower(double angle) {
        this.angle += angle;
        if (this.angle < 90) {
            this.angle = 90;
        } else if (this.angle >270) {
            this.angle = 270;
        }
    }

    public void parachute(int x, int y){
        this.y += y;
    }

    //move the tank
    public void move(int x, int y) {
        this.x += x;
        this.y += y;
        this.fuel -= abs(x);
    }

    public void gainScore(int score,Tank t){
        if(t != this){
            this.score += score;
        }
    }

    public void repair(){
        //use
        if(this.toolBag[0] > 0 && this.life < 100){
            gainLife(20);
            this.toolBag[0]--;
        }else if(score>= 20){
            //buy
            this.setScore(getScore()-20);
            this.toolBag[0]++;
        }
    }

    public void increaseFuel(){
        if(this.toolBag[1] > 0 && this.fuel < 250){
            gainFuel(200);
            this.toolBag[1]--;
        }else if(score>= 10){
            this.setScore(getScore()-10);
            this.toolBag[1]++;
        }
    }

    public void increaseParachute(){
        if(this.toolBag[2] >0 && this.parachute < 3){
            gainParachute();
            this.toolBag[2]--;
        } else if (score >= 10 ) {
            this.setScore(getScore()-10);
            this.toolBag[2]++;
        }
    }

    public void gainParachute() {
        if(this.parachute<3){
            this.parachute++;
        }
    }

    public void gainFuel(int i) {
        if (this.fuel + i > 250) {
            this.fuel = 250;
        } else if (this.fuel + i < 0) {
            this.fuel = 0;
        } else {
            this.fuel += i;
        }
    }

    public void gainLife(int i) {
        if(this.life + i > 100) {
            this.life = 100;
        } else if (this.life + i < 0) {
            this.life = 0;
        } else{
            this.life += i;
        }
    }

    public void increasePower(){
       if(!isCanon() && score >= 20) {
           this.score -= 20;
           this.canon =true;
       }
    }


    public void gainPower(int i) {
        int maxPower = Math.min(100, this.life); // Ensure power is less than or equal to life
        if (i > 0 && this.power + i > maxPower) {
            this.power = maxPower;
        } else if (i < 0 && this.power + i <= 0) {
            this.power = 1;
        } else {
            this.power += i;
        }
    }
    // getters and setters

    public int getAngle() {
        return (int) angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
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

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public boolean hasParachute() {
        return parachute > 0;
    }

    public void reduceLife(int v){
        this.life -=v;
        if(this.life <= 0){
            this.isAlive = false;
        }
//        this.isAlive = false;
    }

    public int getParachute() {
        return parachute;
    }

    public void setParachute(int parachute) {
        this.parachute = parachute;
    }

    public boolean isShooted() {
        return isShooted;
    }

    public void setShooted(boolean shooted) {
        isShooted = shooted;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int[] getToolBag() {
        return toolBag;
    }

    public void setToolBag(int[] toolBag) {
        this.toolBag = toolBag;
    }

    public boolean isCanon() {
        return canon;
    }

    public void setCanon(boolean canon) {
        this.canon = canon;
    }
}
