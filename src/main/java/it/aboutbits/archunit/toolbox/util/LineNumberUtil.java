package it.aboutbits.archunit.toolbox.util;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.core.domain.JavaStaticInitializer;
import com.tngtech.archunit.core.domain.properties.HasSourceCodeLocation;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@NullMarked
public final class LineNumberUtil {
    private LineNumberUtil() {
    }

    public static int getLineNumber(HasSourceCodeLocation element) {
        return element.getSourceCodeLocation().getLineNumber();
    }

    // JavaClass overload with constructor fallback: class-level SLOC is 0 for nested/anonymous classes
    public static int getLineNumber(JavaClass javaClass) {
        var lineNumber = javaClass.getSourceCodeLocation().getLineNumber();
        if (lineNumber != 0) {
            return lineNumber;
        }
        try {
            return javaClass.getConstructors().iterator().next().getSourceCodeLocation().getLineNumber();
        } catch (NoSuchElementException _) {
            return 0;
        }
    }

    public static List<LineNumberType> getDependencyUsageLineNumberTypes(
            JavaClass originClass,
            JavaClass targetClass
    ) {
        var originClassConstructors = originClass.getConstructors();

        // Check the usage of the target class in the fields of the origin class
        var javaFieldLines = originClass.getFields()
                .stream()
                .filter(field -> field.getType().equals(targetClass))
                .map(field -> LineNumberType.of(
                        field.getSourceCodeLocation().getLineNumber() == 0
                                ? originClassConstructors.iterator().next().getSourceCodeLocation().getLineNumber()
                                : field.getSourceCodeLocation().getLineNumber(),
                        "Field"
                ));

        // Check the usage of the target class in the static initializers of the origin class
        var javaStaticInitializerLines = originClass.getStaticInitializer()
                .map(javaStaticInitializer -> getDependencyJavaCodeUnitLineNumbers(
                        Set.of(javaStaticInitializer),
                        targetClass
                ))
                .orElse(Stream.empty());

        // Check the usage of target class in the constructors of the origin class
        var javaConstructorLines = getDependencyJavaCodeUnitLineNumbers(
                originClassConstructors,
                targetClass
        );

        // Check the usage of target class in the methods of the origin class
        var javaMethodLines = getDependencyJavaCodeUnitLineNumbers(
                originClass.getMethods(),
                targetClass
        );

        var lines = Stream.of(
                javaFieldLines,
                javaStaticInitializerLines,
                javaConstructorLines,
                javaMethodLines
        ).flatMap(Function.identity()).toList();

        if (!lines.isEmpty()) {
            return lines;
        }

        return List.of(LineNumberType.of(0, "Unknown Usage Type Fix Me"));
    }

    private static Stream<LineNumberType> getDependencyJavaCodeUnitLineNumbers(
            Set<? extends JavaCodeUnit> codeUnits,
            JavaClass targetClass
    ) {
        return codeUnits.stream()
                .map(codeUnit -> {
                    var codeUnitType = switch (codeUnit) {
                        case JavaMethod _ -> "Method";
                        case JavaConstructor _ -> "Constructor";
                        case JavaStaticInitializer _ -> "Static Initializer";
                        default -> "Unknown Code Unit Type Fix Me";
                    };

                    // Check if the Method/Constructor has a parameter of the target class
                    var parameterPresent = codeUnit.getParameters()
                            .stream()
                            .anyMatch(parameter -> parameter.getType().equals(targetClass));

                    // Check if the Method/Constructor has a type parameter of the target class
                    var typeVariablePresent = codeUnit.getTypeParameters()
                            .stream()
                            .anyMatch(parameter -> parameter.toErasure().equals(targetClass));

                    // Check if the Method/Constructor returns the target class
                    var returnType = codeUnit.getReturnType();
                    var actualReturnType = returnType instanceof JavaParameterizedType pReturnType
                            ? pReturnType.getActualTypeArguments().getFirst()
                            : returnType;
                    var returnTypePresent = actualReturnType.equals(targetClass);

                    // Check if the Method/Constructor calls a constructor of the target class
                    var javaConstructorCallLines = codeUnit
                            .getConstructorCallsFromSelf()
                            .stream()
                            .filter(constructorCall -> constructorCall.getTargetOwner().equals(targetClass))
                            .map(constructorCall -> LineNumberType.of(
                                    constructorCall.getSourceCodeLocation().getLineNumber(),
                                    "Constructor Call"
                            ));

                    // Check if the Method/Constructor calls a method of the target class
                    var javaMethodCallLines = codeUnit
                            .getMethodCallsFromSelf()
                            .stream()
                            .filter(methodCall -> methodCall.getTargetOwner().equals(targetClass))
                            .map(methodCall -> LineNumberType.of(
                                    methodCall.getSourceCodeLocation().getLineNumber(),
                                    "Method Call"
                            ));

                    return Stream.of(
                            parameterPresent
                                    ? Stream.of(
                                    LineNumberType.of(
                                            codeUnit.getSourceCodeLocation().getLineNumber(),
                                            codeUnitType + " Parameter"
                                    ))
                                    : Stream.<LineNumberType>empty(),
                            typeVariablePresent
                                    ? Stream.of(
                                    LineNumberType.of(
                                            codeUnit.getSourceCodeLocation().getLineNumber(),
                                            codeUnitType + " Generic Parameter"
                                    ))
                                    : Stream.<LineNumberType>empty(),
                            returnTypePresent
                                    ? Stream.of(
                                    LineNumberType.of(
                                            codeUnit.getSourceCodeLocation().getLineNumber(),
                                            "Method Return Type"
                                    ))
                                    : Stream.<LineNumberType>empty(),
                            javaConstructorCallLines,
                            javaMethodCallLines
                    ).flatMap(Function.identity()).toList();
                })
                .flatMap(List::stream);
    }

    public record LineNumberType(
            int lineNumber,
            String type
    ) {
        public static LineNumberType of(int lineNumber, String type) {
            return new LineNumberType(lineNumber, type);
        }
    }
}
