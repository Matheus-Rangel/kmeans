package br.ufrn.matheusrangel.kmeans;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

public class Kmeans<T extends ClusterData<T>> implements Runnable{
    private List<T> values;
    private List<Cluster<T>> bestClusters;
    private Double bestVariance;
    private List<List<T>> iterations;
    @Getter private final int numJobs;
    @Getter private final int maxIterations;
    @Getter private int curIteration;
    @Getter private int numClusters;
    private AtomicBoolean running;

    public boolean getRunning(){
        return running.get();
    }
    public Double getBestVariance(){
        if(running.get()){
            return null;
        }
        return bestVariance;
    }
    public List<Cluster<T>> getBestClusters(){
        if(running.get()){
            return null;
        }
        return bestClusters;
    }
    public Kmeans(List<T> values, int numClusters, int numJobs, int maxIterations) {
        this.values = values;
        this.numClusters = numClusters;
        this.numJobs = numJobs;
        this.maxIterations = maxIterations;
        T v = values.get(0);
        this.iterations = new ArrayList<>(maxIterations);
        for (int i = 0; i < maxIterations; i++) {
            List<T> centroids = new ArrayList<>(numClusters);
            for (int j = 0; j < numClusters; j++) {
                centroids.add(v.getRandom());
            }
            iterations.add(centroids);
        }
        curIteration = 0;
        bestVariance = Double.MAX_VALUE;
        running = new AtomicBoolean(false);
    }

    public interface Callback<T extends ClusterData<T>>{
        void updateBestCluster(ClusterizeResult<T> result);
    }
    private Callback<T> callback = (clusterizeResult) -> {
        Double variance = clusterizeResult.getVariance();
        synchronized (bestVariance){
            if(variance < bestVariance){
                bestVariance = variance;
                bestClusters = clusterizeResult.getClusterList();
            }
        }
    };


    @Override
    public void run() {
        if(running.get()){
            return;
        }
        running.set(true);
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinClusterize<T> task = new ForkJoinClusterize<>(values, iterations, iterations.size()/numJobs);
        ClusterizeResult<T> result = pool.invoke(task);
        bestVariance = result.getVariance();
        bestClusters = result.getClusterList();
        running.set(false);
    }
}
