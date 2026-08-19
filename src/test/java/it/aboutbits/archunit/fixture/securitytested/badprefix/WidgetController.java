package it.aboutbits.archunit.fixture.securitytested.badprefix;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WidgetController {
    @GetMapping("/widgets")
    public String getAll() {
        return "[]";
    }

    @GetMapping("/widgets/archived")
    public String getAllArchived() {
        return "[]";
    }
}
