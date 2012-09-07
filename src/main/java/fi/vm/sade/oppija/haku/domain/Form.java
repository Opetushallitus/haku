package fi.vm.sade.oppija.haku.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:27 AM}
 * @since 1.1
 */
public class Form extends Titled {

    final Map<String, Category> categories = new HashMap<String, Category>();

    public Form(String id) {
        super(id);
    }

    public Category getCategory(String categoryId) {
        return categories.get(categoryId);
    }

}
