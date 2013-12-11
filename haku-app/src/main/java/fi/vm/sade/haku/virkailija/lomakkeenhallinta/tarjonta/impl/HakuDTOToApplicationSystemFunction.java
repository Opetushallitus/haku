/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.impl;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakuaikaRDTO;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class HakuDTOToApplicationSystemFunction implements Function<HakuDTO, ApplicationSystem> {

    @Override
    public ApplicationSystem apply(HakuDTO hakuDTO) {
        Map<String, String> names = hakuDTO.getNimi();
        Map<String, String> namesTransformed = Maps.newHashMap();
        Iterator<Map.Entry<String, String>> i = names.entrySet().iterator();

        while (i.hasNext()) {
            Map.Entry<String, String> entry = i.next();
            String key = entry.getKey().split("_")[1];
            String value = Strings.isNullOrEmpty(entry.getValue()) ? "[" + key + "]" : entry.getValue();
            namesTransformed.put(key, value);
        }

        I18nText name = new I18nText(namesTransformed);

        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList();
        List<HakuaikaRDTO> hakuaikas = hakuDTO.getHakuaikas();
        if (hakuaikas != null) {
            for (HakuaikaRDTO hakuaika : hakuaikas) {
                applicationPeriods.add(new ApplicationPeriod(new Date(), hakuaika.getLoppuPvm()));
            }
        }

        ApplicationSystem applicationSystem = new ApplicationSystemBuilder()
                .addId(hakuDTO.getOid())
                .addName(name)
                .addApplicationPeriods(applicationPeriods)
                .addApplicationSystemType(hakuDTO.getHakutyyppiUri().split("#")[0])
                .addHakukausiUri(hakuDTO.getHakukausiUri().split("#")[0])
                .addHakukausiVuosi(hakuDTO.getHakukausiVuosi())
                .get();
        return applicationSystem;
    }
}
