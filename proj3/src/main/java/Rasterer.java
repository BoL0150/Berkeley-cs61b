import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private static final double ULLON=MapServer.ROOT_ULLON;
    private static final double LRLON=MapServer.ROOT_LRLON;
    private static final double ULLAT=MapServer.ROOT_ULLAT;
    private static final double LRLAT=MapServer.ROOT_LRLAT;
    private static final double LONG_WIDTH=Math.abs(LRLON-ULLON);
    private static final double LAT_HEIGHT=Math.abs(ULLAT-LRLAT);
    private static final double TILE_SIZE=MapServer.TILE_SIZE;
    public Rasterer() {
        // YOUR CODE HERE

    }


    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
//        System.out.println(params);
        Map<String, Object> results = new HashMap<>();

        double lrlon = params.get("lrlon");
        double ullon = params.get("ullon");
        double lrlat = params.get("lrlat");
        double ullat = params.get("ullat");
        double width = params.get("w");
        double height = params.get("h");
        int depth = commuteDepth(ullon, lrlon, width);
        boolean querySuccess = true;
        //边界条件判断
        if (lrlon < ullon || ullat < lrlat || lrlon <= ULLON || ullon >= LRLON || lrlat >= ULLAT || ullat <= LRLAT) {
            querySuccess = false;
        }
        //在这一层寻找对应的图片

        //每张图片的宽度和高度（以经度纬度为单位）
        double widthPerPic = LONG_WIDTH / Math.pow(2.0, depth);
        double heightPerPic = LAT_HEIGHT / Math.pow(2.0, depth);
        //x、y方向对应哪几张图片
        int xMin = (int) (Math.floor((ullon - ULLON) / widthPerPic)) ;
        int xMax = (int) (Math.floor((lrlon - ULLON) / widthPerPic)) ;
        int yMin = (int) (Math.floor((ULLAT - ullat) / heightPerPic)) ;
        int yMax = (int) (Math.floor((ULLAT - lrlat) / heightPerPic)) ;
//        System.out.println("xMin:" + xMin + " xMax" + xMax + " yMin" + yMin + " yMax" + yMax);
        //图片的边界经纬度
        double xLeftBounding = ULLON + xMin * widthPerPic;
        double xRightBounding = ULLON + (xMax + 1) * widthPerPic;
        double yUpperBouding = ULLAT - yMin * heightPerPic;
        double yLowerBouding = ULLAT - (yMax + 1) * heightPerPic;
        //在角落的情况
        if (ullon < ULLON) {
            xMin = 0;
            xLeftBounding = ULLON;
        }
        if (lrlon > LRLON) {
            xMax = (int) Math.pow(2, depth) - 1;
            xRightBounding = LRLON;
        }
        if (ullat > ULLAT) {
            yMin = 0;
            yUpperBouding = ULLAT;
        }
        if (lrlat < LRLAT) {
            yMax = (int) Math.pow(2, depth) - 1;
            yLowerBouding = LRLAT;
        }
        String[][] render_grid = new String[yMax - yMin + 1][xMax - xMin + 1];
        for (int i = yMin; i <= yMax; i++) {
            for (int j = xMin; j <= xMax; j++) {
                render_grid[i - yMin][j - xMin] = "d" + depth + "_x" + j + "_y" + i + ".png";
//                System.out.println("i:" + i + "  j:" + j);
//                System.out.println(render_grid[i - yMin][j - xMin]);
            }
        }
        results.put("render_grid", render_grid);
        results.put("raster_ul_lon", xLeftBounding);
        results.put("raster_ul_lat", yUpperBouding);
        results.put("raster_lr_lon", xRightBounding);
        results.put("raster_lr_lat", yLowerBouding);
        results.put("depth", depth);
        results.put("query_success", querySuccess);
//        System.out.println(results);

        return results;
    }

    private int commuteDepth(double ullon, double lrlon, double width) {
        double desiredLonDPP = (lrlon - ullon) / width;
        double n = Math.log(LONG_WIDTH / (desiredLonDPP * TILE_SIZE)) / Math.log(2.0);
        int depth = (int) Math.ceil(n);
        return Math.min(depth, 7);
    }

}
