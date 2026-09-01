package it.aboutbits.archunit.fixture.nestedclassname.goodoptout;

import org.junit.jupiter.api.Nested;

/// A conforming test class, so the rule has something to select.
class WidgetTest {
    @Nested
    class DoWork {
    }
}
