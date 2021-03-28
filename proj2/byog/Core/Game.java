package byog.Core;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.awt.Font;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import edu.princeton.cs.introcs.StdDraw;


public class Game {

    /// Static members
    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;
    private static final int ENTRYX = 40;
    private static final int ENTRYY = 5;
    private static final String NORTH = "w";
    private static final String EAST = "d";
    private static final String SOUTH = "s";
    private static final String WEST = "a";
    private static final String PATH = "C:\\Users\\leeb\\Desktop\\GitHubManagement\\project2\\world.txt";
    private static final int WELCOMEWIDTH = 600;
    private static final int WELCOMEHEIGHT = 800;


    /// Instance members
    private boolean setupMode = true;     // flag to check whether setup has been done
    private boolean newGameMode = false; // flag to check whether a new game is gonna be generated
    private boolean quitMode = false; // flag to check whether a game is supposed to be quited
    private String seedString = ""; // store input random seed numbers as String
    private TERenderer ter = new TERenderer();
    private TETile[][] world;
    private int playerX;
    private int playerY;

    private void switchSetupMode() {
        setupMode = !setupMode;
    }

    private void switchNewGameMode() {
        newGameMode = !newGameMode;
    }

    private void switchQuitMode() {
        quitMode = !quitMode;
    }


    /// Private methods

    /* Processes game recursively according to a given input Strings */
    private void processInput(String input) {

        if (input == null) {
            System.out.println("No input given.");
            System.exit(0);
        }

        String first = Character.toString(input.charAt(0));
        first = first.toLowerCase(); // normalize an input to lower case
        processInputString(first);

        if (input.length() > 1) {
            String rest = input.substring(1);
            processInput(rest); // recursive call until input ends
        }

    }

    /* Processes game according to a given single input String */
    private void processInputString(String first) {

        if (setupMode) {      // when the setup hasn't been done
            switch (first) {
                case "n":       // new game gonna be generated
                    switchNewGameMode();
                    break;
                case "s":       // setup a new game
                    setupNewGame();
                    break;
                case "l":       // load the previously saved game
                    load();
                    break;
                case "q":
                    System.exit(0);
                    break;
                default:        // append next seed integer to seedString
                    try {
                        Long.parseLong(first);
                        seedString += first;
                    } catch (NumberFormatException e) { // exit program if input is invalid
                        System.out.println("Invalid input given: " + first);
                        System.exit(0);
                    }
                    break;
            }
        } else {                // when the setup has been done
            switch (first) {
                // @Note: Add my keyboard preferences
                case NORTH:
                case "o":
                case EAST:
                case "l":
                case SOUTH:
                case "n":
                case WEST:
                case "k":
                    move(first);
                    break;
                case ":":
                    switchQuitMode();
                    break;
                case "q":
                    saveAndQuit();
                    System.exit(0);
                    break;
                default:
            }
        }

    }

    /* Generates a randomized world and put a player in it */
    private void setupNewGame() {
        // check validity of input
        if (!newGameMode) {
            String error = "Input string " + "\"S\" given, but no game has been initialized.\n"
                    + "Please initialize game first by input string \"N\" and following random seed"
                    + "numbers";
            System.out.println(error);
            System.exit(0);
        }
        switchNewGameMode();

        // setup a random seed and generate a world according to it
        MapGenerator wg;
        if (seedString.equals("")) {
            wg = new MapGenerator(WIDTH, HEIGHT,0);
        } else {
            long seed = Long.parseLong(seedString);
            wg = new MapGenerator(WIDTH, HEIGHT,  seed);
        }
        world = wg.mapGenerator();

        // setup a player

        playerX = wg.player.x;
        playerY = wg.player.y;

        // switch off setupMode
        switchSetupMode();
    }

    /* Loads a previously saved game. If no saved game found, returns null. */
    private void load() {
        File f = new File(PATH);
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            world = (TETile[][]) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            System.out.println("No previously saved world found.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.out.println("Class TETile[][] not found.");
            System.exit(1);
        }

        // switch off setupMode
        switchSetupMode();

        // rewrite playerX, playerY
        rewritePlayerLocation();
    }

    /* Helper method for load method: rewrite playerX, playerY */
    private void rewritePlayerLocation() {
        for (int w = 0; w < WIDTH; w += 1) {
            for (int h = 0; h < HEIGHT; h += 1) {
                if (world[w][h].equals(Tileset.PLAYER)) {
                    playerX = w;
                    playerY = h;
                }
            }
        }
    }

    /* Moves a player according to input string */
    /* @Note: Add my keyboard preferences */
    private void move(String input) {
        switch (input) {
            case NORTH:
            case "o":
                if (world[playerX][playerY + 1].equals(Tileset.FLOOR)) {
                    world[playerX][playerY + 1] = Tileset.PLAYER;
                    world[playerX][playerY] = Tileset.FLOOR;
                    playerY += 1;
                }
                return;
            case EAST:
            case "l":
                if (world[playerX + 1][playerY].equals(Tileset.FLOOR)) {
                    world[playerX + 1][playerY] = Tileset.PLAYER;
                    world[playerX][playerY] = Tileset.FLOOR;
                    playerX += 1;
                }
                return;
            case SOUTH:
            case "n":
                if (world[playerX][playerY - 1].equals(Tileset.FLOOR)) {
                    world[playerX][playerY - 1] = Tileset.PLAYER;
                    world[playerX][playerY] = Tileset.FLOOR;
                    playerY -= 1;
                }
                return;
            case WEST:
            case "k":
                if (world[playerX - 1][playerY].equals(Tileset.FLOOR)) {
                    world[playerX - 1][playerY] = Tileset.PLAYER;
                    world[playerX][playerY] = Tileset.FLOOR;
                    playerX -= 1;
                }
                return;
            default:
        }
    }

    /* Quits game saving a current game */
    private void saveAndQuit() {
        // ignore if quit flag : hasn't been inputted in advance
        if (!quitMode) {
            return;
        }
        switchQuitMode();

        File f = new File(PATH);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(world);
            oos.close();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    /* Process keyboard inputs in setupMode */
    private void processWelcome() {
        // prepare welcome board window
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(WELCOMEWIDTH, WELCOMEHEIGHT);
        StdDraw.clear(StdDraw.BLACK);

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String typed = Character.toString(StdDraw.nextKeyTyped());
                processInput(typed);
            }

            renderWelcomeBoard();

            if (!setupMode) {   // break after setup has been done and enter game mode
                break;
            }
        }

        processGame();
    }

    /* Renders welcome board in setupMode */
    private void renderWelcomeBoard() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        // title
        StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
        StdDraw.text(0.5, 0.8, "CS61B: BYoG");

        // menu
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
        StdDraw.text(0.5, 0.5, "New Game: N");
        StdDraw.text(0.5, 0.475, "Load Game: L");
        StdDraw.text(0.5, 0.45, "Quit: Q");

        // seed
        if (newGameMode) {
            StdDraw.text(0.5, 0.25, "Seed: " + seedString);
            StdDraw.text(0.5, 0.225, "(Press S to start the game)");
        }

        StdDraw.show();
        StdDraw.pause(100);
    }

    /* Process keyboard inputs in game mode */
    private void processGame() {
        ter.initialize(WIDTH, HEIGHT);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String typed = Character.toString(StdDraw.nextKeyTyped());
                processInput(typed);
            }

            renderGame();
        }
    }

    /* Renders the current state of the game */
    private void renderGame() {
        renderWorld();
        showTileOnHover();
        StdDraw.pause(10);
    }

    /* Renders world */
    private void renderWorld() {
        StdDraw.setFont();
        StdDraw.setPenColor();
        ter.renderFrame(world);
    }

    /* Draws text describing the Tile currently under the mouse pointer */
    private void showTileOnHover() {
        // turn the position of mouse pointer into xy-coordinate
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        TETile mouseTile = world[mouseX][mouseY];

        // draw as text
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 15));
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(1, HEIGHT - 1, mouseTile.description());
        StdDraw.show();
    }

    /// Public methods

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        processWelcome();
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        processInput(input);
        return world;
    }


    /// Main method just to check this class works itself
    public static void main(String[] args) {
        Game game = new Game();
        game.playWithKeyboard();
    }

}
//package byog.Core;
//
//import byog.TileEngine.TERenderer;
//import byog.TileEngine.TETile;
//import byog.TileEngine.Tileset;
//import edu.princeton.cs.introcs.StdDraw;
//import org.junit.Test;
//
//import java.awt.*;
//import java.io.*;
//import java.util.Random;
//import java.util.Scanner;
//
//public class Game {
//    //初始化渲染器
//    TERenderer ter = new TERenderer();
//    /* Feel free to change the width and height. */
//    public static final int WIDTH = 80;
//    public static final int HEIGHT = 30;
//    public static final int MENUW=40;
//    public static final int MENUH=40;
//    private static long SEED;
//    private boolean gameOver;
//    /**
//     * Method used for playing a fresh game. The game should start from the main menu.
//     */
//    public void playWithKeyboard() {
//        showBlank();
//        showMenu();
//        StdDraw.text(MENUW/2,MENUH/4,"press your choice please:");
//        StdDraw.show();
//        StdDraw.enableDoubleBuffering();
//        String s;
//        while (true) {
//            s = "";
//            if (!StdDraw.hasNextKeyTyped())
//                continue;
//            char key = StdDraw.nextKeyTyped();
//            s += key;
//            StdDraw.enableDoubleBuffering();
//            StdDraw.clear(Color.BLACK);
//            showMenu();
//            StdDraw.text(MENUW / 2, MENUH / 4, "Press your choice please: " + s);
//            StdDraw.show();
//            switch (key) {
//                case ('n'):
//                case ('N'): {
//                    String sd = "";
//                    String trueSeed = "";
//                    char c = 'l';
//                    StdDraw.clear(Color.BLACK);
//                    showMenu();
//                    StdDraw.text(MENUW / 2, MENUH / 4,
//                            "Now please input a seed, then press 's' to start the game.");
//                    StdDraw.show();
//                    do {
//                        if (!StdDraw.hasNextKeyTyped()) {
//                            continue;
//                        }
//                        c = StdDraw.nextKeyTyped();
//                        if (c >= 48 && c <= 57) {
//                            trueSeed += String.valueOf(c);
//                        }
//                        sd += String.valueOf(c);
//                        if (c != 's') {
//                            StdDraw.clear(Color.BLACK);
//                            showMenu();
//                            StdDraw.text(MENUW / 2, MENUH / 4, "Your seed is: " + sd);
//                            StdDraw.show();
//                        }
//                    } while (c != 's');
//
//                    SEED = getStringtoNum(trueSeed);
//                    StdDraw.pause(500);
//                    System.out.println("## Game final SEED: " + SEED);
//
//                    ter.initialize(WIDTH,HEIGHT);
//                    MapGenerator map = new MapGenerator(WIDTH,HEIGHT,SEED);
//                    TETile[][]world=map.mapGenerator();
//                    ter.renderFrame(world);
//                    playGame(map,world);
//                    break;
//                }
//                case ('l'):
//                case ('L'): {
//                    MapGenerator map=loadGame();
//                    ter.initialize(80, 30);
//                    TETile[][]world=map.mapGenerator();
//                    ter.renderFrame(world);
//                    gameOver = false;
//                    playGame(map,world);
//                    break;
//                }
//                case ('q'):
//                case ('Q'): {
//                    gameOver = true;
//                    System.exit(0);
//                    break;
//                }
//                default:
//            }
//        }
//    }
//    private MapGenerator loadGame(){
//        File f = new File("./crazyWorld.txt");
//        if (f.exists()) {
//            try {
//                FileInputStream fs = new FileInputStream(f);
//                ObjectInputStream os = new ObjectInputStream(fs);
//                return (MapGenerator) os.readObject();
//            } catch (FileNotFoundException e) {
//                System.out.println("file not found");
//                System.exit(0);
//            } catch (IOException e) {
//                System.out.println(e);
//                System.exit(0);
//            } catch (ClassNotFoundException e) {
//                System.out.println("class not found");
//                System.exit(0);
//            }
//        }
//        /* In the case no World has been saved yet, we return a new one. */
//        return new MapGenerator(WIDTH,HEIGHT,SEED);
//    }
//    private void playGame(MapGenerator map,TETile[][]world) {
//        new Thread(() -> {
//            while (true) {
//                StdDraw.enableDoubleBuffering();
//
//                //long hh = timeCounter / 60 / 60 % 60;
//                //long mm = timeCounter / 60 % 60;
//                //long ss = timeCounter % 60;
//                //System.out.println("left + hh + "hours" + mm + "minutes" + ss + "seconds");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        char key;
//        String record = "";
//        while (!gameOver) {
//            if (!StdDraw.hasNextKeyTyped()) {
//                continue;
//            }
//            key = StdDraw.nextKeyTyped();
//            record += key;
////            if (health == 0 || timeCounter == 0) {
////                gameOver = true;
////                health = 5;
////                sandNumber = 0;
////                //System.out.println("You lose!");
////                showBlank();
////                drawFrame("Sorry! You lose!");
////                StdDraw.pause(5000);
////                break;
////            } else
//            if ((world[map.player.x][map.player.y].equals(Tileset.LOCKED_DOOR))) {
//                gameOver = true;
//                showBlank();
//                drawFrame("Congratulation! You win!");
//                StdDraw.pause(5500);
//                break;
//            }
//            //System.out.println(record);
//            for (int i = 0; i < record.length() - 1; i += 1) {
//                if ((record.charAt(i) == ':' && record.charAt(i + 1) == 'q')
//                        || (record.charAt(i) == ':' && record.charAt(i + 1) == 'Q')) {
//                    saveGame(map);
//                    showBlank();
//                    drawFrame("Your game has been saved!");
//                    StdDraw.pause(3000);
//                    gameOver = true;
//                }
//            }
//            move(map, world,key);
//
//
//        }
//        showBlank();
//        drawFrame("Have an another try next time!");
//        StdDraw.pause(5000);
//    }
//    private static void saveGame(MapGenerator map) {
//        File f = new File("C:\\Users\\leeb\\Desktop\\GitHubManagement\\project2\\world.txt");
//        try {
//            if (!f.exists()) {
//                f.createNewFile();
//            }
//            FileOutputStream fs = new FileOutputStream(f);
//            ObjectOutputStream os = new ObjectOutputStream(fs);
//            os.writeObject(map);
//        }  catch (FileNotFoundException e) {
//            System.out.println("file not found");
//            System.exit(0);
//        } catch (IOException e) {
//            System.out.println(e);
//            System.exit(0);
//        }
//    }
//    public void drawFrame(String str) {
//        int midWidth = MENUW / 2;
//        int midHeight = MENUH / 2;
//        StdDraw.clear();
//        StdDraw.clear(Color.black);
//        // Draw the actual text
//        Font bigFont = new Font("Monaco", Font.BOLD, 30);
//        StdDraw.setFont(bigFont);
//        StdDraw.setPenColor(Color.white);
//        StdDraw.text(midWidth, midHeight, str);
//        StdDraw.show();
//    }
//    private void move(MapGenerator map,TETile[][]world,char key){
//        TETile upper = world[map.player.x][map.player.y+1];
//        TETile lower = world[map.player.x][map.player.y-1];
//        TETile right = world[map.player.x+1][map.player.y];
//        TETile left = world[map.player.x-1][map.player.y];
//        switch (key) {
//            case ('w'):
//            case ('W'): {
//                if (upper.equals(Tileset.WALL)) {
//                    return;
//                }
//                world[map.player.x][map.player.y+1] = Tileset.PLAYER;
//                world[map.player.x][map.player.y] = Tileset.FLOOR;
//                Position newPlayer = new Position(map.player.x, map.player.y+1);
//                map.player=newPlayer;
//            }
//            case ('s'):
//            case ('S'): {
//                if (lower.equals(Tileset.WALL)) {
//                    return;
//                }
//                world[map.player.x][map.player.y-1] = Tileset.PLAYER;
//                world[map.player.x][map.player.y] = Tileset.FLOOR;
//                Position newPlayer = new Position(map.player.x, map.player.y-1);
//                map.player=newPlayer;
//            }
//            case ('a'):
//            case ('A'): {
//                if (left.equals(Tileset.WALL)) {
//                    return;
//                }
//                world[map.player.x-1][map.player.y] = Tileset.PLAYER;
//                world[map.player.x][map.player.y] = Tileset.FLOOR;
//                Position newPlayer = new Position(map.player.x-1, map.player.y);
//                map.player=newPlayer;
//            }
//            case ('d'):
//            case ('D'): {
//                if (right.equals(Tileset.WALL)) {
//                    return;
//                }
//                world[map.player.x+1][map.player.y] = Tileset.PLAYER;
//                world[map.player.x][map.player.y] = Tileset.FLOOR;
//                Position newPlayer = new Position(map.player.x+1, map.player.y);
//                map.player=newPlayer;
//            } default: return;
//        }
//    }
//    private long getStringtoNum(String str) {
//        str = str.trim();
//        String str2 = "";
//        if (!"".equals(str)) {
//            for (int i = 0; i < str.length(); i++) {
//                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
//                    str2 = str2 + str.charAt(i);
//                }
//            }
//        }
//        return Long.parseLong(str2);
//    }
//    private void showBlank(){
//        StdDraw.clear();
//        StdDraw.enableDoubleBuffering();
//        //设置画布的大小
//        StdDraw.setCanvasSize(MENUW * 16, MENUH * 16);
//        //创建字体
//        Font font = new Font("Monaco", Font.BOLD, 100);
//        //设置字体
//        StdDraw.setFont(font);
//        //设置x轴和y轴为指定范围
//        StdDraw.setXscale(0, MENUW);
//        StdDraw.setYscale(0, MENUH);
//        //将屏幕变成指定颜色
//        StdDraw.clear(Color.BLACK);
//    }
//    private void showMenu(){
//        Font title = new Font("Monaco", Font.BOLD, 25);
//        Font mainMenu = new Font("Monaco", Font.PLAIN, 16);
//        StdDraw.setFont(title);
//        //设置画笔颜色
//        StdDraw.setPenColor(Color.white);
//        //书写文字
//        StdDraw.text(MENUW / 2, MENUH * 2 / 3, " CS61B proj2: Cool Game! ");
//        StdDraw.setFont(mainMenu);
//        StdDraw.text(MENUW / 2, MENUH * 5.5 / 10, "New Game (n / N)");
//        StdDraw.text(MENUW / 2, MENUH * 4.5 / 10, "Load Game (l / L)");
//        StdDraw.text(MENUW / 2, MENUH * 3.5 / 10, "Quit (q / Q)");
//    }
//    /**
//     * Method used for autograding and testing the game code. The input string will be a series
//     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
//     * behave exactly as if the user typed these characters into the game after playing
//     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
//     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
//     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
//     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
//     * to get the exact same world back again, since this corresponds to loading the saved game.
//     * @param input the input string to feed to your program
//     * @return the 2D TETile[][] representing the state of the world
//     */
//    public TETile[][] playWithInputString(String input) {
//        // TODO: Fill out this method to run the game using the input passed in,
//        // and return a 2D tile representation of the world that would have been
//        // drawn if the same inputs had been given to playWithKeyboard().
//
//        //除了数字之外的内容全部替换为空，即只提取seed
//        long seed=Long.parseLong(input.replaceAll("[^0-9]",""));
//        //使用seed生成地图
//        MapGenerator map=new MapGenerator(WIDTH,HEIGHT,seed);
//
//        //生成数组
//        return map.mapGenerator();
//    }
//}
