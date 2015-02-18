package fi.vm.sade.haku.oppija.lomake.validation;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;

public class ApplicationOptionInfo implements Comparable<ApplicationOptionInfo> {
    public final String aoInputId;
    public final ApplicationOption ao;

    public ApplicationOptionInfo(String aoInputId, ApplicationOption ao) {
        this.aoInputId = aoInputId;
        this.ao = ao;
    }

    @Override
    public int compareTo(ApplicationOptionInfo o) {
        return aoInputId.compareTo(o.aoInputId);
    }

    @Override
    public int hashCode() {
        return aoInputId.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ApplicationOptionInfo)) {
            return false;
        }
        return aoInputId.equals(((ApplicationOptionInfo)other).aoInputId);
    }
}
