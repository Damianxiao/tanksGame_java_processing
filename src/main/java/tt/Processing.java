package tt;


import processing.core.*;
import processing.data.JSONObject;
import tt.map.Map;
import tt.map.MapLoader;
import tt.map.Position;
import tt.player.Bullet;
import tt.player.Explosion;
import tt.player.Tank;
import java.util.ArrayList;
import java.util.List;

import static tt.player.Bullet.angleConvert;

public class Processing extends PApplet {
    private Map map;
    private ArrayList<Tank> tanks = new ArrayList<>();
    private Tank currentTank;
    private int currentPlayerIndex = 0;
    private int lastSwitchTime = 0;
    private boolean showArrow = true;
    private int selectedLevel = -1;
    private JSONObject config;
    private final String levelPath = "src/main/resources/level/";
    private final String picPath = "src/main/resources/pic/";
    private ArrayList<Bullet> projectiles = new ArrayList<>();
    private Explosion  explosion;
    private boolean isExpFinished = true;

    public void setup() {
        smooth();
        noStroke();
//        frameRate(60);
    }

    @Override
    public void settings() {
        size(864, 640);
        config = loadJSONObject(levelPath + "config.json");
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
            // startGame();
            drawGame();
            for (int i = 0; i < tanks.size(); i++) {
                Tank tank = tanks.get(i);
                if (i == currentPlayerIndex && showArrow) {
                    fill(0);
                    triangle(tank.getX() , tank.getY() - 60, tank.getX() + 20, tank.getY() - 60, tank.getX()+10,
                            tank.getY() - 30);
                }
                drawHUD();
            }
        }
        if (millis() - lastSwitchTime < 5000) {
            showArrow = true;
        } else {
            showArrow = false;
        }
    }

    public void clearStartScreen() {
        background(255);
    }

    public void keyPressed() {
        if (selectedLevel == -1) {
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
        } else if (selectedLevel != -1) {
            currentTank = tanks.get(currentPlayerIndex);
            if (keyCode == LEFT && currentTank.getX()>0) {
                int diff = map.getHeightsArray()[currentTank.getX()] - map.getHeightsArray()[currentTank.getX() - 1];
                if (diff <= 0) {
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
                if (diff <= 0) {
                    currentTank.move(1, diff);
                    map.getPlayerPositions().get(currentPlayerIndex).setY(currentTank.getY());
                    map.getPlayerPositions().get(currentPlayerIndex).setX(currentTank.getX());
                } else if (diff > 0) {
                    currentTank.move(1, diff);
                    map.getPlayerPositions().get(currentPlayerIndex).setY(currentTank.getY());
                    map.getPlayerPositions().get(currentPlayerIndex).setX(currentTank.getX());
                }
            } else if (keyCode == UP) {
                currentTank.rotateTower(3);
                for(Position p : map.getPlayerPositions()){
                    if(p.getSymbol().charAt(0) == currentTank.getSymbol()){
                        p.setAngle(currentTank.getAngle());
                    }
                }
            } else if (keyCode == DOWN) {
                currentTank.rotateTower(-3);
                for(Position p : map.getPlayerPositions()){
                    if(p.getSymbol().charAt(0) == currentTank.getSymbol()){
                        p.setAngle(currentTank.getAngle());
                    }
                }
            } else if (key == 'W') {
                // power += 5;
            } else if (key == 'S') {
                // power -= 5;
            } else if (key == ' ') {
                shoot();
                lastSwitchTime = millis();
                drawHUD();
            }
        }
    }

    public void shoot() {
        float turretAngle = currentTank.getAngle();
        Bullet bullet = new Bullet(currentTank.getSymbol(), currentTank.getX(), currentTank.getY(), angleConvert(currentTank.getAngle()), currentTank.getPower());
        projectiles.add(bullet);
    }


    public void startGame() {
        // get config for selected level
        JSONObject level = config.getJSONArray("levels").getJSONObject(selectedLevel - 1);
        String levelFileName = level.getString("layout");
        String backgroundFileName = level.getString("background");
        String terrianColorStr = level.getString("foreground-color");
        String[] terrainColorArr = terrianColorStr.split(",");
        JSONObject playerNames = config.getJSONObject("player_color");
        int terrainColor = color(Integer.parseInt(terrainColorArr[0]), Integer.parseInt(terrainColorArr[1]),
                Integer.parseInt(terrainColorArr[2]));
        String treeFileName = level.getString("trees");
        String parachuteFileName = level.getString("parachute");
        // load map
        map = MapLoader.loadMap(levelPath + levelFileName);
        map.setBackgroundFileName(backgroundFileName);
        map.setTerrainColor(terrainColor);
        map.setPlayerNames(playerNames);
        map.setTreeFileName(treeFileName);
        map.setParachuteFileName(parachuteFileName);
        int heights[] = map.heightsArray(map.getTerrain());
        // extend heights array
        int extendedHeights[] = map.interpolateArray(heights, 28 * 32);
        // smooth heights
        int smoothHeights[] = map.smoothData(extendedHeights, 32);
        smoothHeights = map.smoothData(smoothHeights, 32);
        smoothHeights = map.smoothData(smoothHeights, 32);
        map.setHeightsArray(smoothHeights);
        // set player y position
        for(Position p : map.getPlayerPositions()){
            p.setY(640-smoothHeights[p.getX()]-16);
        }
        // set the tree position
        for(Position p : map.getTreePositions()){
            p.setY(640-smoothHeights[p.getX()]);
        }

        // init tank
        for (Position p : map.getPlayerPositions()) {
            Tank tank = new Tank(p.getSymbol().charAt(0), p.getX(), p.getY(), 100, true, 0, 50, 100);
            tanks.add(tank);
            p.setAngle(tank.getAngle());
        }

    }
    // draw bullet
    public void drawBullet() {
        for (Bullet bullet : projectiles) {
            if(bullet.isActive()){
                fill(0);
                ellipse(bullet.getX()+12, bullet.getY(), 4, 4);
                bullet.update();
                // draw explosion
                if (bullet.isExploded(map)) {
                    int col = floor(bullet.getX());
                    int power = bullet.getPower();
                    // update terrain
                    map.updateTerrain(col, power);
                    // explosion effect
                    isExpFinished =false;
                    explosion = new Explosion(bullet.getX(),bullet.getY(),bullet.getPower());
                    // check if bullet hit tank
                    if(checkTankCollision(col, power)){
                        char symbol = bullet.getSymbol();
                        for(Tank tank : tanks){
                            // if terrain collision tank use parachute on the ground
                            if(tank.getSymbol() == symbol){
                               // gain score
                            }
                        }
                    }
                    // next round
//                    currentPlayerIndex = (currentPlayerIndex + 1) % tanks.size();
                    bullet.setActive(false);
                }
                // next round
//                if(bullet.isOutOfMap()) {
//                    currentPlayerIndex = (currentPlayerIndex + 1) % tanks.size();
//                    bullet.setActive(false);
//                }
            }

        }
    }

    public void drawExplosion(){
        if(!isExpFinished){
            explosion.update();
            float x =explosion.getX();
            float y =explosion.getY();
            float currentRadiusRed = explosion.getCurrentRadiusRed();
            float currentRadiusOrange = explosion.getCurrentRadiusOrange();
            float currentRadiusYellow = explosion.getCurrentRadiusYellow();
            // Red circle
            fill(255, 0, 0);
            ellipse(x, y, currentRadiusRed * 2, currentRadiusRed * 2);
            // Orange circle
            fill(255, 165, 0);
            ellipse(x, y, currentRadiusOrange * 2, currentRadiusOrange * 2);
            // Yellow circle
            fill(255, 255, 0);
            ellipse(x, y, currentRadiusYellow * 2, currentRadiusYellow * 2);
        }
    }

    public void deployParachute(Tank tank,int col) {
        PImage parachuteImage = loadImage(picPath+map.getParachuteFileName());
            if (!tank.hasParachute()) {
                if(tank.getY() < 640-map.getHeightsArray()[col]+16){
                    tank.move(0,5);
                    drawPlayers(map.getPlayerPositions(), map.getPlayerNames());
                    tank.reduceLife(5);
                    for(Position p : map.getPlayerPositions()){
                        if(p.getSymbol().charAt(0) == tank.getSymbol()){
                            p.setY(tank.getY());
                        }
                    }
                }
            }else if(tank.hasParachute() && 640-map.getHeightsArray()[col] - tank.getY() > 32){
                // tip to remind parachute
                image(parachuteImage, tank.getX() - parachuteImage.width / 2 +12, tank.getY() - parachuteImage.height / 2 -20);
                if(tank.getY() < 640-map.getHeightsArray()[col]-32){
                    tank.move(0,1);
                    drawPlayers(map.getPlayerPositions(), map.getPlayerNames());
                    for(Position p : map.getPlayerPositions()){
                        if(p.getSymbol().charAt(0) == tank.getSymbol()){
                            p.setY(tank.getY());
                        }
                        if(tank.getY()>= 640-map.getHeightsArray()[col]){
                            tank.setParachute(tank.getParachute()-1);
                        }
                    }
                }

            }
    }
    // tank get hit reduce life
    public boolean checkTankCollision(int col,int power){
        int powerRange = power/2;
        for(Tank tank : tanks){
            // in the range of explosion
            int dist = (int) dist(tank.getX(),tank.getY(),col,640-map.getHeightsArray()[col]);
            if(dist <= powerRange){
                if(dist < powerRange/2)
                    tank.reduceLife(power);
                else if(dist >= powerRange/2 && dist < powerRange){
                    tank.reduceLife(power/2);
                } else{
                    tank.reduceLife(power/4);
                }
            }
        }
        return false;
    }

    // draw HUD
    public void drawHUD() {
        fill(255,255,255);
        rect(0, 0, 864, 60);
        textSize(20);
        // yellow
        fill(255,153,18);
        text("Player " + tanks.get(currentPlayerIndex).getSymbol() + "'s turn!", 100, 10);
        textSize(20);
        text("Health: " + tanks.get(currentPlayerIndex).getLife(), 80, 40);
        text("Power: " + tanks.get(currentPlayerIndex).getPower(), 200, 40);
        text("Fuel: " + tanks.get(currentPlayerIndex).getFuel(), 300, 40);
    }

    public void drawGame() {
        PImage bgImage = loadImage(picPath + map.getBackgroundFileName());
        PImage parachuteImage = loadImage(picPath + map.getParachuteFileName());
        background(bgImage);
        drawHUD();
        drawMap(map.getTerrain(), map.getTerrainColor(), map.getHeightsArray());
        drawPlayers(map.getPlayerPositions(), map.getPlayerNames());
        // draw tree
        String treeFileName = map.getTreeFileName();
        if (treeFileName != null) {
            PImage treeImage = loadImage(picPath + treeFileName);
            for (Position treePos : map.getTreePositions()) {
                if (treePos.getY() < 640 - map.getHeightsArray()[treePos.getX()]) {
                    treePos.setY(treePos.getY() + 1);
                    image(treeImage, treePos.getX(), treePos.getY()-32);
                } else{
                    image(treeImage, treePos.getX(), treePos.getY()-32);
                }
            }
        }
        drawBullet();
        drawExplosion();
        // draw collision fall
        for(Tank tank:tanks){
            if(tank.getY()<640-map.getHeightsArray()[tank.getX()]){
                deployParachute(tank,tank.getX());
            }
        }
    }


    // draw terrain
    public void drawMap(int[][] terrain, int terrainColor, int smoothHeights[]) {
        for (int y = 0; y < smoothHeights.length; y++) {
            fill(terrainColor);
            rect(y, 640-smoothHeights[y], 1, smoothHeights[y]);
        }
    }

    // draw players
    public void drawPlayers(ArrayList<Position> playerPositions, JSONObject playerNames) {
        String[] rgb;
        for (Position playerPos : playerPositions) {
            rgb = playerNames.getString(playerPos.getSymbol()).split(",");
            fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
//            ellipse(playerPos.getX(), playerPos.getY(), 40, 20);
            // tank body
            rect(playerPos.getX() , playerPos.getY() , 25, 10);
            //tower body
            rect(playerPos.getX()+2 , playerPos.getY()-5 , 20, 5);
             //wheel
            ellipse(playerPos.getX()+6 , playerPos.getY()+12, 10, 10);
            ellipse(playerPos.getX()+19, playerPos.getY()+12, 10, 10);
            // draw tower
            pushMatrix();
            translate(playerPos.getX()+15 , playerPos.getY());
//            rotate(radians(90));
            rotate(radians(playerPos.getAngle()));
            fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
//            ellipse(3, -15, 5, 20);
            rect(0, 0, 5, 20);
//            ellipse(0, -10, 5, 25);
            popMatrix();
        }
    }
}
