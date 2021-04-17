package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.Stack;

public class Solver {
    private MinPQ<SearchNode>MP=new MinPQ<>();
    private ArrayList<WorldState>solutions=new ArrayList<>();
    public Solver(WorldState initial){

        MP.insert(new SearchNode(initial,0,null));
        while (!MP.min().word.isGoal()){
            SearchNode min=MP.delMin();
            for (WorldState w:min.word.neighbors())
                //如果当前探测的neighbor和自己的父节点不相同，就可以加入队列
                if (min.previous==null||!w.equals(min.previous.word))
                    MP.insert(new SearchNode(w, min.moves+1,min));
        }
    }
    public int moves(){
        return MP.min().moves;
    }

    public Iterable<WorldState> solution() {
        Stack<WorldState>stack=new Stack<>();
        SearchNode pos=MP.min();
        while (pos!=null) {
            stack.push(pos.word);
            pos=pos.previous;
        }
        while (!stack.isEmpty())
            solutions.add(stack.pop());
        return solutions;
    }
}
