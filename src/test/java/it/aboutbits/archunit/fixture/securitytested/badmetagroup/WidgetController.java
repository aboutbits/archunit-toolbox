package it.aboutbits.archunit.fixture.securitytested.badmetagroup;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WidgetController {
    @GetMapping("/widgets")
    public String getAll() {
        return "[]";
    }
}
