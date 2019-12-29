package de.malkusch.autoebay.bidding.application;

import java.util.function.Supplier;

public final class ApplicationExceptions {

    public static Supplier<NotFoundException> notFound(String message) {
        return () -> new NotFoundException(message);
    }

    public static final class NotFoundException extends IllegalArgumentException {
        private static final long serialVersionUID = -3785868742425810982L;

        private NotFoundException(String message) {
            super(message);
        }
    }

    public static Supplier<NotAuthenticatedException> notAuthenticated(String message) {
        return () -> new NotAuthenticatedException(message);
    }

    public static final class NotAuthenticatedException extends IllegalArgumentException {
        private static final long serialVersionUID = -3785868742425810982L;

        private NotAuthenticatedException(String message) {
            super(message);
        }
    }
}
