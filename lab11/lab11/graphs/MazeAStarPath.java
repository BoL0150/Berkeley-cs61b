package lab11.graphs;

import java.util.PriorityQueue;

/**
 *  @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        int size=maze.N();
        int v_X = v % size + 1;
        int v_Y = v / size + 1;
        int target_X = t % size + 1;
        int target_Y = t / size + 1;
        return Math.abs(target_X - v_X) + Math.abs(target_Y - v_Y);
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked() {
        return -1;
        /* You do not have to use this method. */
    }

    private class Node implements Comparable<Node>{
        public int vertice;
        public Node parent;
        public int moves;
        public int priority;
        Node(int v,int moves,Node parent){
            this.vertice = v;
            this.moves=moves;
            this.parent = parent;
            this.priority = h(v) + moves;
        }

        @Override
        public int compareTo(Node o) {
            return this.priority - o.priority;
        }
    }
    //标记图中哪些点已经访问过（指曾经以该点为中心向四周探索的点，即从优先队列中出队的点）
    private boolean book[]=new boolean[marked.length];
    /** Performs an A star search from vertex s. */
    private void astar(int s) {
        marked[s] = true;
        announce();
        PriorityQueue<Node>pq=new PriorityQueue<>();
        pq.offer(new Node(s, 0, null));
        while (!pq.isEmpty() && h(pq.peek().vertice) != 0) {
            Node v = pq.poll();
            book[v.vertice]=true;
            for (int w : maze.adj(v.vertice)) {
                //曾经加入过优先队列的点没有访问的意义
                if ((v.parent == null || w != v.parent.vertice) && book[w] == false) {
                    book[w] = true;
                    pq.offer(new Node(w, v.moves + 1, v));
                    marked[w] = true;
                    edgeTo[w] = v.vertice;
                    distTo[w] = distTo[v.vertice] + 1;
                    announce();
                }
            }
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

}

