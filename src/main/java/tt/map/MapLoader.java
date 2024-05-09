package tt.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.loadStrings;

public class MapLoader {
    public Map map;
    // load map file map.txt
    public static Map loadMap(String filePath) {
        List<List<Character>> map = new ArrayList<>();
        List<Character> row = new ArrayList<>();
        String[] lines = loadStrings(new File(filePath));
        int [][] terrain = new int[lines.length][28];
        ArrayList<Position> playerPositions = new ArrayList<>();
        ArrayList<Position> treePositions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    row.add(line.charAt(i));
                }
                map.add(new ArrayList<>(row));
                row.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int y = 0;y <lines.length;y++){
            for(int x = 0;x<lines[y].length();x++){
                char ch = lines[y].charAt(x);
                if(ch == 'X'){
                    terrain[y][x] = 20-y;
                } else if (ch == 'T') {
                    treePositions.add(new Position(x* 25, y* 32));
                } else if (Character.isLetter(ch)) {
                    playerPositions.add(new Position(x* 32 , y* 32 ,ch));
                }
            }
        }
        return new Map(map, terrain, playerPositions, treePositions);
    }

}
