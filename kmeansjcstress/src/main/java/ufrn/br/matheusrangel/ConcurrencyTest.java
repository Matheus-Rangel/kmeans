/*
 * Copyright (c) 2017, Red Hat Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.Á
 */
package ufrn.br.matheusrangel;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;
import ufrn.br.Iris;
import ufrn.br.kmeans.Cluster;
import ufrn.br.kmeans.Kmeans;

import java.util.ArrayList;
import java.util.List;

// See jcstress-samples or existing tests for API introduction and testing guidelines

@JCStressTest
// Outline the outcomes here. The default outcome is provided, you need to remove it:
@Outcome(id = "0", expect = Expect.ACCEPTABLE, desc = "Default outcome.")
@Outcome(id = "1", expect = Expect.ACCEPTABLE, desc = "Default outcome.")
@State
public class ConcurrencyTest {
    private List<Iris> randomValues = randomList(50);
    private volatile double variance = 0;
    private Kmeans<Iris> kmeans = new Kmeans<>(randomValues, 3, 4, 10);
    private static List<Iris> randomList(int size){
        Iris i = new Iris(Iris.Tipo.UNCLASSIFIED, 0,0,0,0);
        List<Iris> list = new ArrayList<>(size);
        for (int j = 0; j < size; j++) {
            list.add(i.getRandom());
        }
        return list;
    }

    @Actor
    public void actor1(I_Result r) {
        kmeans.run();
        while(kmeans.getBestVariance() == null){};
        if(variance == 0){
            variance = kmeans.getBestVariance();
        }else{
            boolean flag = variance == kmeans.getBestVariance();
            r.r1 = (byte) (flag ? 1:0);
        }
    }

    @Actor
    public void actor2(I_Result r) {
        kmeans.run();
        while(kmeans.getBestClusters() == null){};
        double v = 0;
        for (Cluster<Iris> cluster: kmeans.getBestClusters()) {
            v += cluster.getVariation();
        }
        if(variance == 0){
            variance = v;
        }else{
            boolean flag = variance == v;
            r.r1 = (byte) (flag ? 1:0);
        }
    }
}
