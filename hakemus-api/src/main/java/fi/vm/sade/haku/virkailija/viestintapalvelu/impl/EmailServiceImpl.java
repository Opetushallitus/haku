package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import com.google.gson.Gson;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailDataBuilder;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationByEmailDTO;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailSendId;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class EmailServiceImpl implements EmailService {
	private Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Value("${cas.service.ryhmasahkoposti}")
    private String targetService;
    @Value("${haku.app.username.to.viestintapalvelu}")
    private String clientAppUser;
    @Value("${haku.app.password.to.viestintapalvelu}")
    private String clientAppPass;
    private PDFService pdfService;
    private EmailDataBuilder emailDataBuilder;
    private CachingRestClient cachingRestClient;
	private ObjectMapper objectMapper = new ObjectMapper();
	private UrlConfiguration urlConfiguration;

	@Autowired
    public EmailServiceImpl(PDFService pdfService, EmailDataBuilder emailDataBuilder, UrlConfiguration urlConfiguration) {
    	this.pdfService = pdfService;
    	this.emailDataBuilder = emailDataBuilder;
		this.urlConfiguration = urlConfiguration;
	}

	@Override
	@Deprecated // WTF, PDFService.getPDF takes an url, not oid
	public String sendApplicationByEmail(ApplicationByEmailDTO applicationByEmail) throws IOException {
		LOGGER.info("EmailServiceImpl.sendApplicationByEmail [applicationOID: " + applicationByEmail.getApplicationOID() + "]");
		byte[] pdf = getPDF(applicationByEmail.getApplicationOID());		
		EmailData emailData = emailDataBuilder.build(applicationByEmail, pdf);
		return sendEmail(emailData);
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

	@Deprecated // WTF, getPDF takes an url, not oid
	private byte[] getPDF(String applicationOID) throws IOException {
		HttpResponse response = pdfService.getPDF(applicationOID);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		response.getEntity().writeTo(outputStream);
		byte[] pdf = outputStream.toByteArray();
		
		return pdf;
	}
	
	public String sendEmail(EmailData emailData) {
		String emailDataJson;

		try {
			emailDataJson = objectMapper.writeValueAsString(emailData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String url = urlConfiguration.url("ryhmasahkoposti-service.send");
		try {
			HttpResponse response = getCachingRestClient().post(url, MediaType.APPLICATION_JSON, emailDataJson);
			String responseJson = EntityUtils.toString(response.getEntity());
			EmailSendId emailSendId = new Gson().fromJson(responseJson, EmailSendId.class);
			return emailSendId.getId();
		} catch (IOException e) {
            throw new RemoteServiceException(url, e);
        }
	}
}
