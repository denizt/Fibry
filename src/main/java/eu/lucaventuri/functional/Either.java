package eu.lucaventuri.functional;

import eu.lucaventuri.common.ConsumerEx;
import eu.lucaventuri.common.Exceptions;

import java.util.Optional;

/** Functional Either, containing or one type or another */
public class Either<L, R> {
    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> left(L value) {
        Exceptions.assertAndThrow(value != null, "Left value is null!");

        return new Either<L, R>(value, null);
    }

    public static <L, R> Either<L, R> right(R value) {
        Exceptions.assertAndThrow(value != null, "Right value is null!");

        return new Either<L, R>(null, value);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    public Optional<L> leftOpt() {
        return Optional.ofNullable(left);
    }

    public Optional<R> rightOpt() {
        return Optional.ofNullable(right);
    }

    public <E extends Throwable> void ifLeft(ConsumerEx<L, E> consumer) throws E {
        if (left != null)
            consumer.accept(left);
    }

    public <E extends Throwable> void ifRight(ConsumerEx<R, E> consumer) throws E {
        if (right != null)
            consumer.accept(right);
    }

    public <E extends Throwable> void ifEither(ConsumerEx<L, E> consumerLeft, ConsumerEx<R, E> consumerRight) throws E {
        if (left != null)
            consumerLeft.accept(left);
        else
            consumerRight.accept(right);
    }
}
