package de.malkusch.autoebay.test;

import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public class NullTransactionService implements TransactionService {

    @Override
    public <T, E extends Throwable> T tx(Operation<T, E> operation) throws E {
        return operation.run();
    }

    @Override
    public <T, E extends Throwable> T afterCurrentTx(Operation<T, E> operation) throws E {
        return operation.run();
    }
}
