package tt.player;

public interface TankInterface {
    // Rotate the tank's tower
    void rotateTower(double angle);

    // Move the tank
    void move(int x, int y);

    // Repair the tank
    void repair();

    // Increase fuel
    void increaseFuel();

    // Increase parachute
    void increaseParachute();

    // Gain parachute
    void gainParachute();

    // Gain fuel
    void gainFuel(int i);

    // Gain life
    void gainLife(int i);

    // Increase power
    void increasePower();

    // Gain power
    void gainPower(int i);

    // Get the angle of the tank's tower
    int getAngle();

    // Get the symbol of the tank
    char getSymbol();

    // Get the X coordinate of the tank
    int getX();

    // Set the X coordinate of the tank
    void setX(int x);

    // Get the Y coordinate of the tank
    int getY();

    // Set the Y coordinate of the tank
    void setY(int y);

    // Get the life of the tank
    int getLife();

    // Set the life of the tank
    void setLife(int life);

    // Check if the tank is alive
    boolean isAlive();

    // Set the status of the tank (alive/dead)
    void setAlive(boolean alive);

}
