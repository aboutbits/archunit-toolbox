package it.aboutbits.archunit.fixture.sortmappings.badnullvalue;

import it.aboutbits.springboot.toolbox.persistence.SortMappings;
import it.aboutbits.springboot.toolbox.stereotype.Store;

/// A field that reads back as null yields no mappings to compare, so the rule cannot verify it.
@Store("widget")
public class WidgetStore {
    static final SortMappings<WidgetSort> SORT_MAPPINGS = null;

    public Object mappings() {
        return SORT_MAPPINGS;
    }
}
