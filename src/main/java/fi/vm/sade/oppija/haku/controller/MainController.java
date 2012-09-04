package fi.vm.sade.oppija.haku.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * @author jukka
 * @version 8/31/1211:47 AM}
 * @since 1.1
 */
@RequestMapping("/")
@Controller
public class MainController {
    @PostConstruct
    public void init() {
        System.out.println("init");
    }

    @RequestMapping("/foo")
    public ModelAndView helloWorld() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("helloWorld1");
        mav.addObject("message", new HashMap<String, Object>());
        return mav;
    }
}
