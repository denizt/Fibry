package eu.lucaventuri.fibry;

import eu.lucaventuri.common.SystemUtils;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import static org.junit.Assert.*;

public class TestAutoScaling {
    @Test
    public void testFixedSize() throws ExecutionException, InterruptedException {
        Set<Thread> actors = new HashSet<>();
        CountDownLatch latch = new CountDownLatch(3);
        PoolActorLeader<String, Void, String> leader = (PoolActorLeader<String, Void, String>) ActorSystem.anonymous().strategy(CreationStrategy.THREAD).<String>poolParams(PoolParameters.fixedSize(3), null).<String>newPool(msg -> {
            actors.add(Thread.currentThread());
            latch.countDown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        assertEquals(3, leader.getGroupExit().size());
        assertEquals(0, actors.size());

        leader.sendMessageReturn("A");
        leader.sendMessageReturn("B");
        leader.sendMessageReturn("C");

        latch.await();

        assertEquals(3, leader.getGroupExit().size());
        assertEquals(3, actors.size());
    }

    @Test
    public void testScaling() throws ExecutionException, InterruptedException {
        int maxActors = 10;
        Set<Thread> actors = new HashSet<>();
        PoolActorLeader<String, Void, String> leader = (PoolActorLeader<String, Void, String>) ActorSystem.anonymous().strategy(CreationStrategy.THREAD).<String>poolParams(PoolParameters.scaling(3, maxActors, 1, 0, 1, 5), null).<String>newPool(msg -> {
            actors.add(Thread.currentThread());
            SystemUtils.sleep(30);
        });

        assertEquals(3, leader.getGroupExit().size());
        assertEquals(0, actors.size());

        CompletableFuture[] msgFirstRound = new CompletableFuture[maxActors];
        CompletableFuture[] msgSecondRound = new CompletableFuture[maxActors*2];

        for (int i = 0; i < maxActors; i++)
            msgFirstRound[i] = leader.sendMessageReturn("A");

        for (int i = 0; i < maxActors * 2; i++)
            msgSecondRound[i] = leader.sendMessageReturn("A");

        CompletableFuture.allOf(msgFirstRound).get();

        assertEquals(maxActors, leader.getGroupExit().size());
        assertTrue(leader.getQueueLength()>0);
        assertEquals(maxActors, leader.getGroupExit().size());

        // Wait for the queue to go down
        while (leader.getQueueLength() > 0)
            SystemUtils.sleep(1);

        assertEquals(leader.getQueueLength(), 0);

        // Give time to the autoscaling to resize down the pool
        SystemUtils.sleep(200);

        // Resized down
        assertEquals(3, leader.getGroupExit().size());
    }
}
