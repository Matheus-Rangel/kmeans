package br.ufrn.matheusrangel.kmeans;

import java.util.List;

public interface ClusterData<T> {
    Double calculateDistante(T v);
    T calculateMean(List<T> list);
    T getRandom();
}
