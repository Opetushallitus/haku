package fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl;

import com.google.common.base.Strings;
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
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
            if (StringUtils.isEmpty(lang)) {
                lang = "fi";
			}
            WebTarget webTarget = clientWithJacksonSerializer.target(urlConfiguration.url("koulutusinformaatio-app.ao", oid)).queryParam("lang", lang).queryParam("uiLang", lang);
			LOGGER.debug(webTarget.getUriBuilder().build().toString());
			return webTarget.request().accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").get(new GenericType<ApplicationOptionDTO>() {});
		}
	}
}
