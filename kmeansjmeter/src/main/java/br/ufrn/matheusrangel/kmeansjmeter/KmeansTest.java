package br.ufrn.matheusrangel.kmeansjmeter;

import br.ufrn.matheusrangel.Iris;
import br.ufrn.matheusrangel.Main;
import br.ufrn.matheusrangel.kmeans.Kmeans;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class KmeansTest extends AbstractJavaSamplerClient implements Serializable {
    private List<Iris> values;
    public KmeansTest(){
        try {
            values = Main.parser(Paths.get("data/iris.data"));
        } catch (IOException e) {
            values = new ArrayList<>();
        }
    }
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        int numJobs = Integer.parseInt(javaSamplerContext.getParameter("numJobs"));
        int numClusters = Integer.parseInt(javaSamplerContext.getParameter("numClusters"));
        int maxIterations = Integer.parseInt(javaSamplerContext.getParameter("maxIterations"));
        Kmeans<Iris> kmean = new Kmeans<>(values, numClusters, numJobs, maxIterations);
        kmean.run();
        while(kmean.getRunning()) {
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {}
        }
        result.sampleEnd();
        result.setResponseCode("200");
        result.setResponseMessage("OK");
        result.setSuccessful(true);
        return result;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("numClusters","1");
        defaultParameters.addArgument("numJobs","1");
        defaultParameters.addArgument("maxIterations","10");
        return defaultParameters;
    }
}
