package fi.vm.sade.oppija.tarjonta.domain;

/**
 * Created by IntelliJ IDEA.
 * User: ville
 * Date: 9/28/12
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdSearchParameters extends SearchParameters {

    public IdSearchParameters(final String term) {
        super("id", term, null, null, null, null, "*");
    }

    private IdSearchParameters(String searchField, String term, String sortOrder, String sortField, Integer start, Integer rows, String... fields) {
        super(searchField, term, sortOrder, sortField, start, rows, fields);
    }
}
