package tt;

import processing.core.*;
import processing.data.JSONObject;
import tt.map.Map;
import tt.map.MapLoader;
import tt.map.Position;
import tt.player.Tank;

import java.util.ArrayList;

public class Processing extends PApplet {
    private Map map;
    private Tank tank;
    private int selectedLevel = -1;
    private String[] levelNames = {"Level1", "Level2", "Level3"}; // 关卡名称数组
    private JSONObject config;
    private final String levelPath = "src/main/resources/level/";
    private final String picPath = "src/main/resources/pic/";

    public void setup() {
        smooth();
        noStroke();
        map.getPlayerPositions();
        tank = new Tank('A', 0, 0, 100, true, 0);
    }

    @Override
    public void settings() {
        size(864, 640);
        config = loadJSONObject(levelPath+"config.json");

    }


    // draw map
    public void draw() {
        if (selectedLevel == -1) {
            background(255);
            fill(0);
            textAlign(CENTER, CENTER);
            textSize(40);
            text("Tanks", width / 2, 50);
            textSize(30);
            text("Press Number Choose a level:", width / 2, 150);
            textSize(25);
            for (int i = 0; i < config.getJSONArray("levels").size(); i++) {
                JSONObject level = config.getJSONArray("levels").getJSONObject(i);
                text((i + 1) + ". Level " + (i + 1), width / 2, 300 + i * 30);
            }
        } else {
            clearStartScreen();
            startGame();
        }

//        rect(tank.getX() * 10, tank.getY() * 10, 10, 10);
//        drawTankAngle(tank.getAngle());
//        rotateAnimation();
    }


    public void clearStartScreen(){
        background(255);
    }

    public void drawTankAngle(double angle) {
        float tankX = tank.getX() + 16;
        float tankY = tank.getY() + 16;

        beginShape();
        vertex(tankX, tankY);
        vertex(tankX - 16, tankY - 16);
        vertex(tankX + 16, tankY - 16);
        endShape();

        float angleMarkerX = tankX + cos(radians((float) angle)) * 16;
        float angleMarkerY = tankY + sin(radians((float) angle)) * 16;
        line(tankX, tankY, angleMarkerX, angleMarkerY);
    }

    private void rotateAnimation() {
        beginShape();
        vertex(tank.getX() + 16, tank.getY() + 16);
        vertex(tank.getX() - 16, tank.getY() - 16);
        vertex(tank.getX() + 16, tank.getY() - 16);
        endShape();

        float angleMarkerX = (float) (tank.getX() + 16 * Math.cos(Math.toRadians(tank.getAngle())));
        float angleMarkerY = (float) (tank.getY() + 16 * Math.sin(Math.toRadians(tank.getAngle())));
        line(tank.getX(), tank.getY(), angleMarkerX, angleMarkerY);
    }

    public void keyPressed() {
        if (key == '1') {
            selectedLevel = 1;
            startGame();
        } else if (key == '2') {
            selectedLevel = 2;
            startGame();
        } else if (key == '3') {
            selectedLevel = 3;
            startGame();
        }
    }

    public void startGame() {
        // get config for selected level
        JSONObject level = config.getJSONArray("levels").getJSONObject(selectedLevel - 1);
        String levelFileName = level.getString("layout");
        String backgroundFileName = level.getString("background");
        String terrianColorStr = level.getString("foreground-color");
        String[] terrainColorArr = terrianColorStr.split(",");
        JSONObject playerNames = config.getJSONObject("player_color");

        int terrainColor = color(Integer.parseInt(terrainColorArr[0]), Integer.parseInt(terrainColorArr[1]), Integer.parseInt(terrainColorArr[2]));

        String treeFileName = level.getString("trees");

        // load map
        map = MapLoader.loadMap(levelPath + levelFileName);
        int heights[] = map.heightsArray(map.getTerrain());
        //extend heights array
        int extendedHeights[] = map.interpolateArray(heights, 28*32);
        // smooth heights
        int smoothHeights[] = map.smoothData(extendedHeights, 32);
        smoothHeights = map.smoothData(smoothHeights, 32);
        smoothHeights = map.smoothData(smoothHeights, 32);
        PImage bgImage = loadImage(picPath + backgroundFileName);
        background(bgImage);
        drawMap(map.getTerrain(), terrainColor,smoothHeights);
        drawPlayers(map.getPlayerPositions(),playerNames);
        //draw tree
        if (treeFileName != null) {
            PImage treeImage = loadImage(picPath + treeFileName);
            for (Position treePos : map.getTreePositions()) {
                image(treeImage, treePos.getX() * 25, treePos.getY() * 32);
            }
        }
    }
    // draw terrain
    public void drawMap(int[][] terrain, int terrainColor,int smoothHeights[]) {
        for(int y=0;y<smoothHeights.length;y++){
            fill(terrainColor);
            rect(y , 640 - smoothHeights[y], 1, smoothHeights[y]);
        }
    }

    // draw players
    public void drawPlayers(ArrayList<Position> playerPositions,JSONObject playerNames) {
        String[] rgb;
        for(Position playerPos : playerPositions){
            rgb = playerNames.getString(playerPos.getSymbol()).split(",");
            fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

            ellipse(playerPos.getX() * 32 + 16, playerPos.getY() * 32 + 16, 30, 20);
            // draw tower
            pushMatrix();
            translate(playerPos.getX() * 32 + 16, playerPos.getY() * 32 + 16);
            rotate(0);
            fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
            rect(-2, -20, 4, 25);
            popMatrix();
        }
    }
}
