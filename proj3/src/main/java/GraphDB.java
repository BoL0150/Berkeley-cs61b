import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
//解析xml文件，创建一个Graph
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     *
     * @param dbPath Path to the XML file to be parsed.
     */
    //每个序号对应的点
    public static Map<Long, Node> nodes = new HashMap<>();
    //每个点相邻的点
    private static Map<Long, ArrayList<Long>> adjNode = new HashMap<>();
    //一个名字可能对应多个点
    private static Map<String, ArrayList<Long>> names = new HashMap<>();
    //某一点对应的地点位置
//    private static Map<Long, Loaction> locations = new HashMap<>();

    //    private static class Edge{
//        private Node v;
//        private Node w;
//        private double weight;
//
//        public Edge(Node v, Node w, double weight) {
//            this.v = v;
//            this.w = w;
//            this.weight = weight;
//        }
//    }
//    private static class Loaction{
//        private final Node node;
//        private final String name;
//
//        public Loaction(String name, Node node) {
//            this.name = name;
//            this.node = node;
//        }
//
//        public Node getNode() {
//            return node;
//        }
//
//        public String getName() {
//            return name;
//        }
//    }
    public static class Node{
        public final long id;
        public final double lon;
        public final double lat;
        public String name = null;
        public String wayName = null;

        public Node(long id, double lon, double lat) {
            this.id = id;
            this.lon = lon;
            this.lat = lat;
        }
    }

    //构造函数解析xml文件，将xml文件以图的形式表示出来
    public GraphDB(String dbPath) {
        try {
            //读取xml文件
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);
            //第二步：创建SAX解析器
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            //第三步：创建事件处理程序对象
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            //第四步：将XML文件和事件处理程序分配到解析器，parser从上到下遍历xml文件中的每一个element，
            // 回调GraphBuildingHandler中的事件处理方法
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        // TODO: Your code here.
        Iterator<Map.Entry<Long, ArrayList<Long>>> it = adjNode.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, ArrayList<Long>> entry = it.next();
            if (entry.getValue().isEmpty()) {
                nodes.remove(entry.getKey());
                it.remove();
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     *
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        return nodes.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     *
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        validateVertex(nodes.get(v));
        return adjNode.get(v);
    }
    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     *
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     *
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     *
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double closest = Double.MAX_VALUE;
        long ret=0;
        for (long id : vertices()) {
            double distance = distance(lon(id), lat(id), lon, lat);
            if (distance < closest) {
                closest = distance;
                ret = id;
            }
        }
        return ret;
    }

    /**
     * Gets the longitude of a vertex.
     *
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        validateVertex(nodes.get(v));
        return nodes.get(v).lon;
    }

    /**
     * Gets the latitude of a vertex.
     *
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        validateVertex(nodes.get(v));
        return nodes.get(v).lat;
    }
    String getName(long v) {
        if (nodes.get(v).name == null) {
            throw new IllegalArgumentException();
        }
        return nodes.get(v).name;
    }
    void addName(long id, double lon, double lat, String locationName) {
        //将名字统一转换成小写
        String cleanedName = cleanString(locationName);

        if (!names.containsKey(cleanedName)) {
            names.put(cleanedName, new ArrayList<>());
        }
        names.get(cleanedName).add(id);
        nodes.get(id).name = locationName;
    }

    void addNode(long id, double lon, double lat) {
        Node n = new Node(id, lon, lat);
        nodes.put(id, n);
        adjNode.put(id, new ArrayList<>());
    }

    void addWay(ArrayList<Long> ways,String wayName) {
        for (int i = 1; i < ways.size(); i++) {
            long id1 = ways.get(i - 1);
            long id2 = ways.get(i);
            addEdge(nodes.get(id1), nodes.get(id2));
            nodes.get(id1).wayName = wayName;
        }
        nodes.get(ways.get(ways.size() - 1)).wayName = wayName;
    }
    void addEdge(Node v, Node w) {
        validateVertex(v);
        validateVertex(w);
        adjNode.get(v.id).add(w.id);
        adjNode.get(w.id).add(v.id);
    }
    void validateVertex(Node v) {
        if (!nodes.containsKey(v.id)) {
            throw new IllegalArgumentException("Vertex " + v + "is not in the graph");
        }
    }
}
