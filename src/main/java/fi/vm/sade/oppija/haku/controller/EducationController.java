package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.domain.Opetuspiste;
import fi.vm.sade.oppija.haku.service.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller for education institute search
 *
 * @author Mikko Majapuro
 */
@Controller
@RequestMapping(value = "/education")
public class EducationController {

    public static final String TERM = "term";

    @Autowired
    EducationService educationService;

    @RequestMapping(value = "/institute/search", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", params = TERM)
    @ResponseBody
    public List<Opetuspiste> search(@RequestParam(TERM) String term) {
        List<Opetuspiste> result = educationService.searchEducationInstitutes(term);
        return result;
    }
}
