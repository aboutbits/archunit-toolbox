package it.aboutbits.archunit.fixture.securitytested.goodnestedgroup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WidgetController {
    @GetMapping("/widgets")
    public String getAll() {
        return "[]";
    }
}
