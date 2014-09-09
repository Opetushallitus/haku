package fi.vm.sade.haku.virkailija.koulutusinformaatio.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.koulutusinformaatio.json.DateJsonAdapter;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

@Service
@Profile(value = {"default", "devluokka"})
public class KoulutusinformaatioServiceImpl implements KoulutusinformaatioService {
	@Value("${koulutusinformaatio.ao.resource.url}")
	private String targetService;

  @Override
	public ApplicationOptionDTO getApplicationOption(String oid) {
        return getApplicationOption(oid, null);
    }

    @Override
    public ApplicationOptionDTO getApplicationOption(String oid, String lang) {
		try {
			// Create the HTTP request for getting application option data
			DefaultHttpClient httpClient = new DefaultHttpClient();
            String uiLang = buildLangParameter(lang);
			HttpGet getRequest = new HttpGet(targetService + "/" + oid + uiLang);
			getRequest.addHeader("accept", MediaType.APPLICATION_JSON);

			// Execute the request. Check was the response successful.
			HttpResponse response = httpClient.execute(getRequest);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException(targetService + "/" + oid + uiLang);
			}

			// Create readers for handling the response
			InputStreamReader inputStreamReader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			// Create builder and register adapter for date handling
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(Date.class, new DateJsonAdapter());
			// Create GSON and get data by using reader to object
			Gson gson = builder.create();
			ApplicationOptionDTO applicationOption = gson.fromJson(bufferedReader, ApplicationOptionDTO.class);

			// Shutdown the connection and return data
			httpClient.getConnectionManager().shutdown();
			return applicationOption;
		} catch (Exception e) {
			throw new RemoteServiceException(targetService + "/" + oid, e);
		}

	}

    private String buildLangParameter(String lang) {
        if ("fi".equals(lang) || "sv".equals(lang) || "en".equals(lang)) {
            return new StringBuilder("?lang=").append(lang)
                    .append("&uiLang=").append(lang)
                    .toString();
        }
        return "";
    }

    @Override
	public List<ApplicationOptionDTO> getApplicationOptions(List<String> oids) {
		List<ApplicationOptionDTO> applicationOptions = new ArrayList<ApplicationOptionDTO>();

		for (String oid : oids) {
			applicationOptions.add(getApplicationOption(oid));
		}

		return applicationOptions;
	}

    public String getTargetService() {
      return targetService;
    }

    public void setTargetService(String targetService) {
      this.targetService = targetService;
    }

}
