package id.ac.ui.cs.advprog.frontend;

import id.ac.ui.cs.advprog.frontend.DTO.UserAccDTO;
import id.ac.ui.cs.advprog.frontend.DTO.UserLoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

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
        model.addAttribute("taken", null);
        return "createUser";
    }

    @PostMapping("/createAccount")
    public String createUserPost(@ModelAttribute UserAccDTO dto, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String urlusername = "http://localhost:8082/api/account/getUser/";
        String urluemail = "http://localhost:8082/api/account/getUserByEmail/";
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.getForEntity(urlusername + dto.getUsername(), String.class);
        } catch (Exception e) {
            try {
                response = restTemplate.getForEntity(urluemail + dto.getEmail(), String.class);
            }
            catch (Exception ex) {
                // TODO: Handle register
                model.addAttribute("user", new UserLoginDTO());
                return "home";
            }
            model.addAttribute("user", new UserAccDTO());
            model.addAttribute("taken", "\"" + dto.getEmail() + "\" is already in use.");
            return "createUser";
        }

        try {
            response = restTemplate.getForEntity(urluemail + dto.getEmail(), String.class);
        }
        catch (Exception ex) {
            model.addAttribute("user", new UserAccDTO());
            model.addAttribute("taken", "Username \"" + dto.getUsername() + "\" is already in taken.");
            return "createUser";
        }

        model.addAttribute("user", new UserAccDTO());
        model.addAttribute("taken", "Username \"" + dto.getUsername() + "\" and email \"" + dto.getEmail()  + "\" are already in use.");
        return "createUser";
    }
}
