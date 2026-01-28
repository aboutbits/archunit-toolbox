package it.aboutbits.archunit.toolbox;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullUnmarked;

@Slf4j
@NullUnmarked
final class InnertRule implements ArchRule {
    @Override
    public void check(JavaClasses javaClasses) {
        log.info("Rule disabled by config.");
    }

    @Override
    public ArchRule because(String s) {
        return null;
    }

    @Override
    public ArchRule allowEmptyShould(boolean b) {
        return null;
    }

    @Override
    public ArchRule as(String s) {
        return null;
    }

    @Override
    public EvaluationResult evaluate(JavaClasses javaClasses) {
        return null;
    }

    @Override
    public String getDescription() {
        return "";
    }
}
