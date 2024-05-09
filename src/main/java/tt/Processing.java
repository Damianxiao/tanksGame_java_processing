package tt;

import processing.core.*;
import processing.data.JSONObject;
import tt.map.Map;
import tt.map.MapLoader;
import tt.map.Position;
import tt.player.Tank;

import java.util.ArrayList;
import java.util.List;

public class Processing extends PApplet {
    private Map map;
    private ArrayList<Tank> tanks = new ArrayList<>();
    private Tank currentTank;
    private int currentPlayerIndex = 0;
    private int lastSwitchTime = 0;
    private boolean showArrow = true;
    private int selectedLevel = -1;
    private String[] levelNames = {"Level1", "Level2", "Level3"}; // 关卡名称数组
    private JSONObject config;
    private final String levelPath = "src/main/resources/level/";
    private final String picPath = "src/main/resources/pic/";

    public void setup() {
        smooth();
        noStroke();
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
//            startGame();
            drawGame();
            for (int i = 0; i < tanks.size(); i++) {
                Tank tank = tanks.get(i);
                if (i == currentPlayerIndex && showArrow) {
                    fill(0);
                    triangle(tank.getX() - 10, tank.getY()-60, tank.getX() + 10, tank.getY()-60,tank.getX(),tank.getY() - 30);
                }
                drawHUD();
            }
        }
        if (millis() - lastSwitchTime < 5000) {
            showArrow = true;
        } else {
            showArrow = false;
        }
//        rect(tank.getX() * 10, tank.getY() * 10, 10, 10);
//        drawTankAngle(tank.getAngle());
//        rotateAnimation();
    }


    public void clearStartScreen(){
        background(255);
    }

//    public void drawTankAngle(double angle) {
//        float tankX = tank.getX() + 16;
//        float tankY = tank.getY() + 16;
//
//        beginShape();
//        vertex(tankX, tankY);
//        vertex(tankX - 16, tankY - 16);
//        vertex(tankX + 16, tankY - 16);
//        endShape();
//
//        float angleMarkerX = tankX + cos(radians((float) angle)) * 16;
//        float angleMarkerY = tankY + sin(radians((float) angle)) * 16;
//        line(tankX, tankY, angleMarkerX, angleMarkerY);
//    }

//    private void rotateAnimation() {
//        beginShape();
//        vertex(tank.getX() + 16, tank.getY() + 16);
//        vertex(tank.getX() - 16, tank.getY() - 16);
//        vertex(tank.getX() + 16, tank.getY() - 16);
//        endShape();
//
//        float angleMarkerX = (float) (tank.getX() + 16 * Math.cos(Math.toRadians(tank.getAngle())));
//        float angleMarkerY = (float) (tank.getY() + 16 * Math.sin(Math.toRadians(tank.getAngle())));
//        line(tank.getX(), tank.getY(), angleMarkerX, angleMarkerY);
//    }

    public void keyPressed() {
        if(selectedLevel == -1){
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
        currentTank = tanks.get(currentPlayerIndex);
        if (keyCode == LEFT) {
            int diff = map.getHeightsArray()[currentTank.getX()] - map.getHeightsArray()[currentTank.getX() - 1];
            if(diff <= 0){
                currentTank.move(-1, diff);
                map.getPlayerPositions().get(currentPlayerIndex).setY(currentTank.getY());
                map.getPlayerPositions().get(currentPlayerIndex).setX(currentTank.getX());
            } else if (diff > 0) {
                currentTank.move(-1, diff);
                map.getPlayerPositions().get(currentPlayerIndex).setY(currentTank.getY());
                map.getPlayerPositions().get(currentPlayerIndex).setX(currentTank.getX());
            }
        } else if (keyCode == RIGHT) {
            int diff = map.getHeightsArray()[currentTank.getX()] - map.getHeightsArray()[currentTank.getX() + 1];
            if(diff <= 0){
                currentTank.move(1, diff);
                map.getPlayerPositions().get(currentPlayerIndex).setY(currentTank.getY());
                map.getPlayerPositions().get(currentPlayerIndex).setX(currentTank.getX());
            } else if (diff > 0) {
                currentTank.move(1, diff);
                map.getPlayerPositions().get(currentPlayerIndex).setY(currentTank.getY());
                map.getPlayerPositions().get(currentPlayerIndex).setX(currentTank.getX());
            }
        } else if (keyCode == UP) {
            currentTank.rotateTower(-3);
        } else if (keyCode == DOWN) {
            currentTank.rotateTower(3);
        } else if (key == 'W') {
//            power += 5;
        } else if (key == 'S') {
//            power -= 5;
        } else if (key == ' ') {
//            shoot();
            currentPlayerIndex = (currentPlayerIndex + 1) % tanks.size();
            lastSwitchTime = millis();
            drawHUD();
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
        map.setBackgroundFileName(backgroundFileName);
        map.setTerrainColor(terrainColor);
        map.setPlayerNames(playerNames);
        map.setTreeFileName(treeFileName);
        // init tank
        for(Position p:map.getPlayerPositions()){
            Tank tank = new Tank(p.getSymbol().charAt(0),p.getX() ,p.getY() ,100,true,0,50,100);
            tanks.add(tank);
        }
        int heights[] = map.heightsArray(map.getTerrain());
        //extend heights array
        int extendedHeights[] = map.interpolateArray(heights, 28*32);
        // smooth heights
        int smoothHeights[] = map.smoothData(extendedHeights, 32);
        smoothHeights = map.smoothData(smoothHeights, 32);
        smoothHeights = map.smoothData(smoothHeights, 32);
        map.setHeightsArray(smoothHeights);
    }
    // draw HUD
    public void drawHUD() {
        fill(255,255,255);
        rect(0, 0, 864, 60);
        textSize(20);
        fill(0);
        text("Player " + tanks.get(currentPlayerIndex).getSymbol() + "'s turn!", 100, 10);
        text("Health: " + tanks.get(currentPlayerIndex).getLife(), 80, 40);
        text("Power: " + tanks.get(currentPlayerIndex).getPower(), 180, 40);
        text("Fuel: " + tanks.get(currentPlayerIndex).getPower(), 280, 40);
    }

    public void drawGame(){
        PImage bgImage = loadImage(picPath + map.getBackgroundFileName());
        background(bgImage);
        drawMap(map.getTerrain(), map.getTerrainColor(),map.getHeightsArray());
        drawPlayers(map.getPlayerPositions(),map.getPlayerNames());
        //draw tree
        String treeFileName = map.getTreeFileName();
        if (treeFileName != null) {
            PImage treeImage = loadImage(picPath + treeFileName);
            for (Position treePos : map.getTreePositions()) {
                image(treeImage, treePos.getX(), treePos.getY() );
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
            ellipse(playerPos.getX() , playerPos.getY() , 40, 20);
//            rect(playerPos.getX() , playerPos.getY() , 40, 20);
            // draw tower
            pushMatrix();
            translate(playerPos.getX() , playerPos.getY() );
            rotate(0);
            fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
            rect(-2, -20, 4, 25);
            popMatrix();
        }
    }
}
