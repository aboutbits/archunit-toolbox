package it.aboutbits.archunit.fixture.securitytested.goodnestedgroup;

import it.aboutbits.archunit.toolbox.support.ArchIgnoreGroupName;
import org.junit.jupiter.api.Nested;

/**
 * getAll() is covered by a @Nested class grouped inside the method-named class. Also exercises
 * @Controller rather than @RestController.
 */
class WidgetControllerSecurityTest {
    @Nested
    @ArchIgnoreGroupName
    class GetAll {
        @Nested
        class WhenAdmin {
        }
    }
}
