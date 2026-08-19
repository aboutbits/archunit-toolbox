package it.aboutbits.archunit.fixture.sortmappings.good;

import it.aboutbits.springboot.toolbox.persistence.SortMappings;
import it.aboutbits.springboot.toolbox.stereotype.Store;

@Store("widget")
public class WidgetStore {
    static final SortMappings<WidgetSort> SORT_MAPPINGS = SortMappings.of(
            WidgetSort.NAME,
            WidgetSort.CREATED_AT
    );

    public Object mappings() {
        return SORT_MAPPINGS;
    }
}
