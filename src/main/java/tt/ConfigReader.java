package tt;

import processing.data.JSONObject;

import static processing.core.PApplet.loadJSONObject;


public class ConfigReader {
    private final String levelPath = "src/main/resources/level/";
    private final String picPath = "src/main/resources/pic/";

    private JSONObject config;

    public int getNumLevels() {
        return config.getJSONArray("levels").size();
    }

    public JSONObject getLevel(int index) {
        return config.getJSONArray("levels").getJSONObject(index);
    }

    public JSONObject getPlayerColors() {
        return config.getJSONObject("player_color");
    }
}
