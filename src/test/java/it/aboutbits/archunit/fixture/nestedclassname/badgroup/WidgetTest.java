package it.aboutbits.archunit.fixture.nestedclassname.badgroup;

import org.junit.jupiter.api.Nested;

/// The group implies a production class Widget$DeleteAction, which does not exist.
class WidgetTest {
    @Nested
    class DeleteAction {
        @Nested
        class DeleteAll {
        }
    }
}
