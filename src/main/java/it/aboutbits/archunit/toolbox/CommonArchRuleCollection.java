package it.aboutbits.archunit.toolbox;

import it.aboutbits.archunit.toolbox.rule.common.ControllerRequestMappingsMustBeSecurityTested;
import it.aboutbits.archunit.toolbox.rule.common.SortMappingsExhaustiveArchRule;

public interface CommonArchRuleCollection extends
        ControllerRequestMappingsMustBeSecurityTested,
        SortMappingsExhaustiveArchRule {
}
