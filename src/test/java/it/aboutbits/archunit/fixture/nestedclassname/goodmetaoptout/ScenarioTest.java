package it.aboutbits.archunit.fixture.nestedclassname.goodmetaoptout;

import org.junit.jupiter.api.Nested;

/// Opted out through the project's stereotype: its @Nested classes have no production methods to
/// be matched against either.
@BusinessScenario
class ScenarioTest {
    @Nested
    class SomeBehaviour {
    }
}
