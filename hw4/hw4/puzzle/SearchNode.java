package hw4.puzzle;

public class SearchNode implements Comparable<SearchNode>{
    public WorldState word;
    public int moves;
    public SearchNode previous;
    public int priority;
    public SearchNode(WorldState w,int m,SearchNode p){
        word=w;
        moves=m;
        previous=p;
        priority=moves+w.estimatedDistanceToGoal();
    }


    @Override
    public int compareTo(SearchNode o) {
        return this.priority-o.priority;
    }
}
