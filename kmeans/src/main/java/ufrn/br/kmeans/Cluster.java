package ufrn.br.kmeans;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;


public class Cluster<T extends ClusterData<T>> {
    @Getter List<T> values;
    @Getter T centroid;
    public Cluster(T centroid){
        this.centroid = centroid;
        this.values = new LinkedList<>();
    }
    public Double getVariation(){
        double variation = 0.0;
        for (T value:values) {
            variation += Math.pow(centroid.calculateDistante(value),2);
        }
        return variation;
    }
    public void addValue(T value){
        values.add(value);
    };
    public List<T> getValues(){
        return new LinkedList<>(values);
    }
    public T getMean(){
        T v = values.get(0);
        return v.calculateMean(values);
    }
}
