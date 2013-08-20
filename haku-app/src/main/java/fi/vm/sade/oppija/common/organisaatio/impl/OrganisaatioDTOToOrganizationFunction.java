package fi.vm.sade.oppija.common.organisaatio.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrganisaatioDTOToOrganizationFunction implements
        Function<OrganisaatioDTO, Organization> {

    @Override
    public Organization apply(OrganisaatioDTO dto) {
        final HashMap<String, String> name = new HashMap<String, String>();

        MonikielinenTekstiTyyppi nimi = dto.getNimi();
        List<MonikielinenTekstiTyyppi.Teksti> nimiTeksti = nimi.getTeksti();

        for (MonikielinenTekstiTyyppi.Teksti teksti : nimiTeksti) {
            String kielikoodi = teksti.getKieliKoodi();
            if ("FI".equalsIgnoreCase(kielikoodi)) {
                name.put("fi", teksti.getValue());
            } else if ("SV".equalsIgnoreCase(kielikoodi)) {
                name.put("sv", teksti.getValue());
            } else if ("EN".equalsIgnoreCase(kielikoodi)) {
                name.put("en", teksti.getValue());
            }
        }

        final String oid = dto.getOid();
        final String parentOid = dto.getParentOid();

        final List<String> types = Lists.transform(dto.getTyypit(),
                new Function<OrganisaatioTyyppi, String>() {
                    public String apply(OrganisaatioTyyppi src) {
                        return src.toString();
                    }
                });

        final Date startDate = dto.getAlkuPvm();
        final Date endDate = dto.getLakkautusPvm();
        return new Organization(new I18nText(name), oid, parentOid,types, startDate, endDate);
    }
}
