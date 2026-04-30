package it.aboutbits.archunit.toolbox;

import it.aboutbits.archunit.toolbox.rule.base.BlacklistAnnotationsArchRule;
import it.aboutbits.archunit.toolbox.rule.base.BlacklistClassesArchRule;
import it.aboutbits.archunit.toolbox.rule.base.BlacklistMethodsArchRule;
import it.aboutbits.archunit.toolbox.rule.base.EnforceJspecifyArchRule;
import it.aboutbits.archunit.toolbox.rule.base.NoSystemOutOrErrArchRule;
import it.aboutbits.archunit.toolbox.rule.base.RecordPropertiesMustBeAccessedViaAccessorArchRule;
import it.aboutbits.archunit.toolbox.rule.base.TestClassInCorrectPackageArchRule;
import it.aboutbits.archunit.toolbox.rule.base.TestClassVisibilityArchRule;
import it.aboutbits.archunit.toolbox.rule.base.TestMethodVisibilityArchRule;
import it.aboutbits.archunit.toolbox.rule.base.TestNestedClassMatchNameArchRule;
import it.aboutbits.archunit.toolbox.rule.base.TestNestedClassVisibilityArchRule;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface BaseArchRuleCollection extends
        BlacklistAnnotationsArchRule,
        BlacklistClassesArchRule,
        BlacklistMethodsArchRule,
        EnforceJspecifyArchRule,
        NoSystemOutOrErrArchRule,
        RecordPropertiesMustBeAccessedViaAccessorArchRule,
        TestClassInCorrectPackageArchRule,
        TestClassVisibilityArchRule,
        TestMethodVisibilityArchRule,
        TestNestedClassMatchNameArchRule,
        TestNestedClassVisibilityArchRule {
}
