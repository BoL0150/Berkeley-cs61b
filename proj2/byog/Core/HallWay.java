package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.LinkedList;
import java.util.Random;

public class HallWay {

    //随机找一个起始点
    private static Position RandomStart(TETile[][]world,long seed){
        Random RANDOM=new Random(seed);
        while (true){
            int startX=RANDOM.nextInt(world.length-2);
            int startY=RANDOM.nextInt(world[0].length-2);
            startX=startX%2==1?startX:startX+1;
            startY=startY%2==1?startY:startY+1;
            if (world[startX][startY]==Tileset.NOTHING)
                return new Position(startX,startY);
        }
    }
    //寻找某一点周围的可以扩展的点，将它们加入候选列表，顺便打通和已经过的点之间的墙
    private static void PositionsAvailableAroundAndConnectPath(Position position, TETile[][]world, LinkedList<Position>positionList,long seed){

        int[][]nextDirction={
                {2,0},
                {0,-2},
                {-2,0},
                {0,2}
        };
        Random RANDOM=new Random(seed);
        //标记每一个方向是否走过
        boolean[]book=new boolean[4];
        //只能连接一次
        boolean isConnected=false;
        while (book[0]==false||book[1]==false||book[2]==false||book[3]==false){
            //随机选取一个方向
            int next=RANDOM.nextInt(4);
            //如果这个方向已经走过了，则重新选择方向
            if (book[next])continue;
            book[next]=true;
            int nextX=position.x+nextDirction[next][0];
            int nextY=position.y+nextDirction[next][1];
            //如果这个点越界了，就选下一个点
            if (nextX<0||nextX>world.length-1||nextY<0||nextY>world[0].length-1)
                continue;
            //如果这个点什么也没有，符合要求，加入候选队列
            if (Tileset.NOTHING.equals(world[nextX][nextY])){
                //将该点变为Flower，标记它在候选队列中
                world[nextX][nextY]=Tileset.FLOWER;
                positionList.add(new Position(nextX,nextY));
            }
            //如果这个点已经经过，就将它们相连，即打通它们之间的墙
            if (Tileset.FLOOR.equals(world[nextX][nextY])&&isConnected==false){
                int mx=(position.x+nextX)/2;
                int my=(position.y+nextY)/2;
                //将它们之间的墙变为FLOOR
                world[mx][my]=Tileset.FLOOR;
                isConnected=true;
            }
        }
    }

    public static boolean generateHallWay(TETile[][]world,long seed){
        //随机找一个起始点
        Position start=RandomStart(world,seed);
        generateHallWay(world,start,seed);
        //必须保证生成的地图所有地点都可达，如果有没有连接到的点，就存在不可达的点，需要重新生成地图
        for (int i=0;i<world.length;i++){
            for (int j=0;j<world[0].length;j++){
                if (Tileset.NOTHING.equals(world[i][j]))
                    return false;
            }
        }
        return true;
    }
    public static void generateHallWay(TETile[][]world,Position start,long seed){
        LinkedList<Position> positionList=new LinkedList<>();

        positionList.add(start);
        Random RANDOM=new Random(seed);
        while (!positionList.isEmpty()){
            //随机获取一个点
            int index=RANDOM.nextInt(positionList.size());
            Position curPos=positionList.get(index);
            //当前点的位置
            int curX=curPos.x;
            int curY=curPos.y;
            //将该点置为FLOOR，标记该点已经被走过
            world[curX][curY]=Tileset.FLOOR;
            //将这个点周围的空的点加入候选队列，顺便打通和已经过的点之间的墙
            PositionsAvailableAroundAndConnectPath(curPos,world,positionList,seed);
            positionList.remove(index);
        }

    }

    public static void initializeHallWay(TETile[][]world){
        for (int i=0;i<=world.length-1;i++)
            for (int j=0;j<=world[0].length-1;j++)
                world[i][j]= Tileset.WALL;

        for (int i=1;i<=world.length-2;i+=2)
            for (int j=1;j<=world[0].length-2;j+=2)
                world[i][j]=Tileset.NOTHING;
    }
    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(79,29);
        TETile[][]world=new TETile[79][29];
        initializeHallWay(world);
        generateHallWay(world,180084946);
        ter.renderFrame(world);
    }
}
