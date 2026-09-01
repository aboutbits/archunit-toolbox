package it.aboutbits.archunit.fixture.blacklistmethods.good;

import static org.assertj.core.api.Assertions.assertThat;

public class CallsAllowedAssertion {
    public void check() {
        assertThat("a").isEqualTo("a");
    }
}
