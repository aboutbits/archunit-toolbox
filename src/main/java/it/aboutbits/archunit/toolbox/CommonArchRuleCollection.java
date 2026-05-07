package it.aboutbits.archunit.toolbox;

import it.aboutbits.archunit.toolbox.rule.common.ControllerRequestMappingsMustBeSecurityTested;
import it.aboutbits.archunit.toolbox.rule.common.SortMappingsExhaustiveArchRule;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CommonArchRuleCollection extends
        ControllerRequestMappingsMustBeSecurityTested,
        SortMappingsExhaustiveArchRule {
}
