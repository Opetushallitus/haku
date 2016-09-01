package fi.vm.sade.haku.oppija.common.organisaatio.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrganisaatioPerustietoToOrganizationFunction implements
        Function<OrganisaatioPerustieto, Organization> {

    @Override
    public Organization apply(OrganisaatioPerustieto input) {
        final String oid = input.getOid();
        final String parentOid = input.getParentOid();

        final List<String> types = Lists.transform(input.getOrganisaatiotyypit(),
                new Function<OrganisaatioTyyppi, String>() {
                    public String apply(OrganisaatioTyyppi src) {
                        return src.toString();
                    }
                });

        final Date startDate = input.getAlkuPvm();
        final Date endDate = input.getLakkautusPvm();

        final String oppilaitostyyppi = input.getOppilaitostyyppi();

        I18nText nimiTranslations = TranslationsUtil.createTranslationsMap(input.getNimi());
        return new Organization(nimiTranslations, oid, parentOid, types, oppilaitostyyppi,
                startDate, endDate);
    }
}
