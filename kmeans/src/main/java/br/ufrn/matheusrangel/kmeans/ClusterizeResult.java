package br.ufrn.matheusrangel.kmeans;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClusterizeResult<T extends ClusterData<T>> {
    List<Cluster<T>> clusterList;
    Double variance;
}
