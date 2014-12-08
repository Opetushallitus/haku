package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import org.apache.commons.lang3.StringUtils;


public class SyntheticApplicationValidator {

    private SyntheticApplication syntheticApplication;

    public SyntheticApplicationValidator(SyntheticApplication syntheticApplication) {
        this.syntheticApplication = syntheticApplication;
    }

    public boolean validateSyntheticApplication() {
        return StringUtils.isNotBlank(syntheticApplication.hakukohdeOid) &&
                StringUtils.isNotBlank(syntheticApplication.tarjoajaOid) &&
                StringUtils.isNotBlank(syntheticApplication.hakuOid) &&
                Iterables.all(syntheticApplication.hakemukset, new ValidateHakemus());
    }

    private class ValidateHakemus implements Predicate<SyntheticApplication.Hakemus> {
        @Override
        public boolean apply(SyntheticApplication.Hakemus hakemus) {
            return StringUtils.isNotBlank(hakemus.etunimi) &&
                    StringUtils.isNotBlank(hakemus.sukunimi) &&
                    StringUtils.isNotBlank(hakemus.hakijaOid);
        }
    }
}
