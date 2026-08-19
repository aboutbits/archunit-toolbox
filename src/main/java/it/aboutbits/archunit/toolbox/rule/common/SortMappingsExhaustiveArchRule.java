package it.aboutbits.archunit.toolbox.rule.common;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static it.aboutbits.archunit.toolbox.util.LineNumberUtil.getLineNumber;

@NullMarked
public interface SortMappingsExhaustiveArchRule {
    String SORT_MAPPINGS_CLASS = "it.aboutbits.springboot.toolbox.persistence.SortMappings";

    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void sort_mappings_cover_all_sort_enum_values(JavaClasses classes) {
        classes()
                .that()
                .areAnnotatedWith("it.aboutbits.springboot.toolbox.stereotype.Store")
                .should(new HaveExhaustiveSortMappingsIfPresent())
                .check(classes);
    }

    /**
     * Checks that every value of a Sort enum has a mapping.
     * <p>
     * Every case this cannot verify is reported as a violation rather than skipped. Reading a
     * mapping requires reflection, and a mapping that cannot be read is indistinguishable from one
     * that is exhaustive - so silence here means the rule quietly stops covering that field.
     * </p>
     */
    class HaveExhaustiveSortMappingsIfPresent extends ArchCondition<JavaClass> {
        public HaveExhaustiveSortMappingsIfPresent() {
            super("have SortMappings that map all values of the associated Sort enum");
        }

        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            var sortMappingsFields = javaClass.getFields()
                    .stream()
                    .filter(field -> field.getRawType().isAssignableTo(SORT_MAPPINGS_CLASS))
                    .toList();

            // A @Store that does not sort is not a violation, there is simply nothing to check.
            if (sortMappingsFields.isEmpty()) {
                return;
            }

            Class<?> runtimeClass;
            try {
                runtimeClass = Class.forName(javaClass.getFullName());
            } catch (ClassNotFoundException | LinkageError _) {
                violated(events, javaClass, javaClass,
                        "cannot be loaded by the architecture test, so its SortMappings fields cannot be validated");
                return;
            }

            for (var field : sortMappingsFields) {
                checkField(javaClass, runtimeClass, field, events);
            }
        }

        private void checkField(
                JavaClass javaClass,
                Class<?> runtimeClass,
                JavaField field,
                ConditionEvents events
        ) {
            if (!field.getModifiers().contains(JavaModifier.STATIC)) {
                violated(events, javaClass, field,
                        "SortMappings field %s must be static, otherwise its mappings cannot be read and validated"
                                .formatted(field.getName()));
                return;
            }

            Map<?, ?> mappings;
            try {
                var reflectField = runtimeClass.getDeclaredField(field.getName());
                reflectField.setAccessible(true);
                var value = reflectField.get(null);
                if (!(value instanceof Map<?, ?> readMappings)) {
                    violated(events, javaClass, field,
                            "SortMappings field %s did not yield a Map (got %s), so its mappings cannot be validated"
                                    .formatted(field.getName(), value == null ? "null" : value.getClass().getName()));
                    return;
                }
                mappings = readMappings;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                violated(events, javaClass, field,
                        "SortMappings field %s could not be read (%s: %s), so its mappings cannot be validated"
                                .formatted(field.getName(), e.getClass().getSimpleName(), e.getMessage()));
                return;
            }

            var enumClassName = resolveEnumClassName(field, mappings);
            if (enumClassName.isEmpty()) {
                violated(events, javaClass, field,
                        "the Sort enum type of SortMappings field %s cannot be determined, so its mappings cannot be validated"
                                .formatted(field.getName()));
                return;
            }

            Class<?> enumClass;
            try {
                enumClass = Class.forName(enumClassName.get());
            } catch (ClassNotFoundException | LinkageError _) {
                violated(events, javaClass, field,
                        "the Sort enum %s of SortMappings field %s cannot be loaded, so its mappings cannot be validated"
                                .formatted(enumClassName.get(), field.getName()));
                return;
            }

            if (!enumClass.isEnum()) {
                violated(events, javaClass, field,
                        "the key type %s of SortMappings field %s is not an enum, so its mappings cannot be validated"
                                .formatted(enumClass.getName(), field.getName()));
                return;
            }

            var mappedNames = enumNames(mappings.keySet());
            var missing = Stream.of((Enum<?>[]) enumClass.getEnumConstants())
                    .map(Enum::name)
                    .filter(name -> !mappedNames.contains(name))
                    .toList();

            if (!missing.isEmpty()) {
                violated(events, javaClass, field,
                        "SortMappings field %s is missing mappings for enum %s values %s"
                                .formatted(field.getName(), enumClass.getSimpleName(), missing));
            }
        }

        private static Optional<String> resolveEnumClassName(JavaField field, Map<?, ?> mappings) {
            // Prefer the declared generic type parameter, e.g. SortMappings<WidgetSort>
            if (field.getType() instanceof JavaParameterizedType parameterizedType
                    && !parameterizedType.getActualTypeArguments().isEmpty()) {
                return Optional.of(parameterizedType.getActualTypeArguments()
                        .getFirst()
                        .toErasure()
                        .getFullName());
            }

            // Fall back to the runtime type of any mapped key
            return mappings.keySet()
                    .stream()
                    .filter(Enum.class::isInstance)
                    .map(key -> ((Enum<?>) key).getDeclaringClass().getName())
                    .findFirst();
        }

        private static Set<String> enumNames(Set<?> keys) {
            return keys.stream()
                    .filter(Enum.class::isInstance)
                    .map(key -> ((Enum<?>) key).name())
                    .collect(Collectors.toSet());
        }

        private static void violated(
                ConditionEvents events,
                JavaClass javaClass,
                Object violatingElement,
                String detail
        ) {
            var lineNumber = violatingElement instanceof JavaField field
                    ? getLineNumber(field)
                    : getLineNumber(javaClass);

            events.add(SimpleConditionEvent.violated(
                    violatingElement,
                    "Class %s: %s (%s.java:%d)".formatted(
                            javaClass.getFullName(),
                            detail,
                            javaClass.getSimpleName(),
                            lineNumber
                    )
            ));
        }
    }
}
