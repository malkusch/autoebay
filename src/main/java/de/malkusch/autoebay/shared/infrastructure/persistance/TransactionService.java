package de.malkusch.autoebay.shared.infrastructure.persistance;

import static de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService.IsolationLevel.DEFAULT;

public interface TransactionService {

    public static enum IsolationLevel {
        DEFAULT, SERIALIZABLE
    }

    void tx(Runnable operation);

    default void tx(IsolationLevel isolationLevel, Runnable operation) {
        tx(DEFAULT, operation);
    }

}
