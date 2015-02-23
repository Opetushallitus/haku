package fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import fi.vm.sade.haku.oppija.common.jackson.UnknownPropertiesAllowingJacksonJsonClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionDTOToApplicationOptionFunction;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

@Service
@Profile(value = {"default", "devluokka"})
public class KoulutusinformaatioServiceImpl extends KoulutusinformaatioService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionServiceImpl.class);
	private final WebResource webResource;
	private final Client clientWithJacksonSerializer;
	private final ApplicationOptionDTOToApplicationOptionFunction converterFunction;

	@Autowired
	public KoulutusinformaatioServiceImpl(@Value("${koulutusinformaatio.ao.resource.url}") final String koulutusinformaatioAOResourceUrl) {
		clientWithJacksonSerializer = UnknownPropertiesAllowingJacksonJsonClientFactory.create();
		webResource = clientWithJacksonSerializer.resource(koulutusinformaatioAOResourceUrl);
		converterFunction = new ApplicationOptionDTOToApplicationOptionFunction();
	}

	@Override
	public ApplicationOptionDTO getApplicationOption(String oid) {
		return getApplicationOption(oid, null);
	}

	@Override
	public ApplicationOptionDTO getApplicationOption(String oid, String lang) {

		if (Strings.isNullOrEmpty(oid)) {
			return null;
		} else {
			UriBuilder builder = webResource.path(oid).getUriBuilder();
			if (!StringUtils.isEmpty(lang)) {
				builder.queryParam("uiLang", lang);
			}
			LOGGER.debug(builder.build().toString());
			WebResource asWebResource = clientWithJacksonSerializer.resource(builder.build());
			return asWebResource.accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").get(new GenericType<ApplicationOptionDTO>() {});
		}
	}
}
