package tt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;
import tt.map.Map;
import tt.map.MapLoader;
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
//        robot.keyPress(KeyEvent.VK_1);
//        robot.keyRelease(KeyEvent.VK_1);
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

    @Test
    public void testTankMove() throws AWTException {
        // tank move
        app.currentPlayerIndex = 0;
        app.tanks.get(app.currentPlayerIndex).setX(400);
        Tank tank = app.tanks.get(app.currentPlayerIndex);
        // Test right move
        testTankMovement(KeyEvent.VK_RIGHT, tank.getX() + 1, "Tank should move right by one unit.");

        // Test left move
        testTankMovement(KeyEvent.VK_LEFT, tank.getX() - 1, "Tank should move left by one unit.");

        app.tanks.get(app.currentPlayerIndex).setX(0);
        // Test border when tank x = 0
        testTankMovement(KeyEvent.VK_LEFT, 0, "Tank can't left move when x=0.");

        app.tanks.get(app.currentPlayerIndex).setX(864);
        // Test border when tank x = 864
        testTankMovement(KeyEvent.VK_RIGHT, 864, "Tank can't right move when x=864.");
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

    private void testTankMovement(int keyEvent, int expectedX, String message) {
        Tank tank = app.tanks.get(app.currentPlayerIndex);
        robot.keyPress(keyEvent);
        robot.keyRelease(keyEvent);
        app.delay(200);
        tank = app.tanks.get(app.currentPlayerIndex);
        assertEquals(expectedX, tank.getX(), message);
    }


}
