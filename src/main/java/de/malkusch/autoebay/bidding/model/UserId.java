package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

public final class UserId {

    private final String email;

    public UserId(String email) {
        this.email = requireNonNull(email);
        if (email.isBlank()) {
            throw new IllegalArgumentException("User Id must not be empty");
        }
    }

    @Override
    public String toString() {
        return email;
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserId)) {
            return false;
        }
        var other = (UserId) obj;
        return email.equals(other.email);
    }
}
