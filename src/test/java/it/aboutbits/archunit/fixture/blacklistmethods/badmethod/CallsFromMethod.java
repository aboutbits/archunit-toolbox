package it.aboutbits.archunit.fixture.blacklistmethods.badmethod;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CallsFromMethod {
    public void check() {
        assertThatThrownBy(() -> {
            throw new IllegalStateException();
        });
    }
}
