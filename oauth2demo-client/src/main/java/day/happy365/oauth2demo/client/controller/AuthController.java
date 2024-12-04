package day.happy365.oauth2demo.client.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/client")
public class AuthController {

    @GetMapping("/forward2Authorize")
    public void forward2Authorize(HttpServletResponse response) throws IOException {
        String redirectURL = """
                http://localhost:8000/auth-server/authorize?response_type=code
                &client_id=oauth2demo-client
                &redirect_uri=http://localhost:8001/client/index
                &scope=username
                &state=oauth2demo-client-state
                """;
        response.sendRedirect(redirectURL);
    }

    @GetMapping("/index")
    public ModelAndView index(@RequestParam String code,
                              @RequestParam String state) {
        String URL = "http://localhost:8000/auth-server/token?" +
                "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=http://localhost:8001/client/index" +
                "&client_id=oauth2demo-client" +
                "&client_secret=oauth2demo-client-secret";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        Map res = restTemplate.getForObject(URL, Map.class);
        assert res != null;
        String accessToken = (String) res.get("access_token");

        URL = "http://localhost:8000/auth-server/userInfo?accessToken=" + accessToken;
        Map map = restTemplate.getForObject(URL, Map.class);
        map.forEach((k, v) -> System.out.println(k + " = " + v));
        assert map != null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("username", map.get("username"));
        modelAndView.setViewName("index");
        return modelAndView;
    }

}
