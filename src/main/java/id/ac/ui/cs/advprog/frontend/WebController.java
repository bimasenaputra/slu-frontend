package id.ac.ui.cs.advprog.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class WebController {
    @GetMapping("/")
    public String getHomePage() {

        return "home.html";
    }

    @GetMapping("/createAccount")
    public String createUser(WebRequest request, Model model) {
        model.addAttribute("user", new UserAccDTO());
        return "createUser";
    }

    @PostMapping("/createAccount")
    public String createUserPost(UserAccDTO dto, Model model) {
        //
        return "home";
    }
}
