package fi.vm.sade.oppija.haku.domain;

import java.util.Date;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:26 AM}
 * @since 1.1
 */
public class ApplicationPeriod {
    private final String id;
    Date starts;
    Date end;

    Map<String, Form> forms;

    public ApplicationPeriod(String id) {
        this.id = id;
    }

    public boolean isActive() {
        assert starts != null;
        assert end != null;
        final Date now = new Date();
        return !now.before(starts) && !now.after(end);
    }

    public void addForm(Form form) {
        this.forms.put(form.getId(), form);
    }

    public Form getFormById(String id) {
        return forms.get(id);
    }

    public Category getGategory(String formId, String categoryId) {
        return getFormById(formId).getCategory(categoryId);
    }

    public String getId() {
        return id;
    }
}
