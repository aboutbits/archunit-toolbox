package it.aboutbits.archunit.fixture.securitytested.badmetagroup;

import org.junit.jupiter.api.Nested;

/// GetAll is marked, through the project's own stereotype, as organisational rather than a test of
/// getAll(). So getAll() is not covered and must be reported.
class WidgetControllerSecurityTest {
    @Nested
    @TestGroup
    class GetAll {
    }
}
