package tt.map;

public class Wind {
    private int direction;
    private int strength;

    public Wind() {
        direction = Math.random() < 0.5 ? -1 : 1;
        strength = (int) ( (direction)*((Math.random() * 36) - 35));
    }

    // update wind after each turn
    public void update(){
        strength += (Math.random() * 11) -5;

        // strength stay in -35 between 35
        strength = Math.min(Math.max(strength,-35),35);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
