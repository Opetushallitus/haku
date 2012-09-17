package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.converter.FormModelToJsonString;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Attachment;
import fi.vm.sade.oppija.haku.service.AdminService;
import fi.vm.sade.oppija.haku.service.FormModelHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author jukka
 * @version 9/12/1210:14 AM}
 * @since 1.1
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    FormModelHolder formModelHolder;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getUploadForm() {
        return toAdminForm();
    }

    @RequestMapping(value = "/model", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public FormModel asJson() {
        return formModelHolder.getModel();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView editModel() {
        final FormModel model = formModelHolder.getModel();
        final ModelAndView modelAndView = new ModelAndView("admin/editModel");
        final String convert = new FormModelToJsonString().convert(model);
        modelAndView.addObject("model", convert);
        return modelAndView;
    }

    @RequestMapping(value = "/edit/foo", method = RequestMethod.POST, consumes = "multipart/form-data; charset=UTF-8")
    public String doActualEdit(HttpServletRequest request, @RequestParam("model") String json) {
        adminService.replaceModel(json);
        return "redirect:/";
    }


    private ModelAndView toAdminForm() {
        final ModelAndView modelAndView = new ModelAndView("admin/admin");
        modelAndView.addObject("attachment", new Attachment("file", "Lataa malli json-objektina"));
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView receiveFile(@RequestParam("file") MultipartFile file) throws IOException {

        adminService.replaceModel(file.getInputStream());
        return toAdminForm();
    }

}
