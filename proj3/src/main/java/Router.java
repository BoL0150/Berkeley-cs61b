import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {

    private static GraphDB.Node start;
    private static GraphDB.Node destination;
    private static GraphDB graph;
    private static class SearchNode implements Comparable<SearchNode> {
        public Long id;
        public SearchNode parent;
        public double distanceToStart;
        public double priorit;

        public SearchNode(Long id, SearchNode parent,double distanceToStart) {
            this.id = id;
            this.parent = parent;
            this.distanceToStart = distanceToStart;
            this.priorit = distanceToStart + distanceToDest(id);
        }
        @Override
        public int compareTo(SearchNode o) {
            if (this.priorit < o.priorit) {
                return -1;
            }
            if (this.priorit > o.priorit) {
                return 1;
            }
            return 0;
        }
    }

    private static double distanceToDest(Long id) {
        GraphDB.Node v = graph.nodes.get(id);
        return GraphDB.distance(v.lon, v.lat, destination.lon, destination.lat);
    }


    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        graph = g;
        start = graph.nodes.get(g.closest(stlon, stlat));
        destination = graph.nodes.get(g.closest(destlon, destlat));
        Map<Long, Boolean> marked = new HashMap<>();
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();
        pq.offer(new SearchNode(start.id, null, 0));
        while (!pq.isEmpty() && !isGoal(pq.peek())) {
            SearchNode v = pq.poll();
            //标记为经过
            marked.put(v.id, true);
            for (Long w : g.adjacent(v.id)) {
                if (!marked.containsKey(w) || marked.get(w) == false) {
                    pq.offer(new SearchNode(w, v, v.distanceToStart + distance(g, w, v.id)));
                }
            }
        }
        SearchNode pos = pq.peek();
        ArrayList<Long> path = new ArrayList<>();
        while (pos != null) {
            path.add(pos.id);
            pos = pos.parent;
        }
        Collections.reverse(path);
        return path; // FIXME
    }
    private static double distance(GraphDB graph,Long id1, Long id2) {
        GraphDB.Node v1 = graph.nodes.get(id1);
        GraphDB.Node v2 = graph.nodes.get(id2);
        return GraphDB.distance(v1.lon, v1.lat, v2.lon, v2.lat);
    }
    private static boolean isGoal(SearchNode v) {
        return distanceToDest(v.id) == 0;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {

        double distance = 0;
        int relativeDirection = NavigationDirection.START;
        ArrayList<NavigationDirection> navigationList = new ArrayList<>();
        //将输入的点转化为连接点的边，如果边的名字相同，则说明在同一条路上，否则就不在同一条路上
        ArrayList<GraphDB.Edge> ways = getWays(g, route);
        if (ways.size() == 1) {
            navigationList.add(new NavigationDirection(NavigationDirection.START, ways.get(0).getName(), ways.get(0).getWeight()));
            return navigationList;
        }
        for (int i = 1; i < ways.size(); i++) {
            GraphDB.Edge preEdge = ways.get(i - 1);
            GraphDB.Edge nextEdge = ways.get(i);

            Long prevVertex = route.get(i - 1);
            Long curVertex = route.get(i);
            Long nextVertex = route.get(i + 1);

            String preWayName = preEdge.getName();
            String nextWayName = nextEdge.getName();

            distance += preEdge.getWeight();
            //如果前后两条路的名字不一样，则说明切换了路线，更新NavigationList，清零distance
            if (!preWayName.equals(nextWayName)) {
                double preBearing = g.bearing(prevVertex, curVertex);
                double nextBearing = g.bearing(curVertex, nextVertex);
                navigationList.add(new NavigationDirection(relativeDirection, preWayName, distance));

                relativeDirection = relativeDirection(preBearing, nextBearing);
                distance = 0;
            }
            if (i == ways.size() - 1) {
                distance += nextEdge.getWeight();
                navigationList.add(new NavigationDirection(relativeDirection, nextWayName, distance));
            }
        }
        return navigationList;
    }

    private static ArrayList<GraphDB.Edge> getWays(GraphDB g, List<Long> route) {
        ArrayList<GraphDB.Edge> ways = new ArrayList<>();
        for (int i = 1; i < route.size(); i++) {
            Long curVertex = route.get(i - 1);
            Long nextVertex = route.get(i);
            for (GraphDB.Edge e : g.neighbors(curVertex)) {
                if (e.other(curVertex).equals(nextVertex)) {
                    ways.add(e);
                }
            }
        }
        return ways;
    }
    private static int relativeDirection(double prevBearing, double curBearing) {
        double relativeBearing = curBearing - prevBearing;
        double absBearing = Math.abs(relativeBearing);
        if (absBearing > 180) {
            absBearing = 360 - absBearing;
            relativeBearing *= -1;
        }
        if (absBearing <= 15) {
            return NavigationDirection.STRAIGHT;
        }
        if (absBearing <= 30) {
            return relativeBearing < 0 ? NavigationDirection.SLIGHT_LEFT : NavigationDirection.SLIGHT_RIGHT;
        }
        if (absBearing <= 100) {
            return relativeBearing < 0 ? NavigationDirection.LEFT : NavigationDirection.RIGHT;
        }
        else {
            return relativeBearing < 0 ? NavigationDirection.SHARP_LEFT : NavigationDirection.SHARP_RIGHT;
        }
    }
    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        /**
         *
         * @param direction
         * @param way
         * @param distance
         */
        public NavigationDirection(int direction, String way, double distance) {
            this.direction = direction;
            this.way = way;
            this.distance = distance;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
