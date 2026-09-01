package it.aboutbits.archunit.fixture.nestedclassname.goodgroup;

import org.junit.jupiter.api.Nested;

/// The @Nested group maps onto the production nested class Widget$DeleteAction, so the lookup has to
/// resolve a fully qualified name containing a '$'.
class WidgetTest {
    @Nested
    class DeleteAction {
        @Nested
        class DeleteAll {
        }
    }
}
