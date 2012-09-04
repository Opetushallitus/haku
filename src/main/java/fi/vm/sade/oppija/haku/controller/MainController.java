package fi.vm.sade.oppija.haku.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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
        mav.setViewName("form");
        final HashMap<String, Object> inputs = createInput();

        final ArrayList<HashMap<String, Object>> attributeValue = new ArrayList<HashMap<String, Object>>();
        attributeValue.add(createInput());
        attributeValue.add(createCheckbox());
        attributeValue.add(createGuide());
        mav.addObject("inputs", attributeValue);


        return mav;
    }

    private HashMap<String, Object> createGuide() {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", "div1");
        map.put("type", "div");
        map.put("description", "Tämä on vaan ohje");
        return map;
    }

    private HashMap<String, Object> createCheckbox() {
        final HashMap<String, Object> inputs = new HashMap<String, Object>();
        inputs.put("id", "checkbox1");
        inputs.put("type", "checkbox");
        inputs.put("description", "Turvakysymys");
        return inputs;
    }

    private HashMap<String, Object> createInput() {
        final HashMap<String, Object> inputs = new HashMap<String, Object>();
        inputs.put("id", "jokuid");
        inputs.put("type", "text");
        inputs.put("description", "Etunimi");
        return inputs;
    }
}
