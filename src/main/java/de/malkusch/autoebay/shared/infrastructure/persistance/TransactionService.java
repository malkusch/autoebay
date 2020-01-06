package de.malkusch.autoebay.shared.infrastructure.persistance;

import static de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService.IsolationLevel.DEFAULT;

public interface TransactionService {

    public static enum IsolationLevel {
        DEFAULT, SERIALIZABLE
    }

    public static interface Operation<T, E extends Throwable> {
        T run() throws E;
    }

    public static interface VoidOperation<E extends Throwable> {
        void run() throws E;
    }

    default <T, E extends Throwable> T tx(Operation<T, E> operation) throws E {
        return tx(DEFAULT, operation);
    }

    <T, E extends Throwable> T tx(IsolationLevel isolationLevel, Operation<T, E> operation) throws E;

    default <E extends Throwable> void tx(IsolationLevel isolationLevel, VoidOperation<E> operation) throws E {
        tx(isolationLevel, () -> {
            operation.run();
            return null;
        });
    }

    default <E extends Throwable> void afterCurrentTx(VoidOperation<E> operation) throws E {
        afterCurrentTx(operation);
    }

    <T, E extends Throwable> T afterCurrentTx(Operation<T, E> operation) throws E;
}
