package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world that contains RANDOM tiles.
 */
public class RandomWorldDemo {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static class Position{
        int x;
        int y;
        Position(int x,int y){
            this.x=y;
            this.y=y;
        }
    }
    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillWithRandomTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.NOTHING;
            default: return Tileset.NOTHING;
        }
    }
    public static void addHexagon(TETile[][]world,Position p,int s,TETile t){
        addHex(world,p.x,p.y,0,s,t);
    }
    private static void addHex(TETile[][]world,int x,int y,int step,int s,TETile t){
        if (step>=s)return;
        for (int i=0;i<s+2*step;i++)
            world[x+i][y]=t;
        y--;
        addHex(world,x-1,y,step+1,s,t);
        y-=2*(s-step-1);
        for (int i=0;i<s+2*step;i++)
            world[x+i][y]=t;
    }
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        //初始化渲染器，同时创建一个空的窗口
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
        //往数组中填Tile对象
        fillWithRandomTiles(randomTiles);
        addHexagon(randomTiles,new Position(25,25),3,Tileset.FLOWER);
        //将数组的内容渲染在窗口中
        ter.renderFrame(randomTiles);
    }


}
