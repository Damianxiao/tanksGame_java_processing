package tt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;
import tt.map.Map;
import tt.map.MapLoader;
import tt.map.Position;
import tt.map.Wind;
import tt.player.Bullet;
import tt.player.Explosion;
import tt.player.Tank;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;


import static javafx.scene.paint.Color.color;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class test extends PApplet {
    private static Map map;
    private static ArrayList<Tank> tanks = new ArrayList<>();
    private Tank currentTank;
    private int currentPlayerIndex = 0;
    private int lastSwitchTime = 0;
    private boolean showArrow = true;
    private int selectedLevel = -1;
    private JSONObject config;
    private static final String levelPath = "src/main/resources/level/";
    private final String picPath = "src/main/resources/pic/";
    private ArrayList<Bullet> projectiles = new ArrayList<>();
    private Explosion explosion;
    private Explosion  tankExplosion;
    private static processingTest app;
//    private static Processing app;
    private static Robot robot;


    @BeforeEach
    public void init() throws AWTException {
        app = new processingTest();
        app.noLoop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.startGame();
        app.key = '1';
        app.keyPressed();
        app.keyReleased();
        robot = new Robot();
        map = app.map;
    }

    // maploader
    @Test
    public void mapLoadTest() {
        map = MapLoader.loadMap(levelPath + "testLevel.txt");
        assertNotNull(map,"map is null");
        assertNotNull(map.getTerrain(),"map terrain is null");
        assertNotNull(map.getPlayerPositions(),"player positions is null");
        assertNotNull(map.getTreePositions(),"tree positions is null");
        // get the terrain X
        int xCount = 0;
        int tCount = 0;
        for(List<Character> a: map.getGrid()){
            for(char c: a){
                if(c == 'X'){
                    xCount++;
                }
                if(c == 'T'){
                    tCount++;
                }
            }
        }
        assertTrue(xCount>20,"terrain 'X' ground count should be above 20");
        assertTrue(tCount>0,"terrain 'T' Tree count should be above 0");
        assertFalse(map.getPlayerPositions().isEmpty(), "player count should be above 0");
        assertEquals('A', map.getPlayerPositions().get(0).getSymbol().charAt(0),"firt player should be A");
        //load resources;
        assertNotNull(app.ex,"ex image is null");
        assertNotNull(app.gas,"gas station image is null");
        assertNotNull(app.wind,"wind image is null");
        assertNotNull(app.repair,"fuel image is null");
        assertNotNull(app.repair,"repair image is null");
        assertNotNull(app.parachuteKit,"para kit image is null");
    }

    /* *
     * @Description
     *  processing test
    */
    @Test
    public void testSuriveTanks() {
        int count = 0;
        for(Position p : app.map.getPlayerPositions()){
            count++;
        }
        app.tanks.add(new Tank('A', 0, 0, 100, true, 0, 50, 250));
        app.tanks.add(new Tank('B', 0, 0, 0, false, 0, 50, 250));
        app.tanks.add(new Tank('C', 0, 0, 50, true, 0, 50, 250));
        assertEquals(count+2, app.suriveTanks());
    }

    @Test
    public void testGetHighestScoringPlayer() {
        Tank tankA = new Tank('A', 0, 0, 100, true, 0, 50, 250);
        Tank tankB = new Tank('B', 0, 0, 0, false, 0, 50, 250);
        Tank tankC = new Tank('C', 0, 0, 50, true, 0, 50, 250);

        tankA.setScore(100);
        tankB.setScore(50);
        tankC.setScore(200);

        app.tanks.add(tankA);
        app.tanks.add(tankB);
        app.tanks.add(tankC);

        assertEquals(tankC, app.getHighestScoringPlayer());
    }


    @Test
    public void testCheckTankCollision() {
        Explosion explosion = new Explosion(100, 100, 50);
        app.explosion = explosion;
        Tank tank = new Tank('F', 100, 100, 100, true, 0, 50, 250);
        Tank tankG = new Tank('G', 11, 111, 100, true, 0, 50, 250);
        app.tanks.add(tank);
        app.tanks.add(tankG);

        assertFalse(app.checkTankCollision(400, 30)); // tankG out of range
        assertTrue(app.checkTankCollision(100, 30)); // tankF within explosive range
        assertEquals(60, tank.getLife()); // Check tankB life reduction take 40 damage from explosion
    }

    @Test
    public void testTurnSwitch() {
        app.tanks.clear();
        Tank tankA = new Tank('A', 10, 100, 100, true, 0, 50, 250);
        Tank tankB = new Tank('B', 20, 100, 100, true, 0, 50, 250);

        app.tanks.add(tankA);
        app.tanks.add(tankB);

        assertEquals(0, app.currentPlayerIndex);
        app.turnSwitch();
        assertEquals(1, app.currentPlayerIndex);
        app.turnSwitch();
        assertEquals(0, app.currentPlayerIndex);
        // Check if switch occurred after tank's death
        tankA.setAlive(false);
        app.turnSwitch();
        assertEquals(1, app.currentPlayerIndex);
    }

    @Test
    public void testDeployParachute() {
        app.tanks.clear();
        Tank tankA = new Tank('A', 10, 100, 100, true, 0, 50, 250);
        app.tanks.add(tankA);

        tankA.setParachute(0);
        app.deployParachute(tankA, 10);
        assertEquals(105, tankA.getY()); // Parachute not deployed

        tankA.setY(100);
        tankA.setParachute(1);
        app.deployParachute(tankA, 10);
        assertEquals(101, tankA.getY()); // Parachute deployed
    }

    /* *
     * @Description
     * tank test
    */
    @Test
    public void testTankInit(){
        // test tank state
        for(Tank tank:app.tanks){
            int x = tank.getX();
            int y = tank.getY();
            assertNotNull(tank,"tank");
            assertNotNull(tank.getToolBag(),"tool bag");
            assertNotNull(tank.getAngle(),"angle is null");
            assertNotNull(tank.getFuel(),"fuel is null");
            assertNotNull(tank.getLife(),"life is null");
            assertNotNull(tank.getPower(),"power is null");
            assertNotNull(tank.getScore(),"score is null");
            assertNotNull(tank.getParachute(),"parachute is null");
            assertNotNull(tank.getActivationTime(),"activation time is null");
            // test tank position
            assertTrue(x>=0 && x<=864,"tank x should be between 0 and 864");
            assertTrue(y>=0 && y<=648,"tank y should be between 0 and 640");
        }
    }


    @Test
    public void testTankMove() throws AWTException {
        // tank move
        app.currentPlayerIndex = 0;
        int diff = 0;
        app.tanks.get(app.currentPlayerIndex).setX(400);
        Tank tank = app.tanks.get(app.currentPlayerIndex);
        // Test right move
        testTankMovement(tank,KeyEvent.VK_RIGHT, tank.getX() + 1, "Tank should move right by one unit.");
        // Test left move
        testTankMovement(tank,KeyEvent.VK_LEFT, tank.getX() - 1, "Tank should move left by one unit.");
        // if fuel <=0  cant move
        tank.setX(0);
        tank.setFuel(0);
        testTankMovement(tank,KeyEvent.VK_RIGHT, tank.getX(), "Tank should not move right when fuel is 0.");
        testTankMovement(tank,KeyEvent.VK_LEFT, tank.getX(), "Tank should not move left when fuel is 0.");
        // if isShooted cant move
        tank.setShooted(true);
        testTankMovement(tank,KeyEvent.VK_RIGHT, tank.getX(), "Tank should not move right when isShooted.");
        testTankMovement(tank,KeyEvent.VK_LEFT, tank.getX(), "Tank should not move LEFT when isShooted.");

        // if right is downhill y should decrease
        tank.setX(20);
        tank.setFuel(100);
        diff = map.getHeightsArray()[tank.getX()] - map.getHeightsArray()[tank.getX() + 1];
        testTankMovementHill(tank,KeyEvent.VK_RIGHT, tank.getX()+1,tank.getY()+diff, "Tank should not move right when right is downhill.");
        //if left is downhill y should decrease
        tank.setFuel(100);
        diff = map.getHeightsArray()[tank.getX()] - map.getHeightsArray()[tank.getX() - 1];
        testTankMovementHill(tank,KeyEvent.VK_LEFT, tank.getX()-1,tank.getY()+diff, "Tank should not move left when right is downhill.");
        // if right is uphill y should increase
        tank.setX(800);
        tank.setFuel(100);
        diff = map.getHeightsArray()[tank.getX()] - map.getHeightsArray()[tank.getX() + 1];
        testTankMovementHill(tank,KeyEvent.VK_RIGHT, tank.getX()+1,tank.getY()+diff, "Tank should not move right when right is uphill.");
        //if left is uphill y should increase
        tank.setX(100);
        tank.setFuel(100);
        diff = map.getHeightsArray()[tank.getX()] - map.getHeightsArray()[tank.getX() - 1];
        testTankMovementHill(tank,KeyEvent.VK_LEFT, tank.getX()-1,tank.getY()+diff, "Tank should not move left when right is uphill.");



        app.tanks.get(app.currentPlayerIndex).setX(0);
        // Test border when tank x = 0
        testTankMovement(tank,KeyEvent.VK_LEFT, 0, "Tank can't left move when x=0.");

        app.tanks.get(app.currentPlayerIndex).setX(864);
        // Test border when tank x = 864
        testTankMovement(tank,KeyEvent.VK_RIGHT, 864, "Tank can't right move when x=864.");
    }

    @Test
    public void testTankTowerRotate(){
        // Test tower rotate up
        testTankTowerRotate(90, 3, KeyEvent.VK_UP, "Tower should rotate up by 3 degree.");

        // Test tower rotate down
        testTankTowerRotate(180, -3, KeyEvent.VK_DOWN, "Tower should rotate down by 3 degree.");

        // Test tower can't rotate less than 90 degree
        testTankTowerRotate(90, 0, KeyEvent.VK_DOWN, "Tower can't rotate less than 90 degree.");

        // Test tower can't rotate more than 270 degree
        testTankTowerRotate(270, 0, KeyEvent.VK_UP, "Tower can't rotate more than 270 degree.");
    }

    // gain score
    @Test
    public void testGainScore() {
        Tank tank1 = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        Tank tank2 = new Tank('T', 0, 0, 100, true, 0, 50, 100);

        tank1.gainScore(30, tank2);
        assertEquals(80, tank1.getScore());

        tank1.gainScore(20, tank1); // Not affecting own score
        assertEquals(80, tank1.getScore());
    }


    @Test
    public void testRepair() {
        // test repair tool
        Tank tank = new Tank('T', 0, 0, 50, true, 0, 50, 100);
        tank.setScore(20);
        tank.repair();
        assertTrue(tank.getToolBag()[0] == 1);
        tank.repair();
        assertEquals(70, tank.getLife());


        tank = new Tank('T', 0, 0, 50, true, 0, 50, 100);
        tank.setScore(5); // Low score, repair fails
        tank.repair();
        assertTrue(tank.getToolBag()[0] == 0);
        tank.repair();
        assertEquals(50, tank.getLife());

        tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setScore(100); // Full life, repair fails
        tank.repair();
        assertTrue(tank.getToolBag()[0] == 1);
        tank.repair();
        assertEquals(100, tank.getLife());

    }


    @Test
    public void testIncreaseParachute() {
        // test parachute
        Tank tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setParachute(0);
        tank.setScore(10);
        tank.increaseParachute();
        assertTrue(tank.getToolBag()[2] == 1);
        // if parachute < 3
        tank.increaseParachute();
        assertEquals(1, tank.getParachute());

        // score not enough
        tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setScore(5);
        tank.setParachute(0);
        tank.increaseParachute();
        assertTrue(tank.getToolBag()[2] == 0);
        tank.increaseParachute();
        assertEquals(0, tank.getParachute());

        // parachute is limit to 3
        tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setScore(10);
        tank.setParachute(3);
        tank.increaseParachute();
        assertTrue(tank.getToolBag()[2] == 1);
        tank.increaseParachute();
        assertEquals(3, tank.getParachute());

        // border case
        tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setScore(5);
        tank.setParachute(3);
        tank.increaseParachute();
        assertTrue(tank.getToolBag()[2] == 0);
        tank.increaseParachute();
        assertEquals(3, tank.getParachute());


        // border case tank parachute cant over 3
        tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setScore(15);
        tank.setParachute(3);
        tank.increaseParachute();
        assertTrue(tank.getToolBag()[2] == 1);
        tank.increaseParachute();
        assertEquals(3, tank.getParachute());
    }

    @Test
    public void testIncreasePower() {
        // ex bullet tool
        Tank tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setCanon(false);
        tank.increasePower();
        assertTrue(tank.isCanon());

        // Test when already having a cannon
        tank.increasePower();
        assertTrue(tank.isCanon());
    }


    @Test
    public void testIncreaseFuel() {
        // fuel add tool
        Tank tank = new Tank('T', 0, 0, 50, true, 0, 50, 100);
        tank.setScore(20);
        tank.increaseFuel();
        assertTrue(tank.getToolBag()[1] == 1);
        tank.increaseFuel();
        assertEquals(250, tank.getFuel());

        // add success
        tank = new Tank('T', 0, 0, 50, true, 0, 50, 100);
        tank.setScore(20);
        tank.setFuel(1);
        tank.increaseFuel();
        assertTrue(tank.getToolBag()[1] == 1);
        tank.increaseFuel();
        assertEquals(201, tank.getFuel());


        tank = new Tank('T', 0, 0, 50, true, 0, 50, 100);
        tank.setScore(5); // Low score, add fuel fails
        tank.increaseFuel();
        assertTrue(tank.getToolBag()[1] == 0);
        tank.increaseFuel();
        assertEquals(100, tank.getFuel());

        tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setFuel(250); // Full fuel, fuel add  fails
        tank.setScore(50);
        tank.increaseFuel();
        assertTrue(tank.getToolBag()[1] == 1);
        tank.increaseFuel();
        assertEquals(250, tank.getFuel());

        // branch
        tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setFuel(0);
        tank.setScore(50);
        tank.increaseFuel();
        assertTrue(tank.getToolBag()[1] == 1);
        tank.increaseFuel();
        assertTrue(tank.getToolBag()[1] == 0);
        assertEquals(200, tank.getFuel());

    }

    @Test
    public void testGainPower() {
        // increase power
        Tank tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setPower(50);
        tank.gainPower(30);
        assertEquals(80, tank.getPower());

        // cant overpass life
        tank.setPower(50);
        tank.setLife(50);
        tank.gainPower(100);
        assertTrue(tank.getPower() == 50);

        tank.setPower(80);
        tank.gainPower(-80); // Decreasing to minimum
        assertEquals(1, tank.getPower());

        tank.gainPower(-10); // Trying to decrease further
        assertEquals(1, tank.getPower());
    }


    @Test
    public void testGainFuel(){
        Tank tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.gainFuel(30);
        assertEquals(130, tank.getFuel());

        tank.gainFuel(200); // Trying to increase further
        assertEquals(250, tank.getFuel());

        tank.gainFuel(-200); // Decreasing fuel
        assertEquals(50, tank.getFuel());

        tank.gainFuel(-60); // Trying to decrease further
        assertEquals(0, tank.getFuel());



    }


    @Test
    public void testGainLife(){
        Tank tank = new Tank('T', 0, 0, 0, true, 0, 50, 100);
        tank.gainLife(30);
        assertEquals(30, tank.getLife());

        tank.gainLife(200); // Trying to increase further
        assertEquals(100, tank.getLife());

        tank.gainLife(-200); // Decreasing life
        assertEquals(0, tank.getLife());

        tank.gainLife(-60); // Trying to decrease further
        assertEquals(0, tank.getLife());
    }

    @Test
    public void testReduceLife(){
        Tank tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.reduceLife(30);
        assertEquals(70, tank.getLife());
    }

    @Test
    public void TestGainParachute(){
        Tank tank = new Tank('T', 0, 0, 100, true, 0, 50, 100);
        tank.setParachute(0);
        tank.gainParachute();
        assertTrue(tank.getParachute() == 1);

        tank.setParachute(3);
        tank.gainParachute();
        assertTrue(tank.getParachute() == 3);
    }




    /* *
     * @Description
     *  explosion test
    */

    @Test
    public void testExplosionUpdate() {
        Explosion explosion1 = new Explosion(100, 100, 50);
        Explosion explosion = new Explosion(100, 100, 50,false);
        delay(100);
        explosion.update();
        assertTrue(explosion.getCurrentRadiusRed() > 0);
        assertTrue(explosion.getCurrentRadiusOrange() > 0);
        assertTrue(explosion.getCurrentRadiusYellow() > 0);
        for(int i = 0; i < 100; i++){
            explosion.update();
            delay(5);
        }
        assertTrue(explosion.getCurrentRadiusRed() == 0);
        assertTrue(explosion.getCurrentRadiusOrange() == 0);
        assertTrue(explosion.getCurrentRadiusYellow() == 0);
    }

    @Test
    public void testExplosionIsFinished() {
        Explosion explosion = new Explosion(100, 100, 50);
        assertFalse(explosion.isFinished()); // Not finished immediately after creation

        // Simulate time passing
        try {
            Thread.sleep(2000); // Sleep for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(explosion.isFinished()); // Finished after 1500 milliseconds
    }

    @Test
    public void testExplosionGettersAndSetters() {
        Explosion explosion = new Explosion(100, 100, 50);
        explosion.setX(50);
        assertEquals(50, explosion.getX());

        explosion.setY(70);
        assertEquals(70, explosion.getY());

        explosion.setMaxRadius(60);
        assertEquals(60, explosion.getMaxRadius());

        explosion.setCurrentRadiusRed(30);
        assertEquals(30, explosion.getCurrentRadiusRed());

        explosion.setCurrentRadiusOrange(20);
        assertEquals(20, explosion.getCurrentRadiusOrange());

        explosion.setCurrentRadiusYellow(10);
        assertEquals(10, explosion.getCurrentRadiusYellow());

        explosion.setGrowthRate(2);
        assertEquals(2, explosion.getGrowthRate());

        explosion.setS(500);
        assertEquals(500, explosion.getS());

        explosion.setTankExplosion(true);
        assertTrue(explosion.isTankExplosion());
    }


    /* *
     * @Description
     * Bullet test
    */

    @Test
    public void testBulletShoot(){
        app.currentPlayerIndex = 0;
        Tank tank = app.tanks.get(app.currentPlayerIndex);
        app.key = ' ';
        app.keyPressed();
        app.keyReleased();
        app.delay(200);
        assertTrue(tank.isShooted(), "Tank should shoot the bullet.");
        assertEquals(1, app.projectiles.size(), "There should be one bullet.");
        // bullet start location should be tank position
        assertEquals(tank.getX(), app.projectiles.get(0).getX(), "Bullet x should be same as tank x.");
        assertEquals(tank.getY(), app.projectiles.get(0).getY(), "Bullet y should be same as tank y.");
    }

    @Test
    public void testBulletConstruction() {
        // bullet construct
        Bullet bullet = new Bullet('A', 100, 200, 45);
        assertTrue(bullet.getSymbol() == 'A');
        assertTrue(bullet.getX() == 100);
        assertTrue(bullet.getY() == 200);
        assertTrue(bullet.getPower() == 50);
        Bullet bullet2 = new Bullet('B', 100, 200, 45, 50, true);
        assertEquals('B', bullet2.getSymbol());
        assertEquals(100, bullet2.getX());
        assertEquals(200, bullet2.getY());
        assertEquals(45, bullet2.getAngle());
        assertEquals(50, bullet2.getPower());
        assertEquals(true, bullet2.isEx());
    }

    @Test
    public void testUpdateBullet() {
        // bullet update localtion
        Bullet bullet = new Bullet('A', 100, 200, 45, 50);
        Wind wind = new Wind();
        for(int i = 0; i < 100; i++){
            bullet.update(wind);
            assertTrue(bullet.getX() != 100);
            assertTrue(bullet.getY() != 200);
        }
    }

    @Test
    public void testUpdateUnActiveBullet() {
        // bullet update localtion
        Bullet bullet = new Bullet('A', 100, 200, 45, 50);
        Wind wind = new Wind();
        bullet.setActive(false);
        float initialX = bullet.getX();
        float initialY = bullet.getY();
        bullet.update(wind);
        // If not active, bullet should not move
        assertEquals(initialX, bullet.getX(), 0.001);
        assertEquals(initialY, bullet.getY(), 0.001);
    }

    @Test
    public void testIsExploded() {
        // when bullet touch the ground will explosion
        app.currentPlayerIndex = 0;
        Tank tank = app.tanks.get(app.currentPlayerIndex);
        app.key = ' ';
        app.keyPressed();
        app.keyReleased();
        app.delay(200);
        Bullet bullet = app.projectiles.get(0);
        bullet.setY(640 - map.getHeightsArray()[0]);
        assertTrue(bullet.isExploded(map));
        bullet.setX(100);
        bullet.setY(640 - map.getHeightsArray()[0] - 1);
        assertFalse(bullet.isExploded(map));
        bullet.setX(900);
        bullet.setY(640 - map.getHeightsArray()[0]);
        assertFalse(bullet.isExploded(map));
        bullet.setX(-1);
        bullet.setY(640 - map.getHeightsArray()[0]);
        assertFalse(bullet.isExploded(map));
    }

    @Test
    public void testOutOfMapX() {
        // bullet out of map  set unactive
        app.currentPlayerIndex = 0;
        Tank tank = app.tanks.get(app.currentPlayerIndex);
        app.key = ' ';
        app.keyPressed();
        app.keyReleased();
        app.delay(200);
        Bullet bullet = app.projectiles.get(0);
        bullet.setX(1000);
        assertTrue(bullet.isOutOfMap());
        bullet.setX(864-1);
        assertFalse(bullet.isOutOfMap());
        bullet.setX(-1);
        assertTrue(bullet.isOutOfMap());
    }

    @Test
    public void testOutOfMapY() {
        // bullet out of map  set unactive
        app.currentPlayerIndex = 0;
        Tank tank = app.tanks.get(app.currentPlayerIndex);
        app.key = ' ';
        app.keyPressed();
        app.keyReleased();
        app.delay(200);
        Bullet bullet = app.projectiles.get(0);
        bullet.setY(950);
        assertTrue(bullet.isOutOfMap());
        bullet.setY(640-1);
        assertFalse(bullet.isOutOfMap());
        bullet.setY(-1);
        assertTrue(bullet.isOutOfMap());
    }

    @Test
    public void testAngleConvert() {
        // angle convert
        assertTrue(Bullet.angleConvert(45) == 45);
        assertTrue(Bullet.angleConvert(100) == 170);
    }

    /* *
     * @Description
     * Test the wind
    */
    @Test
    public void testConstructor() {
        Wind wind = new Wind();
        assertNotNull(wind);
    }

    @Test
    public void testUpdate() {
        // wind update wind strength should be between -35 and 35
        Wind wind = new Wind();
        for(int i = 0 ; i < 100; i++){
            wind.update();
            int strength = wind.getStrength();
            // should change everytime
            assertTrue(strength >= -35 && strength <= 35);
        }
    }

    @Test
    public void testGetDirection() {
        // get wind direction
        Wind wind = new Wind();
        int direction = wind.getDirection();
        assertTrue(direction == -1 || direction == 1);
    }

    @Test
    public void testSetDirection() {
        // set wind
        Wind wind = new Wind();
        wind.setDirection(1);
        assertEquals(1, wind.getDirection());
    }

    /* *
     * @Description
     * map test
    */
    @Test
    public void mapTestConstructor() {
        List<List<Character>> grid = new ArrayList<>();
        int[][] terrain = new int[10][10];
        ArrayList<Position> playerPositions = new ArrayList<>();
        ArrayList<Position> treePositions = new ArrayList<>();
        Map map = new Map(grid, terrain, playerPositions, treePositions);
        assertNotNull(map);
    }

    // update map terrain
    @Test
    public void testUpdateTerrain() {
        // Test with valid input
        int heightBefore = map.getHeightsArray()[5];
        map.updateTerrain(5, 50);
        assertFalse(map.getHeightsArray()[5]==heightBefore);

        // Test with invalid column index
        map.updateTerrain(-1, 5);
        // Ensure it doesn't throw an exception
    }



    @Test
    public void testSmoothData() {
        // smooth the data
        int[] data = {1, 2, 3, 4, 5};
        int[] smoothedData = map.smoothData(data, 3);
        assertEquals(smoothedData.length, 5);
    }

    @Test
    public void testInterpolateArray() {
        int[] originalArray = {1, 2, 3, 4, 5};
        int[] interpolatedArray = map.interpolateArray(originalArray, 10);
        assertEquals(interpolatedArray.length, 10);
    }































    public void testTankTowerRotate(int initialAngle, int expectedAngleChange, int keyCode, String message) {
        app.currentPlayerIndex = 0;
        Tank tank = app.tanks.get(app.currentPlayerIndex);
        tank.setAngle(initialAngle);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        app.delay(200);
        assertEquals(initialAngle + expectedAngleChange, tank.getAngle(), message);
    }

    private void testTankMovement(Tank tank,int keyEvent, int expectedX, String message) {
        robot.keyPress(keyEvent);
        robot.keyRelease(keyEvent);
        app.delay(200);
        assertEquals(expectedX, tank.getX(), message);
    }

    private void testTankMovementHill(Tank tank,int keyEvent, int expectedX,int expectedY, String message) {
        robot.keyPress(keyEvent);
        robot.keyRelease(keyEvent);
        app.delay(200);
        assertEquals(expectedX, tank.getX(), message);
        assertEquals(expectedY, tank.getY(), message);
    }


}
