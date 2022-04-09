package id.ac.ui.cs.advprog.frontend;

import id.ac.ui.cs.advprog.frontend.DTO.UserAccDTO;
import id.ac.ui.cs.advprog.frontend.DTO.UserLoginDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class WebController {
    @GetMapping("/")
    public String getHome(Model model) {
        model.addAttribute("user", new UserLoginDTO());
        return "home";
    }

    @PostMapping("/")
    public String postHome(@ModelAttribute UserLoginDTO dto) {
        // TODO: Handle login
        return "home";
    }

    @GetMapping("/createAccount")
    public String createUser(Model model) {
        model.addAttribute("user", new UserAccDTO());
        return "createUser";
    }

    @PostMapping("/createAccount")
    public String createUserPost(@ModelAttribute UserAccDTO dto) {
        // TODO: Handle register
        return "home";
    }
}
