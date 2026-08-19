package it.aboutbits.archunit.fixture.nestedclassname.goodoptout;

import it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart;
import org.junit.jupiter.api.Nested;

/**
 * Declares that it has no production counterpart, so its @Nested classes have no production methods
 * to be matched against either.
 */
@ArchIgnoreNoProductionCounterpart
class ScenarioTest {
    @Nested
    class SomeBehaviour {
    }
}
