package it.aboutbits.archunit.fixture.nestedclassname.badmethod;

import org.junit.jupiter.api.Nested;

/** Widget has no doSomethingElse() method. */
class WidgetTest {
    @Nested
    class DoSomethingElse {
    }
}
