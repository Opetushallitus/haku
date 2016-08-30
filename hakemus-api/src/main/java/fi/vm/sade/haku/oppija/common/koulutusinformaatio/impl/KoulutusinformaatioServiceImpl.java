package fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl;

import com.google.common.base.Strings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import fi.vm.sade.haku.oppija.common.jackson.UnknownPropertiesAllowingJacksonJsonClientFactory;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.properties.OphProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class KoulutusinformaatioServiceImpl extends KoulutusinformaatioService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionServiceImpl.class);
	private final Client clientWithJacksonSerializer;
	private final OphProperties urlConfiguration;

	@Autowired
	public KoulutusinformaatioServiceImpl(OphProperties urlConfiguration) {
		this.urlConfiguration = urlConfiguration;
		clientWithJacksonSerializer = UnknownPropertiesAllowingJacksonJsonClientFactory.create();
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
			WebResource asWebResource = clientWithJacksonSerializer.resource(urlConfiguration.url("koulutusinformaatio-app.ao", oid));
			if (!StringUtils.isEmpty(lang)) {
				asWebResource.queryParam("uiLang", lang);
				asWebResource.queryParam("lang", lang);
			}
			LOGGER.debug(asWebResource.getUriBuilder().build().toString());
			return asWebResource.accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").get(new GenericType<ApplicationOptionDTO>() {});
		}
	}
}
