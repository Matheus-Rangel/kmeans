package br.ufrn.matheusrangel;

import br.ufrn.matheusrangel.kmeans.Cluster;
import br.ufrn.matheusrangel.kmeans.Kmeans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Iris> parser(Path p) throws IOException {
        List<String> entries = Files.readAllLines(p);
        List<Iris> values = new ArrayList<>(entries.size());
        for (String entry: entries) {
            String[] valores = entry.split(",");
            try{
                values.add(new Iris(
                        Iris.Tipo.fromString(valores[4]),
                        Double.parseDouble(valores[0]),
                        Double.parseDouble(valores[1]),
                        Double.parseDouble(valores[2]),
                        Double.parseDouble(valores[3])
                ));
            }catch (ArrayIndexOutOfBoundsException e){
                continue;
            }
        }
        return values;
    }
    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();
        Path p = Paths.get(args[0]);
        int numClusters = Integer.parseInt(args[1]);
        int maxIterations = Integer.parseInt(args[2]);
        int numJobs = Integer.parseInt(args[3]);
        List<Iris> values = parser(p);
        Kmeans<Iris> k = new Kmeans<>(values, numClusters, numJobs, maxIterations);
        k.run();
        Instant end = Instant.now();
        printResult(k.getBestClusters(), k.getBestVariance());
        saveResult(Paths.get("C:\\Users\\mathe\\Documents\\kmeans\\data\\result.txt"), k.getBestClusters());
        System.out.println("Total Execution time: "+ Duration.between(start, end).toMillis());
    }
    public static void saveResult(Path p, List<Cluster<Iris>> clusters) throws IOException {
        String result = "";
        int clusterId = 0;
        for (Cluster<Iris> cluster: clusters) {
            clusterId += 1;
            for(Iris iris: cluster.getValues()){
                result += clusterId + "," + iris.getSepalLength() + "," + iris.getSepalWidth()
                        + "," + iris.getPetalLength() +  "," + iris.getPetalWidth() +"," + iris.getTipo() + "\n";
            }
        }
        byte[] bytes = result.getBytes("utf-8");
        Files.write(p, bytes);
    }
    public static void printResult(List<Cluster<Iris>> clusters, Double variance){
        System.out.println("###### RESULTADO #######");
        System.out.println("BEST VARIANCE = "+ variance);
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println();
            Cluster<Iris> cluster = clusters.get(i);
            System.out.println("CLUSTER " + i +":");
            for (Iris iris: cluster.getValues()) {
                System.out.println(iris.toString());
            }
        }
    }
}
