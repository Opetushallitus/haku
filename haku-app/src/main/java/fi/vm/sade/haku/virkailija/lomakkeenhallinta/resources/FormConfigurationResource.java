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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@Path("/application-system-form-editor/configuration")
public class FormConfigurationResource {

    public static List<Map<String, Object>> formTemplateTypeTranslations = new ImmutableList.Builder<Map<String, Object>>()
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT)
        .put("name", new I18nText(ImmutableMap.of("fi", "Keskiasteen yhteishaun kev\u00E4\u00E4n lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.YHTEISHAKU_SYKSY)
        .put("name", new I18nText(ImmutableMap.of("fi", "Keskiasteen yhteishaun syksyn lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.LISAHAKU_KEVAT)
        .put("name", new I18nText(ImmutableMap.of("fi", "Keskiasteen lis\u00E4haun kev\u00E4\u00E4n lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.LISAHAKU_SYKSY)
        .put("name", new I18nText(ImmutableMap.of("fi", "Keskiasteen lis\u00E4haun syksyn lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT_KORKEAKOULU)
        .put("name", new I18nText(ImmutableMap.of("fi", "Korkeakoulujen yhteishaun kev\u00E4\u00E4n lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.YHTEISHAKU_SYKSY_KORKEAKOULU)
        .put("name", new I18nText(ImmutableMap.of("fi", "Korkeakoulujen yhteishaun syksyn lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.LISAHAKU_KEVAT_KORKEAKOULU)
        .put("name", new I18nText(ImmutableMap.of("fi", "Korkeakoulujen lis\u00E4haun kev\u00E4\u00E4n lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.LISAHAKU_SYKSY_KORKEAKOULU)
        .put("name", new I18nText(ImmutableMap.of("fi", "Korkeakoulujen lis\u00E4haun syksyn lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.PERUSOPETUKSEN_JALKEINEN_VALMENTAVA)
        .put("name", new I18nText(ImmutableMap.of("fi", "Perusopetuksen j\u00E4lkeisen valmentavan koulutuksen lomakepohja")))
        .build())
      .build();

    //NOTE: Supported roles ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final Logger LOGGER = LoggerFactory.getLogger(FormConfigurationResource.class);

    @Autowired
    private FormConfigurationDAO formConfigurationDAO;

    public FormConfigurationResource(){
    }

    @Autowired
    public FormConfigurationResource(final FormConfigurationDAO formConfigurationDAO) {
        this.formConfigurationDAO = formConfigurationDAO;
    }

    @GET
    @Path("{asId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public FormConfiguration getFormConfigurationByApplicationSystem(@PathParam("asId") String applicationSystemId) {
        LOGGER.debug("Getting Configuration by Id: {}", applicationSystemId);
        return formConfigurationDAO.findByApplicationSystem(applicationSystemId);
    }

    @POST
    @Path("{asId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void saveFormConfigurationForApplicationSystem(@PathParam("asId") String applicationSystemId,
      FormConfiguration formConfiguration) throws IOException {
        LOGGER.debug("Saved form configuration for application system: " + applicationSystemId);
        formConfigurationDAO.save(formConfiguration);
        LOGGER.debug("Saved form configuration for application system: " + applicationSystemId);
    }

    @GET
    @Path("templates")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List<Map<String,Object>> getTemplates() {
        return formTemplateTypeTranslations;
    }
}
