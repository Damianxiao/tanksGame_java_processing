package tt;


import processing.core.*;
import processing.data.JSONObject;
import tt.map.Map;
import tt.map.MapLoader;
import tt.map.Position;
import tt.map.Wind;
import tt.player.Bullet;
import tt.player.Explosion;
import tt.player.Tank;

import java.util.ArrayList;
import java.util.List;

import static tt.player.Bullet.angleConvert;

public class Processing extends PApplet {
    public Map map;
    public ArrayList<Tank> tanks = new ArrayList<>();
    private Tank currentTank;
    public int currentPlayerIndex = 0;
    private int lastSwitchTime = 0;
    private boolean showArrow = true;
    private int selectedLevel = -1;
    private JSONObject config;
    private final String levelPath = "src/main/resources/level/";
    private final String picPath = "src/main/resources/pic/";
    ArrayList<Bullet> projectiles = new ArrayList<>();
    private Explosion explosion;
    private Explosion tankExplosion;
    private boolean isExpFinished = true;
    private boolean isTankExpFinished = true;
    private MapLoader mapLoader = new MapLoader();
    public PImage background = null;
    public PImage tree = null;
    public PImage fuel = null;
    public PImage wind = null;
    public PImage gas = null;
    public PImage repair = null;
    public PImage ex = null;
    public PImage parachuteKit = null;
    private boolean gameEnd = false;
    private boolean gameRestarted = false;


    public void setup() {
        smooth();
        noStroke();
        frameRate(60);
    }

    @Override
    public void settings() {
        size(864, 640);
        config = loadJSONObject(levelPath + "config.json");
    }

    // draw map
    public void draw() {
        if (selectedLevel == -1) {
//            PImage bgImage = loadImage(picPath + "tankBg.png");
//            bgImage.resize(864, 640);
//            background(bgImage);
            background(0);
            fill(255, 255, 255);
//            stroke(255,255,0,100);
            textAlign(CENTER, CENTER);
            textSize(80);
            text("Tanks", width / 2, 50);
            textSize(40);
            text("Press Number Choose a Level:", width / 2, 150);
            textSize(40);
//            noStroke();
            for (int i = 0; i < config.getJSONArray("levels").size(); i++) {
                JSONObject level = config.getJSONArray("levels").getJSONObject(i);
                text((i + 1) + " " + level.get("name"), width / 2, 300 + i * 50);
            }
        } else {
            clearStartScreen();
            // startGame();
            drawGame();
            for (int i = 0; i < tanks.size(); i++) {
                Tank tank = tanks.get(i);
                if (i == currentPlayerIndex && showArrow && tank.isAlive()) {
                    fill(0);
                    triangle(tank.getX(), tank.getY() - 60, tank.getX() + 20, tank.getY() - 60, tank.getX() + 10,
                            tank.getY() - 30);
                }
                drawHUD();
            }
            if (gameEnd) {
                if (!gameRestarted) {
                    showWinner();
                    showFinalScores();
                    delay(2000);
                    gameRestarted = true;
                }
            }
        }


        if (millis() - lastSwitchTime < 5000) {
            showArrow = true;
        } else {
            showArrow = false;
        }
    }

    // check if only one tank is alive
    public int suriveTanks() {
        int count = 0;
        for (Tank tank : tanks) {
            if (tank.isAlive()) {
                count++;
            }
        }
        return count;
    }

    public void showFinalScores() {
        List<Tank> sortedPlayers = getSortedPlayersByScore();
        int yOffset = 50;
        String[] rgb;
        JSONObject playerNames = map.getPlayerNames();
        for (Tank player : sortedPlayers) {
            rgb = playerNames.getString(String.valueOf(player.getSymbol())).split(",");
            fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
            textSize(30);
            text(player.getSymbol() + ": " + player.getScore(), 450, 200 + yOffset);
            yOffset += 30;
            delay(700);
        }
    }


    public void showWinner() {
        // get winner
        String[] rgb;
        JSONObject playerNames = map.getPlayerNames();
        Tank winner = getHighestScoringPlayer();
        rgb = playerNames.getString(String.valueOf(winner.getSymbol())).split(",");
        fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]), 100));
        rectMode(CENTER);
        rect(width / 2, height / 2, 600, 400);
        // get player color
        textSize(40);
//        textAlign(CENTER, CENTER);
        fill(255, 255, 255);
        text("Player " + winner.getSymbol() + " wins!", 560, 200);
        rectMode(CORNER);
    }


    public Tank getHighestScoringPlayer() {
        Tank highestScorer = tanks.get(0);
        for (Tank player : tanks) {
            if (player.getScore() > highestScorer.getScore()) {
                highestScorer = player;
            }
        }
        return highestScorer;
    }

    public List<Tank> getSortedPlayersByScore() {
        List<Tank> sortedPlayers = new ArrayList<>(tanks);
        sortedPlayers.sort((p1, p2) -> p2.getScore() - p1.getScore());
        return sortedPlayers;
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
            if (keyCode == LEFT && currentTank.getX() > 0 && currentTank.getFuel() > 0 && !currentTank.isShooted()) {
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
            } else if (keyCode == RIGHT && currentTank.getX() < 864 && currentTank.getFuel() > 0 && !currentTank.isShooted()) {
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
            } else if (keyCode == UP && !currentTank.isShooted()) {
                currentTank.rotateTower(3);
                for (Position p : map.getPlayerPositions()) {
                    if (p.getSymbol().charAt(0) == currentTank.getSymbol()) {
                        p.setAngle(currentTank.getAngle());
                    }
                }
            } else if (keyCode == DOWN && !currentTank.isShooted()) {
                currentTank.rotateTower(-3);
                for (Position p : map.getPlayerPositions()) {
                    if (p.getSymbol().charAt(0) == currentTank.getSymbol()) {
                        p.setAngle(currentTank.getAngle());
                    }
                }
            } else if (key == 'w' && !currentTank.isShooted()) {
                currentTank.gainPower(1);
            } else if (key == 's' && !currentTank.isShooted()) {
                currentTank.gainPower(-1);
            } else if (key == ' ') {
                if (!currentTank.isShooted()) {
                    shoot();
                    currentTank.setShooted(true);

                }
                drawHUD();
            } else if (key == 't' && !currentTank.isShooted()) {
                currentTank.repair();
            } else if (key == 'f' && !currentTank.isShooted()) {
                currentTank.increaseFuel();
            } else if (key == 'p' && !currentTank.isShooted()) {
                currentTank.increaseParachute();
            } else if (key == 'x' && !currentTank.isShooted()) {
                currentTank.increasePower();
            } else if (key == 'r' || gameRestarted) {
                restartGame();
            }
        }
    }

    private void restartGame() {
        gameEnd = false;
        gameRestarted = false;
        selectedLevel = -1;
        tanks.clear();
        currentPlayerIndex = 0;
    }

    public void shoot() {
        if (currentTank.isCanon()) {
            Bullet bullet = new Bullet(currentTank.getSymbol(), currentTank.getX(), currentTank.getY(), angleConvert(currentTank.getAngle()), currentTank.getPower(), true);
            projectiles.add(bullet);
        } else {
            Bullet bullet = new Bullet(currentTank.getSymbol(), currentTank.getX(), currentTank.getY(), angleConvert(currentTank.getAngle()), currentTank.getPower());
            projectiles.add(bullet);
        }
    }

    public void startGame() {
        // get config for selected level
        JSONObject level = config.getJSONArray("levels").getJSONObject(selectedLevel - 1);
        String levelName = level.getString("name");
        String levelFileName = level.getString("layout");
        String backgroundFileName = level.getString("background");
        String terrianColorStr = level.getString("foreground-color");
        String[] terrainColorArr = terrianColorStr.split(",");
        JSONObject playerNames = config.getJSONObject("player_color");
        int terrainColor = color(Integer.parseInt(terrainColorArr[0]), Integer.parseInt(terrainColorArr[1]),
                Integer.parseInt(terrainColorArr[2]));
        String treeFileName = level.getString("trees");
        String parachuteFileName = level.getString("parachute");
        String windFileName = level.getString("wind");
        String fuelFileName = level.getString("fuel");
        String gasFileName = level.getString("gas-station");
        String repairFileName = level.getString("repair");
        String exFileName = level.getString("ex");
        String parachuteKitFileName = level.getString("parachute-kit");
        // load map
        map = MapLoader.loadMap(levelPath + levelFileName);
        map.setLevelName(levelName);
        map.setBackgroundFileName(backgroundFileName);
        map.setTerrainColor(terrainColor);
        map.setPlayerNames(playerNames);
        map.setTreeFileName(treeFileName);
        map.setParachuteFileName(parachuteFileName);
        map.setWindFileName(windFileName);
        map.setFuelFileName(fuelFileName);
        map.setGasFileName(gasFileName);
        map.setRepairFileName(repairFileName);
        map.setExFileName(exFileName);
        map.setParachuteKitFileName(parachuteKitFileName);
        fuel = loadImage(picPath + map.getFuelFileName());
        fuel.resize(32, 32);
        wind = loadImage(picPath + map.getWindFileName());
        wind.resize(32, 32);
        gas = loadImage(picPath + map.getGasFileName());
        gas.resize(20, 20);
        repair = loadImage(picPath + map.getRepairFileName());
        repair.resize(20, 20);
        ex = loadImage(picPath + map.getExFileName());
        ex.resize(20, 20);
        parachuteKit = loadImage(picPath + map.getParachuteKitFileName());
        parachuteKit.resize(20, 20);
        int heights[] = map.heightsArray(map.getTerrain());
        // extend heights array
        int extendedHeights[] = map.interpolateArray(heights, 28 * 32);
        // smooth heights
        int smoothHeights[] = map.smoothData(extendedHeights, 32);
        smoothHeights = map.smoothData(smoothHeights, 32);
        smoothHeights = map.smoothData(smoothHeights, 32);
        map.setHeightsArray(smoothHeights);
        // set player y position
        for (Position p : map.getPlayerPositions()) {
            p.setY(640 - smoothHeights[p.getX()] - 16);
        }
        // set the tree position
        for (Position p : map.getTreePositions()) {
            p.setY(640 - smoothHeights[p.getX()]);
        }

        // init tank
        for (Position p : map.getPlayerPositions()) {
            Tank tank = new Tank(p.getSymbol().charAt(0), p.getX(), p.getY(), 100, true, 0, 50, 250);
            tanks.add(tank);
            p.setAngle(tank.getAngle());
        }
        // init wind
        map.setWind(new Wind());
    }

    // draw bullet
    public void drawBullet() {
        for (Bullet bullet : projectiles) {
            if (bullet.isActive()) {
                if (bullet.isEx()) {
                    fill(255, 0, 0);
                    ellipse(bullet.getX() + 12, bullet.getY(), 6, 8);
                } else {
                    fill(0);
                    ellipse(bullet.getX() + 12, bullet.getY(), 4, 4);
                }
                bullet.update(map.getWind());
                // draw explosion
                if (bullet.isExploded(map)) {
                    int col = floor(bullet.getX());
                    int power = bullet.getPower();
                    // update terrain
                    map.updateTerrain(col, power);
                    // explosion effect
                    isExpFinished = false;
                    // cannon
                    if (bullet.isEx()) {
                        explosion = new Explosion(bullet.getX(), bullet.getY(), bullet.getPower() * 2);
                        currentTank.setCanon(false);
                    } else {
                        explosion = new Explosion(bullet.getX(), bullet.getY(), bullet.getPower());
                    }
                    // check if bullet hit tank
                    checkTankCollision(col, power);
//                     next round
                    if (suriveTanks() <= 1) {
                        gameEnd = true;
                    } else {
                        turnSwitch();
                        map.getWind().update();
                        bullet.setActive(false);
                    }
                }
                // next round
                if (bullet.isOutOfMap()) {
                    turnSwitch();
                    map.getWind().update();
                    bullet.setActive(false);
                }
            }
        }
    }

    //  switch player turn
    public void turnSwitch() {
        currentTank.setShooted(false);
        lastSwitchTime = millis();
        currentPlayerIndex = (currentPlayerIndex + 1) % tanks.size();
        currentTank = tanks.get(currentPlayerIndex);
        if (!currentTank.isAlive()) {
            int nextTankIndex = (currentPlayerIndex + 1) % tanks.size();
            while (!tanks.get(nextTankIndex).isAlive()) {
                nextTankIndex = (nextTankIndex + 1) % tanks.size();
            }
            currentPlayerIndex = nextTankIndex;
            currentTank = tanks.get(currentPlayerIndex);
            lastSwitchTime = millis();
        }
    }

    public void drawExplosion() {
        if (!isExpFinished) {
            explosion.update();
            float x = explosion.getX();
            float y = explosion.getY();
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
        if (!isTankExpFinished) {
            tankExplosion.update();
            float x = tankExplosion.getX();
            float y = tankExplosion.getY();
            float currentRadiusRed = tankExplosion.getCurrentRadiusRed();
            float currentRadiusOrange = tankExplosion.getCurrentRadiusOrange();
            float currentRadiusYellow = tankExplosion.getCurrentRadiusYellow();
            // Red circle
            fill(255, 0, 0);
            ellipse(x, y, currentRadiusRed * 2, currentRadiusRed * 2);
            // Orange circle
            fill(255, 165, 0);
            ellipse(x, y, currentRadiusOrange * 2, currentRadiusOrange * 2);
            // Yellow circle
            fill(255, 255, 0);
            ellipse(x, y, currentRadiusYellow * 2, currentRadiusYellow * 2);
            isTankExpFinished = true;
        }
    }


    public void deployParachute(Tank tank, int col) {
        if (!tank.isAlive()) return;
        PImage parachuteImage = loadImage(picPath + map.getParachuteFileName());
        parachuteImage.resize(32, 32);
        if (!tank.hasParachute()) {
            if (tank.getY() < 640 - map.getHeightsArray()[col] + 16) {
                tank.move(0, 5);
                drawPlayers(map.getPlayerPositions(), map.getPlayerNames());
                tank.reduceLife(5);
                if (!tank.isAlive()) {
                    tankExplosion = new Explosion(tank.getX(), tank.getY(), 15, true);
                    isTankExpFinished = false;
                }
                for (Position p : map.getPlayerPositions()) {
                    if (p.getSymbol().charAt(0) == tank.getSymbol()) {
                        p.setY(tank.getY());
                    }
                }
            }
        } else if (tank.hasParachute() && 640 - map.getHeightsArray()[col] - tank.getY() > 32) {
            // tip to remind parachute
            image(parachuteImage, tank.getX() - parachuteImage.width / 2 + 12, tank.getY() - parachuteImage.height / 2 - 20);
            if (tank.getY() < 640 - map.getHeightsArray()[col] - 32) {
                tank.move(0, 1);
                drawPlayers(map.getPlayerPositions(), map.getPlayerNames());
                for (Position p : map.getPlayerPositions()) {
                    if (p.getSymbol().charAt(0) == tank.getSymbol()) {
                        p.setY(tank.getY());
                    }
                    if (tank.getY() >= 640 - map.getHeightsArray()[col]) {
                        tank.setParachute(tank.getParachute() - 1);
                    }
                }
            }

        }
    }

    // tank get hit reduce life
    public boolean checkTankCollision(int col, int power) {
        int powerRange = 30;
        if (currentTank.isCanon()) {
            powerRange = 60;
        }
        for (Tank tank : tanks) {
            // in the range of explosion
            int dist = (int) abs(tank.getX() - col + tank.getY() - explosion.getY()) / 3;
//            int dist = (int) dist(tank.getX(), tank.getY(), col, explosion.getY());
            if (dist <= powerRange && !explosion.isTankExplosion()) {
                tank.reduceLife(powerRange - dist + 10);
                currentTank.gainScore(powerRange - dist + 10, tank);
                return true;
            }
            if (!tank.isAlive()) {
                tankExplosion = new Explosion(tank.getX(), tank.getY(), 15, true);
                isTankExpFinished = false;
            }
        }
        return false;
    }

    // draw HUD
    public void drawHUD() {
        fill(255, 255, 255);
        rect(0, 0, width, 70);
        fill(0);
        textSize(30);
        // turn info
        text("Player " + tanks.get(currentPlayerIndex).getSymbol() + "'s turn!", 250, 10);
        // tools hud
        textSize(15);
//        text("gas-station", 100, 35);
        image(gas, 300, 15);
        text(tanks.get(currentPlayerIndex).getToolBag()[1], 340, 15);
        image(repair, 300, 40);
        text(tanks.get(currentPlayerIndex).getToolBag()[0], 340, 40);
        image(parachuteKit, 400, 15);
        text(tanks.get(currentPlayerIndex).getToolBag()[2], 440, 15);
        image(ex, 400, 40);
        if (!tanks.get(currentPlayerIndex).isCanon()) {
            fill(0);
            text("Normal Bullet", 530, 40);
        } else {
            fill(255, 0, 0);
            text("Cannon Active!", 530, 40);
        }
        fill(0);
        //health and power
        textAlign(CENTER, TOP);
        textSize(20);
        text("Health: " + tanks.get(currentPlayerIndex).getLife(), 600, 10);
        text("Power: " + tanks.get(currentPlayerIndex).getPower(), 600, 40);
        // fuel
        textAlign(RIGHT, TOP);
        textSize(20);
        image(fuel, width - 200, 10);
        text(tanks.get(currentPlayerIndex).getFuel(), width - 120, 15);
        //wind
        textAlign(RIGHT, TOP);
        textSize(20);
        image(wind, width - 100, 10);
        text(map.getWind().getStrength(), width - 20, 15);
    }

    public void drawGame() {
        PImage bgImage = loadImage(picPath + map.getBackgroundFileName());
        PImage parachuteImage = loadImage(picPath + map.getParachuteFileName());
        background(bgImage);
        drawMap(map.getTerrain(), map.getTerrainColor(), map.getHeightsArray());
        drawPlayers(map.getPlayerPositions(), map.getPlayerNames());
        drawHUD();
        // draw tree
        String treeFileName = map.getTreeFileName();
        if (treeFileName != null) {
            PImage treeImage = loadImage(picPath + treeFileName);
            treeImage.resize(32, 32);
            for (Position treePos : map.getTreePositions()) {
                if (treePos.getY() < 640 - map.getHeightsArray()[treePos.getX()]) {
                    treePos.setY(treePos.getY() + 1);
                    image(treeImage, treePos.getX(), treePos.getY() - 32);
                } else {
                    image(treeImage, treePos.getX(), treePos.getY() - 32);
                }
            }
        }
        drawBullet();
        drawExplosion();
        // draw collision fall
        for (Tank tank : tanks) {
            if (tank.getY() < 640 - map.getHeightsArray()[tank.getX()]) {
                deployParachute(tank, tank.getX());
            }
        }
        // end game
        if (suriveTanks() <= 1) {
            showWinner();
            showFinalScores();
            gameRestarted = true;
        }
        if (gameEnd) {
            if (!gameRestarted) {

            }
        }
    }


    // draw terrain
    public void drawMap(int[][] terrain, int terrainColor, int smoothHeights[]) {
        for (int y = 0; y < smoothHeights.length; y++) {
            fill(terrainColor);
            rect(y, 640 - smoothHeights[y], 1, smoothHeights[y]);
        }
    }

    // draw players
    public void drawPlayers(ArrayList<Position> playerPositions, JSONObject playerNames) {
        String[] rgb;
        int yOffset = 110;
        textAlign(RIGHT, TOP);
        textSize(25);
        fill(0);
        text("ScoreBoard", width - 30, yOffset - 30);
        for (Position playerPos : playerPositions) {
            rgb = playerNames.getString(playerPos.getSymbol()).split(",");
            for (Tank tank : tanks) {
                if (tank.getSymbol() == playerPos.getSymbol().charAt(0)) {
                    if (tank.isAlive()) {
                        fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
//            ellipse(playerPos.getX(), playerPos.getY(), 40, 20);
                        // tank body
                        stroke(0);
                        strokeWeight(1);
                        rect(playerPos.getX(), playerPos.getY(), 25, 10);
                        //tower body
                        rect(playerPos.getX() + 2, playerPos.getY() - 5, 20, 5);
                        //wheel
                        ellipse(playerPos.getX() + 6, playerPos.getY() + 12, 10, 10);
                        ellipse(playerPos.getX() + 19, playerPos.getY() + 12, 10, 10);
                        // draw tower
                        pushMatrix();
                        translate(playerPos.getX() + 15, playerPos.getY());
                        rotate(radians(playerPos.getAngle()));
                        fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
                        rect(0, 0, 5, 20);
                        popMatrix();
                        noStroke();
                        //draw scoreBoards
                        textAlign(RIGHT, TOP);
                        textSize(20);
                        int score = tank.getScore();
                        String playerName = String.valueOf(tank.getSymbol());
                        fill(color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
                        text("Player " + playerName + ": ", width - 100, yOffset);
                        fill(0);
                        textSize(25);
                        text(score, width - 35, yOffset);
                        yOffset += 28;
                    }
                }
            }
        }
    }
}
