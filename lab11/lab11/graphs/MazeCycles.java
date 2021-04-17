package lab11.graphs;

import java.util.Stack;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private class Node{
        int v;
        int parent;
        public Node(int v,int parent){
            this.v=v;
            this.parent=parent;
        }
    }
    public MazeCycles(Maze m) {
        super(m);
    }

    @Override
    public void solve() {
        DFS(new Node(0, 0));
        while (!stack.isEmpty()) {
            int top=stack.pop();
            if (stack.isEmpty())
                break;
            edgeTo[top] =  stack.peek();
            announce();
        }
    }

    private boolean isCycle=false;
    private Stack<Integer>stack=new Stack<>();
    private boolean overlap;
    private int overlapPoint;
    private void DFS(Node node){
        marked[node.v]=true;
        announce();
        for (int w : maze.adj(node.v)) {
            if (marked[w]&&w!=node.parent){
                isCycle=true;
                stack.push(w);
                overlap=false;
                overlapPoint = w;
                return;
            }
            if (!marked[w]){
                marked[w]=true;
                DFS(new Node(w,node.v));
            }
            if (isCycle){
                if (overlap==false)
                    stack.push(w);
                if (overlapPoint==w){
                    overlap=true;
                }
                return;
            }
        }
    }
    // Helper methods go here
}

