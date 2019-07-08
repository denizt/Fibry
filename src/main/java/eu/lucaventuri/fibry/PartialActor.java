package eu.lucaventuri.fibry;

import java.util.function.Consumer;

public interface PartialActor<T, S> {
    void sendMessage(T message);

    void execAsync(Consumer<PartialActor<T, S>> worker);

    void execAsync(Runnable worker);

    void sendPoisonPill();

    S getState();

    void askExit();
}
