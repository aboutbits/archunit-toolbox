package it.aboutbits.archunit.toolbox.rule.common;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@NullMarked
public interface SortMappingsExhaustiveArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void sort_mappings_cover_all_sort_enum_values(JavaClasses classes) {
        classes()
                .that()
                .areAnnotatedWith("it.aboutbits.springboot.toolbox.stereotype.Store")
                .should(new HaveExhaustiveSortMappingsIfPresent())
                .allowEmptyShould(true)
                .check(classes);
    }

    @Slf4j
    class HaveExhaustiveSortMappingsIfPresent extends ArchCondition<JavaClass> {
        public HaveExhaustiveSortMappingsIfPresent() {
            super("have SortMappings that map all values of the associated Sort enum");
        }

        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            var mappings = detectSortMappings(javaClass);

            if (mappings.fieldToKeyNames().isEmpty()) {
                return;
            }

            validateSortMappings(javaClass, events, mappings);
        }

        private static DetectedSortField detectSortMappings(JavaClass javaClass) {
            // Read SortMappings static fields, resolve their enum type, and collect key names
            var fieldToKeyNames = new HashMap<String, Set<String>>();
            var fieldToEnumClassName = new HashMap<String, String>();
            try {
                var runtimeClass = Class.forName(javaClass.getFullName());
                for (var field : javaClass.getFields()) {
                    if (field.getRawType().isAssignableTo(
                            "it.aboutbits.springboot.toolbox.persistence.SortMappings")) {
                        String enumClassName = null;

                        // Try to resolve enum type from the field's generic type parameter
                        var fieldType = field.getType();
                        if (fieldType instanceof JavaParameterizedType pt && !pt.getActualTypeArguments()
                                .isEmpty()) {
                            enumClassName = pt.getActualTypeArguments()
                                    .getFirst()
                                    .toErasure()
                                    .getFullName();
                        }

                        try {
                            var reflectField = runtimeClass.getDeclaredField(field.getName());
                            reflectField.setAccessible(true);
                            var value = reflectField.get(null);
                            if (value instanceof Map<?, ?> m) {
                                var keys = m.keySet();
                                var keyNames = keys.stream()
                                        .filter(k -> k instanceof Enum<?>)
                                        .map(k -> ((Enum<?>) k).name())
                                        .collect(Collectors.toSet());
                                fieldToKeyNames.put(field.getName(), keyNames);

                                // If generic info is missing, infer enum type from the first key
                                if (enumClassName == null) {
                                    var anyKey = keys.stream()
                                            .filter(k -> k instanceof Enum<?>)
                                            .findFirst();
                                    if (anyKey.isPresent()) {
                                        enumClassName = ((Enum<?>) anyKey.get()).getDeclaringClass()
                                                .getName();
                                    }
                                }
                            }
                        } catch (Exception _) {
                            // ignore fields we cannot read (non-static or other issues)
                            log.warn(
                                    "Failed to read SortMappings field {} in {} ({}.java:{})",
                                    field.getName(),
                                    javaClass.getFullName(),
                                    javaClass.getSimpleName(),
                                    field.getSourceCodeLocation().getLineNumber()
                            );
                        }

                        if (enumClassName != null) {
                            fieldToEnumClassName.put(field.getName(), enumClassName);
                        }
                    }
                }
            } catch (ClassNotFoundException _) {
                // ignore
                log.warn(
                        "Failed to resolve enum type for SortMappings field in {} ({}.java:{})",
                        javaClass.getFullName(),
                        javaClass.getSimpleName(),
                        javaClass.getSourceCodeLocation().getLineNumber()
                );
            }
            return new DetectedSortField(fieldToKeyNames, fieldToEnumClassName);
        }

        private static void validateSortMappings(
                JavaClass javaClass,
                ConditionEvents events,
                DetectedSortField mappings
        ) {
            // Validate each SortMappings field against its associated enum
            for (var entry : mappings.fieldToKeyNames().entrySet()) {
                var fieldName = entry.getKey();
                var keyNames = entry.getValue();
                var enumClassName = mappings.fieldToEnumClassName().get(fieldName);

                if (enumClassName == null) {
                    // Cannot determine enum type for this field; skip validation
                    log.warn(
                            "Cannot determine enum type for SortMappings field {} in {} ({}.java:{})",
                            fieldName,
                            javaClass.getFullName(),
                            javaClass.getSimpleName(),
                            javaClass.getSourceCodeLocation().getLineNumber()
                    );
                    continue;
                }

                try {
                    var enumClass = Class.forName(enumClassName);
                    if (enumClass.isEnum()) {
                        var enumConstants = (Enum<?>[]) enumClass.getEnumConstants();
                        var missing = Stream.of(enumConstants)
                                .map(Enum::name)
                                .filter(name -> !keyNames.contains(name))
                                .toList();
                        if (!missing.isEmpty()) {
                            // Try to get a precise field line number for the message
                            var fieldLine = javaClass.getFields().stream()
                                    .filter(f -> f.getName().equals(fieldName))
                                    .filter(f -> f.getRawType()
                                            .isAssignableTo("it.aboutbits.springboot.toolbox.persistence.SortMappings"))
                                    .findFirst()
                                    .map(f -> f.getSourceCodeLocation().getLineNumber())
                                    .orElse(-1);

                            var message = String.format(
                                    "Class %s: SortMappings field %s is missing mappings for enum %s values %s (%s.java:%d)",
                                    javaClass.getFullName(),
                                    fieldName,
                                    enumClass.getSimpleName(),
                                    missing,
                                    javaClass.getSimpleName(),
                                    fieldLine
                            );
                            events.add(SimpleConditionEvent.violated(javaClass, message));
                        }
                    }
                } catch (ClassNotFoundException _) {
                    // ignore
                    log.warn(
                            "Failed to resolve enum type for SortMappings field {} in {} ({}.java:{})",
                            fieldName,
                            javaClass.getFullName(),
                            javaClass.getSimpleName(),
                            javaClass.getSourceCodeLocation().getLineNumber()
                    );
                }
            }
        }

        private record DetectedSortField(
                HashMap<String, Set<String>> fieldToKeyNames,
                HashMap<String, String> fieldToEnumClassName
        ) {
        }
    }
}
