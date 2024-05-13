package tt;

import processing.core.PApplet;
import processing.core.PImage;
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

public class processingTest extends PApplet {
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
    private Explosion  tankExplosion;
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
    PImage parachuteKit = null;
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
            if (gameEnd) {
                if (!gameRestarted) {
                    delay(2000);
                    gameRestarted = true;
                }
        }
    }

    // check if only one tank is alive
    public int suriveTanks(){
        int count = 0;
        for (Tank tank : tanks) {
            if (tank.isAlive()) {
                count++;
            }
        }
        return count;
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
            } else if (key == 'w' && !currentTank.isShooted() && currentTank.getPower()<=currentTank.getLife()) {
                currentTank.gainPower(1);
            } else if (key == 's' && !currentTank.isShooted() && currentTank.getPower()<=currentTank.getLife()) {
                currentTank.gainPower(-1);
            } else if (key == ' ') {
                if (!currentTank.isShooted()) {
                    shoot();
                    currentTank.setShooted(true);

                }
            } else if (key == 't'&& !currentTank.isShooted()) {
                if (currentTank.getScore() >= 20 || currentTank.getToolBag()[0] > 0) {
                    if (currentTank.getLife() < 100) {
                        currentTank.repair();
                    }
                }
            } else if (key == 'f'&& !currentTank.isShooted()) {
                if (currentTank.getScore() >= 10 || currentTank.getToolBag()[1] > 0) {
                    if (currentTank.getFuel() < 1000) {
                        currentTank.increaseFuel();
                    }
                }
            } else if (key == 'p'&& !currentTank.isShooted()) {
                if (currentTank.getScore() >= 15 || currentTank.getToolBag()[2] > 0) {
                    currentTank.increaseParachute();
                }
            } else if (key == 'x'&& !currentTank.isShooted()) {
                if (currentTank.getScore() >= 20 && currentTank.getToolBag()[3] == 0) {
                    currentTank.increasePower();
                }
            } else if (key == 'r' || gameRestarted) {
                restartGame();
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
        JSONObject level = config.getJSONArray("levels").getJSONObject(0);
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

    public void deployParachute(Tank tank, int col) {
        if (!tank.isAlive()) return;
        PImage parachuteImage = loadImage(picPath + map.getParachuteFileName());
        parachuteImage.resize(32, 32);
        if (!tank.hasParachute()) {
            if (tank.getY() < 640 - map.getHeightsArray()[col] + 16) {
                tank.move(0, 5);
                tank.reduceLife(5);
                if(!tank.isAlive()){
                    tankExplosion = new Explosion(tank.getX(), tank.getY(), 15,true);
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
        if(currentTank.isCanon()){
            powerRange = 60;
        }
        for (Tank tank : tanks) {
            // in the range of explosion
            int dist = (int) abs(tank.getX()- col+tank.getY()-explosion.getY())/2 ;
//            int dist = (int) dist(tank.getX(), tank.getY(), col, explosion.getY());
            if (dist <= powerRange && !explosion.isTankExplosion()) {
                tank.reduceLife(powerRange-dist+20);
                currentTank.gainScore(powerRange-dist, tank);
            }
            if(!tank.isAlive()){
                tankExplosion = new Explosion(tank.getX(), tank.getY(), 15,true);
                isTankExpFinished = false;
            }
        }
        return false;
    }
}
