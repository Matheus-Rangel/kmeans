package br.ufrn.matheusrangel.kmeans;

import java.util.List;
import java.util.concurrent.RecursiveTask;


public class ForkJoinClusterize<T extends ClusterData<T>> extends RecursiveTask<ClusterizeResult<T>> {

    private List<T> values;
    private List<List<T>> iterations;
    private final int defaultSize;
    public ForkJoinClusterize(List<T> values, List<List<T>> iterations, int defaultSize){
        this.values = values;
        this.iterations = iterations;
        this.defaultSize = defaultSize;
    }
    @Override
    protected ClusterizeResult<T> compute() {
        if(iterations.size() > defaultSize){
            List<List<T>> it1 = iterations.subList(0, iterations.size()/2);
            List<List<T>> it2 = iterations.subList(iterations.size()/2, iterations.size());
            ForkJoinClusterize<T> join1 = new ForkJoinClusterize<>(values, it1, defaultSize);
            ForkJoinClusterize<T> join2 = new ForkJoinClusterize<>(values, it2, defaultSize);
            join1.fork();
            join2.fork();
            ClusterizeResult<T> r1 = join1.join();
            ClusterizeResult<T> r2 = join2.join();
            return r1.getVariance() < r2.getVariance() ? r1 : r2;
        }else{
            ClusterizeResult<T> bestR = new ClusterizeResult<>(null, Double.MAX_VALUE);
            for (List<T> iteration: iterations
            ) {
                Clusterize<T> c = new Clusterize<T>(values, result -> {}, iteration);
                ClusterizeResult<T> curR = c.compute();
                bestR = curR.getVariance() < bestR.getVariance() ? curR : bestR;
            }
            return bestR;
        }
    }
}
