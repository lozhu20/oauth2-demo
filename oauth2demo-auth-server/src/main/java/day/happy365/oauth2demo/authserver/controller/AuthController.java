package day.happy365.oauth2demo.authserver.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/auth-server")
public class AuthController {

    private static final Map<String, String> CACHE_MAP = new HashMap<>();

    private static final Map<String, String> TOKEN_MAP = new HashMap<>();

    private static final Map<String, String> USER_INFO = Map.of("xinxiaoyan", "123456");

    @GetMapping("/authorize")
    public ModelAndView authorize(@RequestParam String response_type,
                                  @RequestParam String client_id,
                                  @RequestParam String redirect_uri,
                                  @RequestParam String scope,
                                  @RequestParam String state) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("response_type", response_type);
        modelAndView.addObject("client_id", client_id);
        modelAndView.addObject("redirect_uri", redirect_uri);
        modelAndView.addObject("scope", scope);
        modelAndView.addObject("state", "state");
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/login")
    public void login(@RequestParam String username,
                      @RequestParam String password,
                      @RequestParam String response_type,
                      @RequestParam String client_id,
                      @RequestParam String redirect_uri,
                      @RequestParam String scope,
                      @RequestParam String state,
                      HttpServletResponse response) throws IOException {
        String passwd = USER_INFO.get(username);
        if (passwd == null || "".equals(passwd) || !passwd.equals(password)) {
            // 用户名和密码错误，重定向到首页
            response.sendRedirect("http://localhost:8000/auth-server/login");
            return;
        }

        Random random = new Random();
        int code = random.nextInt(100);
        CACHE_MAP.put(String.valueOf(code), username);

        String redirectURL = redirect_uri + "?code=" + code + "&state=" + state;
        redirectURL = redirectURL.replaceAll("\\s", "");
        response.sendRedirect(redirectURL);
    }

    @GetMapping("/token")
    @ResponseBody
    public Map<String, Object> getTokenByCode(@RequestParam String grant_type,
                                              @RequestParam String code,
                                              @RequestParam String redirect_uri,
                                              @RequestParam String client_id,
                                              @RequestParam String client_secret) {
        Map<String, Object> map = new HashMap<>();
        if (!CACHE_MAP.containsKey(code)) {
            map.put("message", "授权码错误");
            return map;
        }

        CACHE_MAP.remove(code);

        String token = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();

        TOKEN_MAP.put(token, CACHE_MAP.get(code));

        map.put("access_token", token);
        map.put("token_type", "bearer");
        map.put("expires_in", 600);
        map.put("refresh_token", refreshToken);
        map.put("scope", "username");
        return map;
    }

    @GetMapping("/userInfo")
    @ResponseBody
    public Map<String, String> getUserInfo(@RequestParam String accessToken,
                                           HttpServletResponse response) throws IOException {
        Map<String, String> map = new HashMap<>();
        if (!TOKEN_MAP.containsKey(accessToken)) {
            response.sendError(403, "无效的 token");
            return map;
        }
        map.put("username", TOKEN_MAP.get(accessToken));
        return map;
    }
}
