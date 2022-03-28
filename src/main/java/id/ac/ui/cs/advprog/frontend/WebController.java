package id.ac.ui.cs.advprog.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    public String getHomePage() {
        return "home.html";
    }
}
