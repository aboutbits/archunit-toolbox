package it.aboutbits.archunit.fixture.nestedclassname.goodmetagroup;

import org.junit.jupiter.api.Nested;

class WidgetTest {
    /// Matches Widget.doWork(), so the rule has a nested class to actually check.
    @Nested
    class DoWork {
    }

    /// Groups tests only. Widget has no someGrouping() method, and must not be expected to.
    @Nested
    @TestGroup
    class SomeGrouping {
    }
}
