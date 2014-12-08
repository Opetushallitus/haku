package fi.vm.sade.haku.oppija.hakemus.resource;


import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SyntheticApplicationValidator {

    private SyntheticApplication syntheticApplication;

    public SyntheticApplicationValidator(SyntheticApplication syntheticApplication) {
        this.syntheticApplication = syntheticApplication;
    }

    public boolean validateSyntheticApplication() {
            return StringUtils.isNotEmpty(syntheticApplication.getHakukohdeOid()) &&
                    StringUtils.isNotEmpty(syntheticApplication.getHakuOid()) &&
                    !syntheticApplication.getHakemukset().isEmpty();
    }
}
