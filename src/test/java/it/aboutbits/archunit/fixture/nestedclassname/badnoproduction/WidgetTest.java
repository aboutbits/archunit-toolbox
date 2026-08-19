package it.aboutbits.archunit.fixture.nestedclassname.badnoproduction;

import org.junit.jupiter.api.Nested;

/** No Widget class at all, so there is no method name to match DoWork against. */
class WidgetTest {
    @Nested
    class DoWork {
    }
}
