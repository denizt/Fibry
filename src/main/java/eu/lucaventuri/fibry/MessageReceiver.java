package eu.lucaventuri.fibry;

import java.util.function.Predicate;

public interface MessageReceiver<T> {
    T readMessage();

    <E extends T> E receive(Class<E> clz, Predicate<E> filter);
}
