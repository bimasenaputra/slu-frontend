package id.ac.ui.cs.advprog.frontend.controller;

import id.ac.ui.cs.advprog.frontend.dto.JWTToken;
import id.ac.ui.cs.advprog.frontend.dto.ScheduleDTO;
import id.ac.ui.cs.advprog.frontend.dto.UserAccDTO;
import id.ac.ui.cs.advprog.frontend.dto.LoginForm;
import id.ac.ui.cs.advprog.frontend.util.CookieExtractor;
import id.ac.ui.cs.advprog.frontend.util.HttpHeadersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebController {
    @Resource(name = "sessionToken")
    private JWTToken token;

    private final RestTemplate restTemplate;

    @Autowired
    public WebController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @GetMapping("/")
    public String getHome(Model model) {
        if (token.idTokenIsEmpty()) {
            Map<String, String> message = new HashMap<>();
            message.put("code", "403 FORBIDDEN");
            return getUnauthenticatedHome(model, message);
        } else {
            try {
                return getAuthenticatedHome(model);
            } catch (HttpClientErrorException e) {
                if (!token.refreshTokenIsEmpty()) {
                    try {
                        HttpHeaders headers = HttpHeadersBuilder.build();
                        headers.set(HttpHeaders.COOKIE, token.getCookieRefresh());
                        HttpEntity<String> request = new HttpEntity<>("refreshToken", headers);
                        @SuppressWarnings("unchecked")
                        var response = restTemplate.postForEntity("api/account/auth/refresh",request,(Class<Map<String,Object>>)(Class)Map.class);
                        token.setToken(CookieExtractor.extract(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)));
                        return getAuthenticatedHome(model);
                    } catch (HttpClientErrorException ex) {
                        Map<String, String> message = new HashMap<>();
                        message.put("message", e.getResponseBodyAsString());
                        message.put("code", e.getStatusCode().toString());
                        return getUnauthenticatedHome(model, message);
                    }
                } else {
                    Map<String, String> message = new HashMap<>();
                    message.put("message", e.getResponseBodyAsString());
                    message.put("code", e.getStatusCode().toString());
                    return getUnauthenticatedHome(model, message);
                }
            }
        }
    }


    @PostMapping("/login")
    public String postLogin(@ModelAttribute("user") LoginForm dto, Model model) {
        HttpHeaders headers = HttpHeadersBuilder.build();
        HttpEntity<LoginForm> request = new HttpEntity<>(dto, headers);
        try {
            @SuppressWarnings("unchecked")
            var response = restTemplate.postForEntity("api/account/auth/login",request,(Class<Map<String,Object>>)(Class)Map.class);
            token.setToken(CookieExtractor.extract(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)));
            return "redirect:/";
        } catch (HttpClientErrorException e) {
            Map<String, String> message = new HashMap<>();
            message.put("message", e.getResponseBodyAsString());
            message.put("code", e.getStatusCode().toString());
            return getUnauthenticatedHome(model, message);
        }
    }

    private String getUnauthenticatedHome(Model model, Map<String, String> message) {
        model.addAttribute("user", new LoginForm());
        if (!message.get("code").equals("403 FORBIDDEN")) {
            var error = message.get("message").split("\"");
            model.addAttribute("taken", error[3]);
            model.addAttribute(message);
        }
        return "home";
    }

    private String getAuthenticatedHome(Model model) throws HttpClientErrorException {
        HttpHeaders headers = HttpHeadersBuilder.build();
        if (!token.idTokenIsEmpty()) headers.set(HttpHeaders.COOKIE, token.getCookie());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        var response = restTemplate.exchange("api/schedule/schedules", HttpMethod.GET, request, new ParameterizedTypeReference<Iterable<ScheduleDTO>>() {}).getBody();
        model.addAttribute("schedules", response);
        return "calendar";
    }

    @GetMapping("/createAccount")
    public String createUser(Model model) {
        model.addAttribute("user", new UserAccDTO());
        return "createUser";
    }

    @PostMapping("/createAccount")
    public String createUserPost(@ModelAttribute UserAccDTO dto, Model model, RedirectAttributes redirectAttributes) {
        HttpHeaders headers = HttpHeadersBuilder.build();
        HttpEntity<UserAccDTO> request = new HttpEntity<>(dto, headers);
        try {
            var response = restTemplate.postForEntity("api/account/auth/register", request, (Class<Map<String, Object>>) (Class) Map.class);
            redirectAttributes.addFlashAttribute("reg", "Account registered successfully. Please login here.");
            return "redirect:/";
        } catch (HttpClientErrorException e) {
            Map<String, String> message = new HashMap<>();
            message.put("message", e.getResponseBodyAsString());
            return failedRegister(model, message);
        }
    }

    public String failedRegister(Model model, Map<String, String> error) {
        model.addAttribute("user", new UserAccDTO());
        var errornya = error.get("message").split("\"");
        model.addAttribute("taken", errornya[3]);
        return "createUser";
    }

    @GetMapping("/logout")
    public String logout() {
        token.setRefreshToken(null);
        token.setIdToken(null);
        return "redirect:/";
    }

    @GetMapping("/createSchedule")
    public String createSchedule(Model model) {
        if (token.idTokenIsEmpty())
            return "redirect:/";
        model.addAttribute("sched", new ScheduleDTO());
        return "createSchedule";
    }

    @PostMapping("/createSchedule")
    public String createSchedulePost(@ModelAttribute ScheduleDTO dto, RedirectAttributes redirectAttributes) {
        HttpHeaders headers = HttpHeadersBuilder.build();

        if (!token.idTokenIsEmpty()) headers.set(HttpHeaders.COOKIE, token.getCookie());
        HttpEntity<ScheduleDTO> request = new HttpEntity<>(dto, headers);

        if (dto.getStartTime().compareTo(dto.getEndTime()) > 0) {
            redirectAttributes.addFlashAttribute("date", "Start time must be earlier than end time, please try again.");
            return "redirect:/createSchedule";
        }

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        var checkStartTime = restTemplate.exchange(
                "api/schedule/filter/" + dto.getStartTime(), HttpMethod.GET, requestEntity, Boolean.class);
        if (Boolean.FALSE.equals(checkStartTime.getBody())) {
            redirectAttributes.addFlashAttribute("date", "You already have a schedule with that start time, please try again.");
            return "redirect:/createSchedule";
        }

        var response = restTemplate.postForEntity("api/schedule/", request, Map.class);
        return "redirect:/";
    }

    @GetMapping("/schedule/{id}")
    public String getSchedule(@PathVariable String id, Model model) {
        HttpHeaders headers = HttpHeadersBuilder.build();
        if (!token.idTokenIsEmpty()) headers.set(HttpHeaders.COOKIE, token.getCookie());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            @SuppressWarnings("unchecked")
            var response = restTemplate.exchange("api/schedule/"+id, HttpMethod.GET, request,(Class<Map<String,Object>>)(Class)Map.class).getBody();
            var schedule = new ScheduleDTO();
            assert response != null;
            schedule.setTitle(response.get("title").toString());
            schedule.setStartTime(response.get("startTime").toString());
            schedule.setEndTime(response.get("endTime").toString());
            schedule.setStartingLoc(response.get("startingLoc").toString());
            schedule.setDestination(response.get("destination").toString());
            schedule.setDesc(response.get("desc").toString());
            model.addAttribute("schedule", schedule);
            model.addAttribute("id", response.get("id").toString());
            return "readSchedule";
        } catch (HttpClientErrorException | NullPointerException e) {
            System.out.println(e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/schedule/{id}/delete")
    public String deleteSchedule(@PathVariable String id, @ModelAttribute("schedule") ScheduleDTO schedule) {
        HttpHeaders headers = HttpHeadersBuilder.build();
        if (!token.idTokenIsEmpty()) headers.set(HttpHeaders.COOKIE, token.getCookie());
        HttpEntity<ScheduleDTO> request = new HttpEntity<>(schedule, headers);
        try {
            restTemplate.exchange("api/schedule/"+id, HttpMethod.DELETE, request, Void.class);
            return "redirect:/";
        } catch (HttpClientErrorException e) {
            return "redirect:/";
        }
    }

    @GetMapping("/schedule/{id}/update")
    public String updateSchedule(@PathVariable String id, Model model) {
        HttpHeaders headers = HttpHeadersBuilder.build();
        if (!token.idTokenIsEmpty()) headers.set(HttpHeaders.COOKIE, token.getCookie());
        HttpEntity<ScheduleDTO> request = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange("api/schedule/"+id, HttpMethod.GET, request,(Class<Map<String,Object>>)(Class)Map.class).getBody();
            var schedule = new ScheduleDTO();
            assert response != null;
            schedule.setTitle(response.get("title").toString());
            schedule.setStartTime(response.get("startTime").toString());
            schedule.setEndTime(response.get("endTime").toString());
            schedule.setStartingLoc(response.get("startingLoc").toString());
            schedule.setDestination(response.get("destination").toString());
            schedule.setDesc(response.get("desc").toString());
            model.addAttribute("schedule", schedule);
            model.addAttribute("id", response.get("id").toString());
            return "updateSchedule";
        } catch (HttpClientErrorException e) {
            return "redirect:/";
        }
    }

    @PostMapping("/schedule/{id}/update")
    public String updateSchedulePost(@PathVariable String id, @ModelAttribute("schedule") ScheduleDTO schedule, RedirectAttributes redirectAttributes) {
        HttpHeaders headers = HttpHeadersBuilder.build();
        if (!token.idTokenIsEmpty()) headers.set(HttpHeaders.COOKIE, token.getCookie());
        HttpEntity<ScheduleDTO> request = new HttpEntity<>(schedule, headers);

        if (schedule.getStartTime().compareTo(schedule.getEndTime()) > 0) {
            redirectAttributes.addFlashAttribute("date", "Start time must be earlier than end time, please try again.");
            return "redirect:/schedule/" + id + "/update";
        }

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        var checkStartTime = restTemplate.exchange(
                "api/schedule/filter/" + schedule.getStartTime() + "/" + schedule.getId(), HttpMethod.GET, requestEntity, Boolean.class);
        if (Boolean.FALSE.equals(checkStartTime.getBody())) {
            redirectAttributes.addFlashAttribute("date", "You already have a schedule with that start time, please try again.");
            return "redirect:/schedule/" + id + "/update";
        }


        try {
            restTemplate.exchange("api/schedule/"+id, HttpMethod.PUT, request, Void.class);
            return "redirect:/";
        } catch (HttpClientErrorException e) {
            return "redirect:/";
        }
    }
}
