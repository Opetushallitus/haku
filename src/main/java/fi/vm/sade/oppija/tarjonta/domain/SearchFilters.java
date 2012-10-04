package fi.vm.sade.oppija.tarjonta.domain;

import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class SearchFilters {

    public static final String KOULUTUSTYYPPI = "koulutustyyppi";
    public static final String POHJAKOULUTUS = "pohjakoulutus";
    public static final String KOULUTUSKIELI = "koulutuskieli";
    public static final String OPETUSMUOTO = "opetusmuoto";
    public static final String OPPILAITOSTYYPPI = "oppilaitostyyppi";

    private List<Filter> filters = new ArrayList<Filter>();
    private final SearchService service;

    @Autowired
    public SearchFilters(SearchService service) {
        this.service = service;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void update() {
        this.filters = updateFilters();
    }

    private List<Filter> updateFilters() {
        List<Filter> filters = new ArrayList<Filter>();

        List<FilterValue> koulutustyyppi = populateFilter(KOULUTUSTYYPPI);
        List<FilterValue> pohjakoulutus = populateFilter(POHJAKOULUTUS);
        List<FilterValue> koulutuskieli = populateFilter(KOULUTUSKIELI);
        List<FilterValue> opetusmuoto = populateFilter(OPETUSMUOTO);
        List<FilterValue> oppilaitostyyppi = populateFilter(OPPILAITOSTYYPPI);

        filters.add(new Filter(KOULUTUSTYYPPI, koulutustyyppi));
        filters.add(new Filter(POHJAKOULUTUS, pohjakoulutus));
        filters.add(new Filter(KOULUTUSKIELI, koulutuskieli));
        filters.add(new Filter(OPETUSMUOTO, opetusmuoto));
        filters.add(new Filter(OPPILAITOSTYYPPI, oppilaitostyyppi));

        return Collections.unmodifiableList(filters);
    }

    private List<FilterValue> populateFilter(final String fieldName) {
        List<FilterValue> filterValues = new ArrayList<FilterValue>();
        Collection<String> values = service.getUniqValuesByField(fieldName);
        for (String name : values) {
            filterValues.add(new FilterValue(name));
        }
        return filterValues;
    }
}
