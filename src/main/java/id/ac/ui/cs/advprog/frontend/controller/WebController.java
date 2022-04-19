package id.ac.ui.cs.advprog.frontend.controller;

import id.ac.ui.cs.advprog.frontend.dto.JWTToken;
import id.ac.ui.cs.advprog.frontend.dto.UserAccDTO;
import id.ac.ui.cs.advprog.frontend.dto.LoginForm;
import id.ac.ui.cs.advprog.frontend.util.CookieExtractor;
import id.ac.ui.cs.advprog.frontend.util.HttpHeadersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class WebController {
    @Resource(name = "sessionToken")
    private JWTToken token;

    private RestTemplate restTemplate;

    @Autowired
    public WebController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @GetMapping("/")
    public String getHome(Model model) {
        try {
            return getAuthenticatedHome();
        } catch (HttpClientErrorException e) {
            if (!token.refreshTokenIsEmpty()) {
                // TODO: implement re-authentication / refresh token rotation
                return getAuthenticatedHome();
            }
            return getUnauthenticatedHome(model);
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/auth/login")
    public String postLogin(@ModelAttribute("user") LoginForm dto) {
        HttpHeaders headers = HttpHeadersBuilder.build();
        HttpEntity<LoginForm> request = new HttpEntity<>(dto, headers);
        var response = restTemplate.postForEntity("api/account/auth/login/test",request,(Class<Map<String,Object>>)(Class)Map.class);
        try {
            token.setToken(CookieExtractor.extract(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)));
            return "redirect:/";
        } catch (NullPointerException e) {
            // TODO: Handle invalid email/password/timeout
            return "test";
        }
    }

    private String getUnauthenticatedHome(Model model) {
        model.addAttribute("user", new LoginForm());
        model.addAttribute("taken", null);
        return "home";
    }

    private String getAuthenticatedHome() throws HttpClientErrorException {
        HttpHeaders headers = HttpHeadersBuilder.build();
        if (!token.idTokenIsEmpty()) headers.set(HttpHeaders.COOKIE, token.getCookie());
        HttpEntity<String> request = new HttpEntity<>("Success",headers);
        var response = restTemplate.postForEntity("api/account/auth/logout",request,String.class);
        System.out.println(response.getBody());
        return "calendar";
    }

    @GetMapping("/createAccount")
    public String createUser(Model model) {
        model.addAttribute("user", new UserAccDTO());
        return "createUser";
    }

    @PostMapping("/createAccount")
    public String createUserPost(@ModelAttribute UserAccDTO dto, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String urlusername = "http://localhost:8081/api/account/getUser/";
        String urluemail = "http://localhost:8081/api/account/getUserByEmail/";
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.getForEntity(urlusername + dto.getUsername(), String.class);
        } catch (Exception e) {
            try {
                response = restTemplate.getForEntity(urluemail + dto.getEmail(), String.class);
            }
            catch (Exception ex) {
                // TODO: Handle register
                model.addAttribute("user", new LoginForm());
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
            model.addAttribute("taken", "Username \"" + dto.getUsername() + "\" is already taken.");
            return "createUser";
        }

        model.addAttribute("user", new UserAccDTO());
        model.addAttribute("taken", "Username \"" + dto.getUsername() + "\" and email \"" + dto.getEmail()  + "\" are already in use.");
        return "createUser";
    }
}
