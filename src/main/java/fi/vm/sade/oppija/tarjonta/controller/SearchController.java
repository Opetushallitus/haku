package fi.vm.sade.oppija.tarjonta.controller;

import fi.vm.sade.oppija.tarjonta.converter.ArrayParametersToMap;
import fi.vm.sade.oppija.tarjonta.domain.*;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@Controller
public class SearchController {


    public static final String VIEW_NAME_ITEMS = "tarjonta/tarjontatiedot";
    public static final String VIEW_NAME_ITEM = "tarjonta/tarjontatieto";
    public static final String MODEL_NAME = "searchResult";
    public static final String MODEL_NAME_SEARCH_PARAMETERS = "searchParameters";
    public static final String PARAMETERS = "parameters";
    public static final String SEARCH_FILTERS = "filters";
    public static final String SORT_PARAMETERS = "sort_parameters";
    public static final String PAGING_PARAMETERS = "paging_parameters";
    public static final String FILTERS = "filters";
    public static final String TUNNISTE = "tunniste";
    public static final String KOULUTUSTYYPPI = "koulutustyyppi";
    public static final String KOULUTUSKIELI = "koulutuskieli";
    public static final String OPETUSMUOTO = "opetusmuoto";
    public static final String OPPILAITOSTYYPPI = "oppilaitostyyppi";
    public static final String SEARCH_PARAMETER = "text";
    private final SearchService service;
    private final SearchFilters searchFilters;
    private ArrayParametersToMap arrayParametersToMap;

    @Autowired
    public SearchController(final SearchService searchService, final SearchFilters searchFilters) {
        this.service = searchService;
        this.searchFilters = searchFilters;
        arrayParametersToMap = new ArrayParametersToMap();
    }

    @ModelAttribute(SORT_PARAMETERS)
    public SortParameters getSortParameters(@RequestParam(value = "sortOrder", required = false) String sortOrder,
                                            @RequestParam(value = "sortField", required = false) String sortField) {
        return new SortParameters(sortOrder, sortField);
    }

    @ModelAttribute(PAGING_PARAMETERS)
    public PagingParameters getPagingParameters(@RequestParam(value = "start", required = false) Integer start,
                                                @RequestParam(value = "count", required = false) Integer count) {
        return new PagingParameters(start, count);
    }

    @ModelAttribute(FILTERS)
    public Map<String, Map<String, String>> getFilters(@RequestParam(value = KOULUTUSTYYPPI, required = false) String[] koulutustyyppi,
                                                       @RequestParam(value = KOULUTUSKIELI, required = false) String[] koulutuskieli,
                                                       @RequestParam(value = OPETUSMUOTO, required = false) String[] opetusmuoto,
                                                       @RequestParam(value = OPPILAITOSTYYPPI, required = false) String[] oppilaitostyyppi) {
        Map<String, Map<String, String>> filters = new HashMap<String, Map<String, String>>();
        filters.put(KOULUTUSTYYPPI, new ArrayParametersToMap().convert(koulutustyyppi));
        filters.put(KOULUTUSKIELI, new ArrayParametersToMap().convert(koulutuskieli));
        filters.put(OPETUSMUOTO, arrayParametersToMap.convert(opetusmuoto));
        filters.put(OPPILAITOSTYYPPI, new ArrayParametersToMap().convert(oppilaitostyyppi));
        return filters;
    }

    @ModelAttribute(MODEL_NAME_SEARCH_PARAMETERS)
    public SearchParameters getSearchParameters(@RequestParam(value = SEARCH_PARAMETER, required = false) String text,
                                                @ModelAttribute(SORT_PARAMETERS) SortParameters sortParameters,
                                                @ModelAttribute(PAGING_PARAMETERS) PagingParameters pagingParameters,
                                                @ModelAttribute(FILTERS) Map<String, Map<String, String>> filters) {
        HashSet<String> fields = new HashSet<String>();
        fields.add("id");
        fields.add("name");
        filters.put(SEARCH_PARAMETER, new ArrayParametersToMap().convert(new String[]{text}));
        return new SearchParameters(text, fields, sortParameters, pagingParameters, filters);
    }

    @RequestMapping(value = "/tarjontatiedot", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView listTarjontatiedot(@ModelAttribute(MODEL_NAME_SEARCH_PARAMETERS) SearchParameters searchParameters, @RequestParam(value = "update", defaultValue = "false") boolean update) {
        if (update) {
            searchFilters.update();
        }
        SearchResult searchResult = service.search(searchParameters);
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME_ITEMS);
        modelAndView.addObject(MODEL_NAME, searchResult);
        modelAndView.addObject(PARAMETERS, searchParameters);
        modelAndView.addObject(SEARCH_FILTERS, searchFilters.getFilters());
        return modelAndView;
    }


    @RequestMapping(value = "/tarjontatiedot/{tarjontatietoId}", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView getTarjontatiedot(@PathVariable final String tarjontatietoId) {
        Map<String, Map<String, String>> filters = new HashMap<String, Map<String, String>>();
        filters.put(TUNNISTE, arrayParametersToMap.convert(new String[]{tarjontatietoId}));
        SearchParameters searchParameters = new SearchParameters(filters);
        Map<String, Object> searchResult = service.searchById(searchParameters);
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME_ITEM);
        System.out.println(searchResult);
        modelAndView.addObject(MODEL_NAME, searchResult);
        return modelAndView;
    }

}
