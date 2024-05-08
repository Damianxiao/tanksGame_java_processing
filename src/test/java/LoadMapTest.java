
import org.junit.jupiter.api.Test;
import tt.map.Map;
import tt.map.MapLoader;

import java.util.List;

public class LoadMapTest {
    @Test
    public void testLoadMap() {
        // Load map from file
        Map map = MapLoader.loadMap("src/main/resources/level/level1.txt");
        // Print map to console
        System.out.println("before smooth");
        map.printMap();
        // Smooth the map
//        map.smoothMap(map.getTerrain());
        map.smoothTerrain();
        System.out.println("after smooth");
        map.printMap();

    }

}
