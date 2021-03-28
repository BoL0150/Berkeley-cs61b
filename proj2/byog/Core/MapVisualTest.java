package byog.Core;

import byog.TileEngine.TERenderer;

public class MapVisualTest {
    public static void main(String[] args) {
        TERenderer ter=new TERenderer();
        ter.initialize(79,29);
        MapGenerator map=new MapGenerator(79,29,284);
        ter.renderFrame(map.mapGenerator());
    }
}
