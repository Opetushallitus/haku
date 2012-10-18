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

package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private static final long MILLISECONDS_IN_DAY = 1000 * 3600 * 24;

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
        final Date now = new Date();
        //intentionally failing fast here, if dates not valid
        return starts.before(now) && end.after(now);
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


    @JsonIgnore
    public long getDaysUntilEnd() {
        final Calendar start = initCalendar(this.starts);
        final Calendar end = initCalendar(this.end);

        return (end.getTimeInMillis() - start.getTimeInMillis()) / MILLISECONDS_IN_DAY;
    }

    private Calendar initCalendar(Date date) {
        final Calendar starts = GregorianCalendar.getInstance();
        starts.setTime(date);
        starts.set(Calendar.HOUR_OF_DAY, 0);
        starts.set(Calendar.MINUTE, 0);
        starts.set(Calendar.SECOND, 0);
        starts.set(Calendar.MILLISECOND, 0);
        return starts;
    }

    public Date getEnd() {
        return new Date(end.getTime());
    }

    public void setEnd(final Date end) {
        this.end = new Date(end.getTime());
    }
}
