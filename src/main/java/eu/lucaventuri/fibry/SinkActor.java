package eu.lucaventuri.fibry;

public interface SinkActor<S> {
    void execAsync(Runnable worker);

    S getState();

    void askExit();

    boolean isExiting();

    void waitForExit();
}
