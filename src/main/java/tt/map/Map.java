package tt.map;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private List<List<Character>> grid;
    private int[][] terrain;
    private ArrayList<Position> playerPositions;
    private ArrayList<Position> treePositions;

    public Map(List<List<Character>> grid, int[][] terrain, ArrayList<Position> playerPositions, ArrayList<Position> treePositions) {
        this.grid = grid;
        this.terrain = terrain;
        this.playerPositions = playerPositions;
        this.treePositions = treePositions;
    }

    public int[] smoothData(int[] data, int windowSize) {
        int[] smoothedData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            int sum = 0;
            int count = 0;
            for (int j = Math.max(0, i - windowSize / 2); j <= Math.min(data.length - 1, i + windowSize / 2); j++) {
                sum += data[j];
                count++;
            }
            smoothedData[i] = sum / count;
        }

        return smoothedData;
    }
    // interpolate array
    public int[] interpolateArray(int[] originalArray, int targetLength) {
        float interpolationFactor = (float)originalArray.length / targetLength;
        int[] interpolatedArray = new int[targetLength];
        for (int i = 0; i < targetLength; i++) {
            float originalIndex = i * interpolationFactor;
            int lowerIndex = (int)Math.floor(originalIndex);
            int upperIndex = Math.min((int)Math.ceil(originalIndex), originalArray.length - 1);
            float fraction = originalIndex - lowerIndex;
            interpolatedArray[i] = (int)(originalArray[lowerIndex] * (1 - fraction) + originalArray[upperIndex] * fraction);
        }
        return interpolatedArray;
    }

    //get heightsArray
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
            maxValues[col] = max*32;
        }
        return maxValues;
        }


    // print map
    public void printMap() {
        for (int[] t: terrain) {
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


}
