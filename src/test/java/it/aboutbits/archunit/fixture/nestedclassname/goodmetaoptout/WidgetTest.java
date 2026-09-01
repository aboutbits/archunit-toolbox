package it.aboutbits.archunit.fixture.nestedclassname.goodmetaoptout;

import org.junit.jupiter.api.Nested;

/// A conforming test class, so the rule has something to select.
class WidgetTest {
    @Nested
    class DoWork {
    }
}
