package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;

import java.io.Serializable;
import java.util.*;

/**
 * @author jukka
 * @version 9/7/1210:26 AM}
 * @since 1.1
 */
public class ApplicationPeriod implements Serializable {

    private static final long serialVersionUID = 709005625385191180L;

    private String id;
    private Date starts = new Date();
    private Date end;

    final Map<String, Form> forms = new HashMap<String, Form>();

    public ApplicationPeriod() {
    }

    public ApplicationPeriod(String id) {
        this.id = id;
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, 1);
        end = new Date(instance.getTimeInMillis());
    }

    public ApplicationPeriod(final String id, final Date starts, final Date end) {
        this.id = id;
        this.starts = new Date(starts.getTime());
        this.end = new Date(end.getTime());
    }

    @JsonIgnore
    public boolean isActive() {
        assert starts != null;
        assert end != null;
        final Date now = new Date();
        return !now.before(starts) && !now.after(end);
    }

    @JsonIgnore
    public Set<String> getFormIds() {
        return this.forms.keySet();
    }

    public void addForm(final Form form) {
        this.forms.put(form.getId(), form);
    }

    public Form getFormById(final String id) {
        return forms.get(id);
    }

    public Category getGategory(final String formId, final String categoryId) {
        return getFormById(formId).getCategory(categoryId);
    }

    public String getId() {
        return id;
    }

    public Map<String, Form> getForms() {
        return forms;
    }

    public Date getStarts() {
        return new Date(starts.getTime());
    }

    public void setStarts(final Date starts) {
        this.starts = new Date(starts.getTime());
    }

    public Date getEnd() {
        return new Date(end.getTime());
    }

    public void setEnd(final Date end) {
        this.end = new Date(end.getTime());
    }
}
