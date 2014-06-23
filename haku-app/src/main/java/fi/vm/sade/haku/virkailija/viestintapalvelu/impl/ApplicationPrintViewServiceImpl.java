package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.virkailija.viestintapalvelu.ApplicationPrintViewService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.UtfUtil;

@Service
public class ApplicationPrintViewServiceImpl implements ApplicationPrintViewService {
    @Value("${web.url.cas}")
    private String casUrl;
    @Value("${cas.service.haku}")
    private String targetService;
    @Value("${haku.app.username.to.viestintapalvelu}")
    private String clientAppUser;
    @Value("${haku.app.password.to.viestintapalvelu}")
    private String clientAppPass;
    private CachingRestClient cachingRestClient;

	@Override
	public String getApplicationPrintView(String applicationOID) {
		String applicationViewJSON = "";
		String url = "/virkailija/hakemus/" + applicationOID + "/print/view";
		
		try {
			CachingRestClient cachingRestClient = getCachingRestClient();
			applicationViewJSON = cachingRestClient.getAsString(url);			
			applicationViewJSON = UtfUtil.toUTF8(applicationViewJSON);
        } catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
		}
		
		return applicationViewJSON;
	}

    private synchronized CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            cachingRestClient = new CachingRestClient();
            cachingRestClient.setWebCasUrl(casUrl);
            cachingRestClient.setCasService(targetService);
            cachingRestClient.setUsername(clientAppUser);
            cachingRestClient.setPassword(clientAppPass);
        }
        return cachingRestClient;
    }
}
