import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import tt.Main;
import tt.Processing;
import tt.map.Map;
import tt.map.MapLoader;
import tt.map.Position;
import tt.player.Bullet;
import tt.player.Explosion;
import tt.player.Tank;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
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
    private static Processing app;

    @BeforeAll
    public static void init(){
        app = new Processing();
        app.loop();
        app.delay(50);
        app.noLoop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        MapLoader mapLoader = new MapLoader();
        map = MapLoader.loadMap(levelPath + "testLevel.txt");
        // init tank
        for (Position p : map.getPlayerPositions()) {
            Tank tank = new Tank(p.getSymbol().charAt(0), p.getX(), p.getY(), 100, true, 0, 50, 250);
            tanks.add(tank);
            p.setAngle(tank.getAngle());
        }

    }

    // maploader
    @Test
    public void mapLoadTest() {

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
    }

    @Test
    public void testTankMove() throws AWTException {
        // tank move
        app.keyCode = Processing.LEFT;
        app.keyPressed();

        



    }


}
