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
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.GroupConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.FormConfigurationService;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;


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
        .put("name",
          new I18nText(ImmutableMap.of("fi", "Perusopetuksen j\u00E4lkeisen valmentavan koulutuksen lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.AMK_ERKAT_JA_OPOT)
        .put("name",
          new I18nText(ImmutableMap.of("fi", "Ammatillisen erityisopettajakoulutuksen ja ammatillisen opinto-ohjaajankoulutuksen lomakepohja")))
        .build())
      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", FormConfiguration.FormTemplateType.AMK_OPET)
        .put("name",
          new I18nText(ImmutableMap.of("fi", "Ammatillisen opettajankoulutuksen lomakepohja")))
        .build())
      .build();

    //NOTE: Supported roles ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final Logger LOGGER = LoggerFactory.getLogger(FormConfigurationResource.class);

    @Autowired
    private FormConfigurationDAO formConfigurationDAO;

    @Autowired
    private FormConfigurationService formConfigurationService;

    public FormConfigurationResource() {
    }

    @Autowired
    public FormConfigurationResource(final FormConfigurationDAO formConfigurationDAO,
                                     final FormConfigurationService formConfigurationService) {
        this.formConfigurationDAO = formConfigurationDAO;
        this.formConfigurationService = formConfigurationService;
    }

    @GET
    @Path("{asId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public FormConfiguration getFormConfigurationByApplicationSystem(@PathParam("asId") String applicationSystemId) {
        LOGGER.debug("Getting Configuration by Id: {}", applicationSystemId);
        return formConfigurationService.createOrGetFormConfiguration(applicationSystemId);
    }

    @GET
    @Path("templates")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List<Map<String, Object>> getTemplates() {
        return formTemplateTypeTranslations;
    }

    @GET
    @Path("{asId}/defaultTemplate")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public FormConfiguration.FormTemplateType getDefaultConfiguration(@PathParam("asId") String applicationSystemId) {
        LOGGER.debug("Getting Configuration by Id: {}", applicationSystemId);
        return formConfigurationService.defaultFormTemplateType(applicationSystemId);
    }

    @POST
    @Path("{asId}/formTemplate")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_LOMAKEPOHJANVAIHTO')")
    public void saveFormTemplateType(@PathParam("asId") String applicationSystemId,
                                     Map<String, String> configuration) throws IOException {

        if (!applicationSystemId.equals(configuration.get("applicationSystemId"))) {
            throw new JSONException(Response.Status.BAD_REQUEST, "Data error: Mismatch on applicationSystemId from path and model", null);
        }
        String formTemplateType = configuration.get("formTemplateType").toString();
        LOGGER.debug("Got formtemplateType: {} for applicationSystemId: {} ", formTemplateType, applicationSystemId);
        formConfigurationService.saveFormTemplateType(applicationSystemId, formTemplateType);
        LOGGER.debug("Saved formtemplateType: {} for applicationSystemId: {} ", formTemplateType, applicationSystemId);
    }

    @POST
    @Path("{asId}/groupConfiguration/{groupId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void saveGroupConfiguration(@PathParam("asId") String applicationSystemId,
                                       @PathParam("groupId") String groupId,
                                       GroupConfiguration groupConfiguration) throws IOException {
        if (!groupId.equals(groupConfiguration.getGroupId())) {
            throw new JSONException(Response.Status.BAD_REQUEST, "Data error: Mismatch on groupId from path and model", null);
        }
        LOGGER.debug("Got groupconfiguration for group: {} for applicationSystemId: {}  ", groupConfiguration.getGroupId(), applicationSystemId);
        formConfigurationService.saveGroupConfiguration(applicationSystemId, groupConfiguration);
        LOGGER.debug("Saved groupconfiguration for group: {} for applicationSystemId: {} ", groupConfiguration.getGroupId(), applicationSystemId);
    }

    @POST
    @Path("{asId}/groupConfiguration/{groupId}/delete")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void removeGroupConfiguration(@PathParam("asId") String applicationSystemId,
                                         @PathParam("groupId") String groupId,
                                         GroupConfiguration groupConfiguration) throws IOException {
        if (!groupId.equals(groupConfiguration.getGroupId())) {
            throw new JSONException(Response.Status.BAD_REQUEST, "Data error: Mismatch on groupId from path and model", null);
        }
        LOGGER.debug("Removing groupconfiguration for group: {} for applicationSystemId: {}", groupConfiguration.getGroupId(), applicationSystemId);
        formConfigurationService.removeGroupConfiguration(applicationSystemId, groupConfiguration);
        LOGGER.debug("Removed groupconfiguration for group: {} for applicationSystemId: {}", groupConfiguration.getGroupId(), applicationSystemId);
    }
}
