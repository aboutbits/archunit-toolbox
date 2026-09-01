package it.aboutbits.archunit.fixture.sortmappings.badnonstatic;

import it.aboutbits.springboot.toolbox.persistence.SortMappings;
import it.aboutbits.springboot.toolbox.stereotype.Store;

/// A non-static field cannot be read reflectively, so its mappings cannot be validated.
@Store("widget")
public class WidgetStore {
    private final SortMappings<WidgetSort> sortMappings = SortMappings.of(WidgetSort.NAME);

    public Object mappings() {
        return sortMappings;
    }
}
