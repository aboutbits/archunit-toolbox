package it.aboutbits.archunit.fixture.blacklistmethods.badconstructor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CallsFromConstructor {
    public CallsFromConstructor() {
        assertThatThrownBy(() -> {
            throw new IllegalStateException();
        });
    }
}
