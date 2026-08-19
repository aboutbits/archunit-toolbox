package it.aboutbits.archunit.fixture.securitytested.badprefix;

import org.junit.jupiter.api.Nested;

/**
 * Only getAllArchived() is security tested. A prefix match would let this @Nested class stand in
 * for getAll() as well.
 */
class WidgetControllerSecurityTest {
    @Nested
    class GetAllArchived {
    }
}
