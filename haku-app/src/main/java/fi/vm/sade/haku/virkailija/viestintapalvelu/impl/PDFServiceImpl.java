package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.virkailija.viestintapalvelu.ApplicationPrintViewService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.DocumentSource;
import fi.vm.sade.haku.virkailija.viestintapalvelu.DocumentSourceJsonAdapter;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
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
@Profile(value = {"default", "devluokka"})
public class PDFServiceImpl implements PDFService {
    @Value("${web.url.cas}")
    private String casUrl;
    @Value("${cas.service.viestintapalvelu}")
    private String targetService;
    @Value("${haku.app.username.to.viestintapalvelu}")
    private String clientAppUser;
    @Value("${haku.app.password.to.viestintapalvelu}")
    private String clientAppPass;
    private ApplicationPrintViewService applicationPrintViewService;
    private CachingRestClient cachingRestClient;

    @Autowired
    public PDFServiceImpl(ApplicationPrintViewService applicationPrintViewService) {
    	this.applicationPrintViewService = applicationPrintViewService;
    }
    
	@Override
	public HttpResponse getUriToPDF(String applicationOID) {
		String applicationPrintView = applicationPrintViewService.getApplicationPrintView(applicationOID);
		String documentSourceJson = getDocumentsourceJson(applicationPrintView);		
		
		String url = "/api/v1/printer/pdf";
		CachingRestClient cachingRestClient = getCachingRestClient();
		
		try {
			return cachingRestClient.post(url, MediaType.APPLICATION_JSON, documentSourceJson);
		} catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }
	}

    @Override
	public HttpResponse getPDF(String applicationOID) {
		String applicationPrintView = applicationPrintViewService.getApplicationPrintView(applicationOID);
		String documentSourceJson = getDocumentsourceJson(applicationPrintView);		
		
		String url = "/api/v1/printer/pdf/content";
		CachingRestClient cachingRestClient = getCachingRestClient();
		
		try {
			return cachingRestClient.post(url, MediaType.APPLICATION_JSON, documentSourceJson);
		} catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }	
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
    
    private String getDocumentsourceJson(String applicationPrintView) {
		List<String> sources = new ArrayList<String>();
		sources.add(applicationPrintView);
		
		DocumentSource documentSource = new DocumentSource();
		documentSource.setDocumentName("application");
		documentSource.setSources(sources);
		
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DocumentSource.class, new DocumentSourceJsonAdapter());
        Gson gson = gsonBuilder.create();
        
        return gson.toJson(documentSource, DocumentSource.class);
    }
}
