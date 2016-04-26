package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.virkailija.viestintapalvelu.ApplicationPrintViewService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.DocumentSourceDTO;
import fi.vm.sade.haku.virkailija.viestintapalvelu.json.DocumentSourceJsonAdapter;
import fi.vm.sade.properties.OphProperties;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class PDFServiceImpl implements PDFService {
    @Value("${cas.service.viestintapalvelu}")
    private String targetService;
    @Value("${haku.app.username.to.viestintapalvelu}")
    private String clientAppUser;
    @Value("${haku.app.password.to.viestintapalvelu}")
    private String clientAppPass;
    private ApplicationPrintViewService applicationPrintViewService;
    private CachingRestClient cachingRestClient;
    private OphProperties urlConfiguration;

    @Autowired
    public PDFServiceImpl(ApplicationPrintViewService applicationPrintViewService, OphProperties urlConfiguration) {
    	this.applicationPrintViewService = applicationPrintViewService;
        this.urlConfiguration = urlConfiguration;
    }
    
	@Override
	public HttpResponse getUriToPDF(String applicationOid) {
		String applicationPrintView = applicationPrintViewService.getApplicationPrintView(urlConfiguration.url("haku-app.hakemusPdf", applicationOid));
		String documentSourceJson = getDocumentsourceJson(applicationPrintView);

		String url = urlConfiguration.url("viestintapalvelu.uriToPDF");

        try {
			return getCachingRestClient().post(url, MediaType.APPLICATION_JSON, documentSourceJson);
		} catch (IOException e) {
            throw new RemoteServiceException(url, e);
        }
	}

    @Override
    @Deprecated // NOT IN USE?
	public HttpResponse getPDF(String urlToApplicationPrint) {
		String applicationPrintView = applicationPrintViewService.getApplicationPrintView(urlToApplicationPrint);
		String documentSourceJson = getDocumentsourceJson(applicationPrintView);

		String url = urlConfiguration.url("viestintapalvelu.pdfContent");

        try {
			return getCachingRestClient().post(url, MediaType.APPLICATION_JSON, documentSourceJson);
		} catch (IOException e) {
            throw new RemoteServiceException(url, e);
        }	
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
    
    private String getDocumentsourceJson(String applicationPrintView) {
		List<String> sources = new ArrayList<String>();
		sources.add(applicationPrintView);
		
		DocumentSourceDTO documentSource = new DocumentSourceDTO();
		documentSource.setDocumentName("application");
		documentSource.setSources(sources);
		
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DocumentSourceDTO.class, new DocumentSourceJsonAdapter());
        Gson gson = gsonBuilder.create();
        
        return gson.toJson(documentSource, DocumentSourceDTO.class);
    }
}
