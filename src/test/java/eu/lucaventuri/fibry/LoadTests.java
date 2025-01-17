package eu.lucaventuri.fibry;

import eu.lucaventuri.common.HttpUtil;
import eu.lucaventuri.common.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTests {
    @Test
    public void testHttp() throws IOException, URISyntaxException, InterruptedException {
        boolean fibersAvailable = ActorUtils.areFibersAvailable();

        System.out.println("Fibers: " + fibersAvailable);

        var num = new AtomicInteger();
        int port = 10001;
        var url = new URL("http://localhost:" + port + "/test");
        var uri = new URI("http://localhost:" + port + "/test");

        Stereotypes.def().embeddedHttpServer(port, new Stereotypes.HttpStringWorker("/test", ex ->
                ""+num.incrementAndGet()));

        final int numThreads;
        final int numCalls;

        if (fibersAvailable) {
            numThreads = 250;
            numCalls = 100;
        } else {
            numThreads = 100;
            numCalls = 100;
        }

        CountDownLatch latch = new CountDownLatch(numThreads);
        var client = HttpUtil.getHttpClient(10);
        var request = HttpRequest.newBuilder()
                .uri(uri).GET().build();
        var handlers = HttpResponse.BodyHandlers.ofString();

        for(int i=0; i<numThreads; i++) {
            Stereotypes.def().runOnce(() -> {
                for(int j=0; j<numCalls; j++) {
                    try {
                        client.send(request, handlers);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    //SystemUtils.sleep(5);
                }

                latch.countDown();
            });
        }

        latch.await();

        Assert.assertEquals(num.get(), numThreads*numCalls);

        System.out.println("Number of requests: " + num.get());
    }
}
