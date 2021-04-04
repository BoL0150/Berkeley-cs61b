package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[][]grid;
    private int N;
    private WeightedQuickUnionUF UF;
    //用来避免backwash的UF
    private WeightedQuickUnionUF UFwithoutBackWash;
    private int numberOfOpenSites;
    public Percolation(int N){
        if (N<=0)
            throw new IllegalArgumentException("N must > 0");
        this.N=N;
        this.numberOfOpenSites=0;
        UF=new WeightedQuickUnionUF(N*N+2);
        UFwithoutBackWash=new WeightedQuickUnionUF(N*N+2);
        grid=new boolean[N][N];
    }
    public void open(int row,int col){
        int next[][]=new int[][]{
                {0,1},
                {1,0},
                {0,-1},
                {-1,0}
        };
        if (row<0||row>=N||col<0||col>=N)
            throw new IllegalArgumentException("row and col must between 0 and N-1");
        if (isOpen(row,col))return;
        grid[row][col]=true;
        numberOfOpenSites++;
        //连接周围的open的点
        for (int i=0;i<=3;i++){
            int mx=row+next[i][0];
            int my=col+next[i][1];
            if (my<0||my>=N)
                continue;
            if (mx==-1){
                //两个UnionFind都需要使用最顶部的辅助点
                UFwithoutBackWash.union(xyTo1D(row, col),N*N);
                UF.union(xyTo1D(row, col),N*N);
                continue;
            }
            else if (mx==N){
                //只有一个UnionFind需要使用最底部的辅助点
                UF.union(xyTo1D(row, col),N*N+1);
                continue;
            }
            //判断两个点之间有没有连接不需要底部的辅助点
            if (isOpen(mx,my)&&UFwithoutBackWash.connected(xyTo1D(row,col),xyTo1D(mx,my))==false){
                UF.union(xyTo1D(row, col),xyTo1D(mx,my));
                UFwithoutBackWash.union(xyTo1D(row, col),xyTo1D(mx,my));
            }
        }
    }
    private int xyTo1D(int mx,int my){
        return mx*N+my;
    }
    // is the site (row, col) open?
    public boolean isOpen(int row, int col){
        if (row<0||row>=N||col<0||col>=N)
            throw new IllegalArgumentException("row and col must between 0 and N-1");
        return grid[row][col];
    }
    // is the site (row, col) full?
    public boolean isFull(int row, int col){
        if (row<0||row>=N||col<0||col>=N)
            throw new IllegalArgumentException("row and col must between 0 and N-1");
        //在isFull中使用UFwithoutBackWash
        return UFwithoutBackWash.connected(xyTo1D(row, col), N * N);
    }
    // number of open sites
    public int numberOfOpenSites(){
        return numberOfOpenSites;
    }
    // does the system percolate?
    public boolean percolates(){
        //
        return UF.connected(N*N,N*N+1);
    }
    // use for unit testing (not required)
    public static void main(String[] args){

    }
}
