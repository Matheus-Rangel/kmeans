package br.ufrn.matheusrangel.kmeans;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Clusterize<T extends ClusterData<T>> implements Runnable {
    private List<T> values;
    private Kmeans.Callback callback;
    private List<Cluster<T>> currentClusters;
    private List<T> centroids;
    Clusterize(List<T> values, Kmeans.Callback<T> callback, List<T> initialCentroids){
        currentClusters = new LinkedList<>();
        for (T centroid: initialCentroids) {
            currentClusters.add(new Cluster<T>(centroid));
            centroids = initialCentroids;
        }
        this.values = values;
        this.callback = callback;
    }
    private boolean setCentroids(){
        boolean flag = false;
        List<T> newCentroids = new ArrayList<>(centroids.size());
        for (Cluster<T> cluster: currentClusters) {
            T mean = cluster.getCentroid().calculateMean(cluster.getValues());
            newCentroids.add(mean);
        }
        for (int i = 0; i < centroids.size(); i++) {
            if(!newCentroids.get(i).equals(centroids.get(i))){
                flag = true;
                centroids.set(i, newCentroids.get(i));
            }
        }
        if(flag){
            for (int i = 0; i < centroids.size(); i++) {
                currentClusters.set(i, new Cluster<>(centroids.get(i)));
            }
        }
        return flag;
    }
    private void clusterizeValue(T value){
        Cluster<T> bestCluster = null;
        Double bestDistance = null;
        for (Cluster<T> cluster: currentClusters) {
            if(bestCluster == null){
                bestCluster = cluster;
                bestDistance = cluster.getCentroid().calculateDistante(value);
                continue;
            }
            Double curDistantance = cluster.getCentroid().calculateDistante(value);
            if(curDistantance < bestDistance){
                bestCluster = cluster;
                bestDistance = curDistantance;
            }
        }
        bestCluster.addValue(value);
    }
    private boolean clusterize(){
        for (T value: values) {
            clusterizeValue(value);
        }
        return setCentroids();
    }
    private Double getVariance(){
        double variance = 0;
        for (Cluster<T> cluster: currentClusters) {
            variance += cluster.getVariation();
        }
        return variance;
    }
    @Override
    public void run() {
        boolean flag = true;
        while(flag){
            flag = clusterize();
        };
        callback.updateBestCluster(currentClusters, getVariance());
    }
}
