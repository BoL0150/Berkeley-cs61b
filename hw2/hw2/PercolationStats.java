package hw2;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private int T;
    private double[]x;
    // perform T independent experiments on an N-by-N grid
    public PercolationStats(int N, int T, PercolationFactory pf){
        if (N<=0||T<=0)
            throw new IllegalArgumentException("N and T must > 0");
        this.T=T;
        x=new double[T];

        for (int i=0;i<T;i++){
            Percolation test=pf.make(N);
            while (!test.percolates()){
                int randomRow= StdRandom.uniform(N);
                int randomCol = StdRandom.uniform(N);
                test.open(randomRow,randomCol);
            }
            x[i]=(double) test.numberOfOpenSites()/(N*N);
        }
    }
    // sample mean of percolation threshold
    public double mean(){
        return StdStats.mean(x);
    }
    // sample standard deviation of percolation threshold
    public double stddev(){
        return StdStats.stddev(x);
    }
    // low endpoint of 95% confidence interval
    public double confidenceLow(){
        return mean()-1.96*stddev()/Math.sqrt(T);
    }
    // high endpoint of 95% confidence interval
    public double confidenceHigh(){
        return mean() + 1.96 * stddev() / Math.sqrt(T);
    }
}
