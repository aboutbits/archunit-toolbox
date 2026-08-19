package it.aboutbits.archunit.fixture.sortmappings.bad;

import it.aboutbits.springboot.toolbox.persistence.SortMappings;
import it.aboutbits.springboot.toolbox.stereotype.Store;

/** CREATED_AT has no mapping. */
@Store("widget")
public class WidgetStore {
    static final SortMappings<WidgetSort> SORT_MAPPINGS = SortMappings.of(WidgetSort.NAME);

    public Object mappings() {
        return SORT_MAPPINGS;
    }
}
