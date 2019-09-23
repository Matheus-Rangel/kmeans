package br.ufrn.matheusrangel;

import br.ufrn.matheusrangel.kmeans.ClusterData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
public class Iris implements ClusterData<Iris> {
    private static double minSepalLength = 4.0;
    private static double maxSepalLength= 8.0;
    private static double minSepalWidth = 2.0;
    private static double maxSepalWidth = 4.5;
    private static double minPetalLength = 1.0;
    private static double maxPetalLength = 7.0;
    private static double minPetalWidth = 0.1;
    private static double maxPetalWidth = 2.5;

    public enum Tipo{
        IRIS_SETOSA(1), IRIS_VERSICOLOUR(2), IRIS_VIRGINICA(3), UNCLASSIFIED(0);
        public final int valor;
        public static Tipo fromString(String s){
            s = s.toLowerCase();
            switch (s){
                case "iris-setosa":
                    return IRIS_SETOSA;
                case "iris-versicolor":
                    return IRIS_VERSICOLOUR;
                case "iris-virginica":
                    return IRIS_VIRGINICA;
                default:
                    return UNCLASSIFIED;
            }
        }
        Tipo(int i) {
            valor = i;
        }
    }
    private Tipo tipo;
    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;

    private Double getNormalizedPetalLength(){
        return (this.petalLength - minPetalLength)/(maxPetalLength - minPetalLength);
    }
    private Double getNormalizedPetalWidth(){
        return (this.petalWidth - minPetalWidth)/(maxPetalWidth - minPetalWidth);
    }
    @Override
    public Double calculateDistante(Iris v) {
        return Math.abs((this.getNormalizedPetalLength() - v.getNormalizedPetalLength())
                +(this.getNormalizedPetalWidth() - v.getNormalizedPetalWidth()));
    }
    @Override
    public Iris calculateMean(List<Iris> list) {
        double totalSepalLength = 0.0;
        double totalSepalWidth = 0.0;
        double totalPetalLength = 0.0;
        double totalPetalWidth = 0.0;
        for (Iris i: list) {
            totalPetalLength += i.getPetalLength();
            totalPetalWidth += i.getPetalWidth();
            totalSepalLength += i.getSepalLength();
            totalSepalWidth += i.getSepalWidth();
        }
        return new Iris(
            Tipo.UNCLASSIFIED,
            totalSepalLength/list.size(),
            totalSepalWidth/list.size(),
            totalPetalLength/list.size(),
            totalPetalWidth/list.size()
        );
    }

    @Override
    public Iris getRandom(){
        Random r = new Random();
        double petalLength = minPetalLength + (maxPetalLength - minPetalLength) * r.nextDouble();
        double petalWidth = minPetalWidth + (maxPetalWidth - minPetalWidth) * r.nextDouble();
        double sepalLength = minSepalLength + (maxSepalLength - minSepalLength) * r.nextDouble();
        double sepalWidth = minSepalWidth + (maxSepalWidth - minSepalWidth) * r.nextDouble();
        return new Iris(Tipo.UNCLASSIFIED, sepalLength, sepalWidth, petalLength, petalWidth);
    }
}
