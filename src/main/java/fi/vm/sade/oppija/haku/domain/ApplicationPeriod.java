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

    public ApplicationPeriod(String id, Date starts, Date end) {
        this.id = id;
        this.starts = starts;
        this.end = end;
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

    public Map<String, Form> getForms() {
        return forms;
    }

    public Date getStarts() {
        return starts;
    }

    public void setStarts(Date starts) {
        this.starts = starts;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
