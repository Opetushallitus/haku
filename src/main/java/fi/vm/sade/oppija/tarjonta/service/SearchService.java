package fi.vm.sade.oppija.tarjonta.service;

import fi.vm.sade.oppija.tarjonta.domain.SearchParameters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.domain.exception.SearchException;

import java.util.Collection;
import java.util.Map;

public interface SearchService {

    SearchResult search(final SearchParameters searchParameters) throws SearchException;

    Map<String, Object> searchById(final SearchParameters searchParameters);
    Collection<String> getUniqValuesByField(final String field);
}
