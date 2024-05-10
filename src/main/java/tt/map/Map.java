package tt.map;

import processing.data.JSONObject;
import tt.player.Tank;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Map {
    private List<List<Character>> grid;
    private int[][] terrain;
    private ArrayList<Position> playerPositions;
    private ArrayList<Position> treePositions;
    private int[] heightsArray;
    private String backgroundFileName;
    private JSONObject playerNames;
    private int terrainColor;
    private String treeFileName;
    private ArrayList<Tank> tanks;
    private String parachuteFileName;

    public Map(List<List<Character>> grid, int[][] terrain, ArrayList<Position> playerPositions,
            ArrayList<Position> treePositions) {
        this.grid = grid;
        this.terrain = terrain;
        this.playerPositions = playerPositions;
        this.treePositions = treePositions;
    }

    public  void updateTerrain(int col,int power) {
            int height = heightsArray[col];
            int powerRange = power/2;
            for (int i = col - powerRange; i <= col + powerRange; i++) {
                if (i >= 0 && i < heightsArray.length) {
                // calculate new height
                int newHeight = max(0, height - (power - abs(col - i)));
                heightsArray[i] = newHeight;
                }
            }

            // smooth the terrain
            setHeightsArray(smoothData(getHeightsArray(), 32));
    }


    public int[] smoothData(int[] data, int windowSize) {
        int[] smoothedData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            int sum = 0;
            int count = 0;
            for (int j = max(0, i - windowSize / 2); j <= Math.min(data.length - 1, i + windowSize / 2); j++) {
                sum += data[j];
                count++;
            }
            smoothedData[i] = sum / count;
        }

        return smoothedData;
    }

    // interpolate array
    public int[] interpolateArray(int[] originalArray, int targetLength) {
        float interpolationFactor = (float) originalArray.length / targetLength;
        int[] interpolatedArray = new int[targetLength];
        for (int i = 0; i < targetLength; i++) {
            float originalIndex = i * interpolationFactor;
            int lowerIndex = (int) Math.floor(originalIndex);
            int upperIndex = Math.min((int) Math.ceil(originalIndex), originalArray.length - 1);
            float fraction = originalIndex - lowerIndex;
            interpolatedArray[i] = (int) (originalArray[lowerIndex] * (1 - fraction)
                    + originalArray[upperIndex] * fraction);
        }
        return interpolatedArray;
    }

    // get heightsArray
    public int[] heightsArray(int[][] terrain) {
        int[] maxValues = new int[28];
        int rows = terrain.length;
        int cols = terrain[0].length;

        for (int col = 0; col < cols; col++) {
            int max = terrain[0][col];
            for (int row = 1; row < rows; row++) {
                if (terrain[row][col] > max) {
                    max = terrain[row][col];
                }
            }
            maxValues[col] = max * 32;
        }
        return maxValues;
    }

    // print map
    public void printMap() {
        for (int[] t : terrain) {
            for (int i : t) {
                System.out.print(i);
            }
            System.out.println();
        }
    }

    public List<List<Character>> getGrid() {
        return grid;
    }

    public int[][] getTerrain() {
        return terrain;
    }

    public ArrayList<Position> getPlayerPositions() {
        return playerPositions;
    }

    public ArrayList<Position> getTreePositions() {
        return treePositions;
    }

    public int[] getHeightsArray() {
        return heightsArray;
    }

    public void setHeightsArray(int[] heightsArray) {
        this.heightsArray = heightsArray;
    }

    public void setGrid(List<List<Character>> grid) {
        this.grid = grid;
    }

    public void setTerrain(int[][] terrain) {
        this.terrain = terrain;
    }

    public void setPlayerPositions(ArrayList<Position> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public void setTreePositions(ArrayList<Position> treePositions) {
        this.treePositions = treePositions;
    }

    public String getBackgroundFileName() {
        return backgroundFileName;
    }

    public void setBackgroundFileName(String backgroundFileName) {
        this.backgroundFileName = backgroundFileName;
    }

    public JSONObject getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(JSONObject playerNames) {
        this.playerNames = playerNames;
    }

    public int getTerrainColor() {
        return terrainColor;
    }

    public void setTerrainColor(int terrainColor) {
        this.terrainColor = terrainColor;
    }

    public String getTreeFileName() {
        return treeFileName;
    }

    public void setTreeFileName(String treeFileName) {
        this.treeFileName = treeFileName;
    }

    public ArrayList<Tank> getTanks() {
        return tanks;
    }

    public void setTanks(ArrayList<Tank> tanks) {
        this.tanks = tanks;
    }

    public String getParachuteFileName() {
        return parachuteFileName;
    }

    public void setParachuteFileName(String parachuteFileName) {
        this.parachuteFileName = parachuteFileName;
    }
}
