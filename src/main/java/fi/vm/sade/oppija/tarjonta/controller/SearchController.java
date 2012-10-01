package fi.vm.sade.oppija.tarjonta.controller;

import fi.vm.sade.oppija.tarjonta.domain.IdSearchParameters;
import fi.vm.sade.oppija.tarjonta.domain.SearchParameters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;


@Controller
public class SearchController {


    public static final String VIEW_NAME_ITEMS = "tarjonta/tarjontatiedot";
    public static final String VIEW_NAME_ITEM = "tarjonta/tarjontatieto";
    public static final String MODEL_NAME = "searchResult";
    public static final String MODEL_NAME_SEARCH_PARAMETERS = "searchParameters";
    public static final String PARAMETERS = "parameters";
    private final SearchService service;

    @Autowired
    public SearchController(final SearchService searchService) {
        this.service = searchService;
    }

    @ModelAttribute(MODEL_NAME_SEARCH_PARAMETERS)
    public SearchParameters getSearchParameters(@RequestParam(value = "term", required = false) String term,
                                                @RequestParam(value = "sort", required = false, defaultValue = "asc") String sortOrder,
                                                @RequestParam(value = "sort_field", required = false) String sortField,
                                                @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                @RequestParam(value = "rows", required = false) Integer rows) {
        String[] fields = new String[2];
        fields[0] = "id";
        fields[1] = "name";
        return new SearchParameters("text", term, sortOrder, sortField, start, rows, fields);
    }


    @RequestMapping(value = "/tarjontatiedot", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView listTarjontatiedot(@ModelAttribute(MODEL_NAME_SEARCH_PARAMETERS) final SearchParameters searchParameters) {
        SearchResult searchResult = service.search(searchParameters);
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME_ITEMS);
        modelAndView.addObject(MODEL_NAME, searchResult);
        modelAndView.addObject(PARAMETERS, searchParameters);
        return modelAndView;
    }

    @RequestMapping(value = "/tarjontatiedot/{tarjontatietoId}", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView getTarjontatiedot(@PathVariable final String tarjontatietoId) {
        Map<String, Object> searchResult = service.searchById(new IdSearchParameters(tarjontatietoId));
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME_ITEM);
        System.out.println(searchResult);
        modelAndView.addObject(MODEL_NAME, searchResult);
        return modelAndView;
    }

}
