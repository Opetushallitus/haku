/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.lomake.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.oppija.lomake.domain.elements.Form;

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
    private Date starts;
    private Date end;

    final Map<String, Form> forms = new HashMap<String, Form>();

    public ApplicationPeriod() {
        this.starts = new Date();
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, 1);
        end = new Date(instance.getTimeInMillis());
    }

    public ApplicationPeriod(String id) {
        this();
        this.id = id;
    }

    public ApplicationPeriod(final String id, final Date starts, final Date end) {
        this.id = id;
        this.starts = new Date(starts.getTime());
        this.end = new Date(end.getTime());
    }

    @JsonIgnore
    public boolean isActive() {
        final long now = new Date().getTime();
        return starts.getTime() <= now && end.getTime() > now;
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
