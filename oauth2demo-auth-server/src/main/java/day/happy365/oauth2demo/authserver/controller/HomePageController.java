package day.happy365.oauth2demo.authserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth-server")
public class HomePageController {

    @GetMapping()
    public ModelAndView home() {
        System.out.println("【认证服务端】进入认证服务登陆页");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
}
