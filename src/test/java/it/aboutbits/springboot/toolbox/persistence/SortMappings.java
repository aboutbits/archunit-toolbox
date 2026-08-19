package it.aboutbits.springboot.toolbox.persistence;

import java.util.HashMap;

/**
 * Stub of the spring-boot-toolbox type. Verified against the real artifact: it extends HashMap keyed
 * by the Sort enum, which is what the rule relies on when it reads the mappings reflectively.
 */
public class SortMappings<T extends Enum<?>> extends HashMap<T, Object> {
    @SafeVarargs
    public static <T extends Enum<?>> SortMappings<T> of(T... keys) {
        var mappings = new SortMappings<T>();
        for (var key : keys) {
            mappings.put(key, key.name());
        }
        return mappings;
    }
}
