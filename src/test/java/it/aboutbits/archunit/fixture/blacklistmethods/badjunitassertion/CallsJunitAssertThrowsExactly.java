package it.aboutbits.archunit.fixture.blacklistmethods.badjunitassertion;

import org.junit.jupiter.api.Assertions;

public class CallsJunitAssertThrowsExactly {
    public void check() {
        Assertions.assertThrowsExactly(IllegalStateException.class, () -> {
            throw new IllegalStateException();
        });
    }
}
