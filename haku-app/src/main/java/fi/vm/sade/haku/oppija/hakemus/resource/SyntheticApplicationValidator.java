package fi.vm.sade.haku.oppija.hakemus.resource;


import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SyntheticApplicationValidator {

    private List<SyntheticApplication> syntheticApplications;

    public SyntheticApplicationValidator(List<SyntheticApplication> syntheticApplications) {
        this.syntheticApplications = syntheticApplications;
    }

    public Boolean validateSyntheticApplications() {
        return Iterables.all(syntheticApplications, new ValidateSyntheticApplication());
    }

    private class ValidateSyntheticApplication implements Predicate<SyntheticApplication> {
        @Override
        public boolean apply(SyntheticApplication syntheticApplication) {
            return StringUtils.isNotEmpty(syntheticApplication.getEtunimi()) &&
                    StringUtils.isNotEmpty(syntheticApplication.getSukunimi()) &&
                    StringUtils.isNotEmpty(syntheticApplication.getHakukohdeOid()) &&
                    StringUtils.isNotEmpty(syntheticApplication.getHakijaOid()) &&
                    StringUtils.isNotEmpty(syntheticApplication.getHakuOid());
        }
    }
}
