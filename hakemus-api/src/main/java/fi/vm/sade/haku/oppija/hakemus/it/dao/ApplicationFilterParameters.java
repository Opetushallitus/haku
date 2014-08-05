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

package fi.vm.sade.haku.oppija.hakemus.it.dao;

import java.util.List;

public class ApplicationFilterParameters {

    private int maxApplicationOptions;
    private List<String> organizationsReadable;
    private List<String> organizationsOpo;

    public ApplicationFilterParameters(int maxApplicationOptions, List<String> organizationsReadable, List<String> organizationsOpo) {
        this.maxApplicationOptions = maxApplicationOptions;
        this.organizationsReadable = organizationsReadable;
        this.organizationsOpo = organizationsOpo;
    }

    public int getMaxApplicationOptions() {
        return maxApplicationOptions;
    }

    public List<String> getOrganizationsReadble() {
        return organizationsReadable;
    }

    public List<String> getOrganizationsOpo() {
        return organizationsOpo;
    }

    @Override
    public boolean equals(Object other) {
        if (!this.getClass().isAssignableFrom(other.getClass())) {
            return false;
        }
        ApplicationFilterParameters otherParams = (ApplicationFilterParameters) other;
        if (otherParams.maxApplicationOptions != this.maxApplicationOptions) {
            return false;
        }
        // Can't rely on List.equals. ArrayList.equals checks for order, and it's not relevant here.
        return isListEqual(this.organizationsOpo, otherParams.organizationsOpo)
                && isListEqual(this.organizationsReadable, otherParams.organizationsReadable);
    }

    private boolean isListEqual(List<String> thisList, List<String> otherList) {
        if (thisList != null && otherList != null) {
            if (thisList.size() != otherList.size()) {
                return false;
            }
            for (String str : thisList) {
                if (!otherList.contains(str)) {
                    return false;
                }
            }
        }
        return true;
    }
}
