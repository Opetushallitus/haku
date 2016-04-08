package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.virkailija.viestintapalvelu.ApplicationPrintViewService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.util.UtfUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class ApplicationPrintViewServiceImpl implements ApplicationPrintViewService {
    @Value("${cas.service.haku}")
    private String targetService;
    @Value("${haku.app.username.to.haku}")
    private String clientAppUser;
    @Value("${haku.app.password.to.haku}")
    private String clientAppPass;
    private CachingRestClient cachingRestClient;
    private UrlConfiguration urlConfiguration;

    public ApplicationPrintViewServiceImpl(UrlConfiguration urlConfiguration) {
        this.urlConfiguration = urlConfiguration;
    }

    @Override
	public String getApplicationPrintView(String urlToApplicationPrint) {
		String applicationViewJSON = "";
		
		try {
			CachingRestClient cachingRestClient = getCachingRestClient();
			applicationViewJSON = cachingRestClient.getAsString(urlToApplicationPrint);			
			applicationViewJSON = UtfUtil.toUTF8(applicationViewJSON);
        } catch (IOException e) {
            throw new RemoteServiceException(targetService + urlToApplicationPrint, e);
		}
		
		return applicationViewJSON;
	}

    private synchronized CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            cachingRestClient = new CachingRestClient().setClientSubSystemCode("haku.hakemus-api");
            cachingRestClient.setWebCasUrl(urlConfiguration.url("cas.url"));
            cachingRestClient.setCasService(targetService);
            cachingRestClient.setUsername(clientAppUser);
            cachingRestClient.setPassword(clientAppPass);
        }
        return cachingRestClient;
    }
}
