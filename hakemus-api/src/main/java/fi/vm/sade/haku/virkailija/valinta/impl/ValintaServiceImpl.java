package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.api.client.util.Maps;
import com.google.gson.*;
import fi.vm.sade.authentication.cas.CasClient;
import fi.vm.sade.generic.PERA;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.valinta.MapJsonAdapter;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.ValintaServiceCallFailedException;
import fi.vm.sade.haku.virkailija.valinta.dto.HakemusDTO;
import fi.vm.sade.haku.virkailija.valinta.dto.HakijaDTO;
import fi.vm.sade.properties.OphProperties;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class ValintaServiceImpl implements ValintaService {

    private static final Logger log = LoggerFactory.getLogger(ValintaServiceImpl.class);
    private final OphProperties urlConfiguration;

    private String casUrl;

    @Value("${valinta-default.timeout.millis:4000}")
    private int defaultValintaHttpRequestTimeoutMilliseconds;

    @Value("${cas.service.valintalaskenta-service}")
    private String targetServiceValinta;
    @Value("${haku.app.username.to.valintalaskenta}")
    private String clientAppUserValinta;
    @Value("${haku.app.password.to.valintalaskenta}")
    private String clientAppPassValinta;

    @Value("${cas.service.valintarekisteri-service}")
    private String targetServiceValintarekisteri;
    @Value("${haku.app.username.to.valintarekisteri}")
    private String clientAppUserValintarekisteri;
    @Value("${haku.app.password.to.valintarekisteri}")
    private String clientAppPassValintarekisteri;

    @Value("${cas.service.valintalaskentakoostepalvelu}")
    private String targetServiceKooste;
    @Value("${haku.app.username.to.valintalaskentakoostepalvelu}")
    private String clientAppUserKooste;
    @Value("${haku.app.password.to.valintalaskentakoostepalvelu}")
    private String clientAppPassKooste;

    @Value("${cas.service.valinta-tulos-service}")
    private String targetServiceValintatulosService;
    @Value("${valinta-tulos-service.timeout.millis:15000}")
    private int valintaTulosServiceRequestTimeoutMilliseconds;

    private static CachingRestClient cachingRestClientValinta;
    private static Map<Integer, CachingRestClient> cachingRestClientKoosteWithTimeout = Maps.newHashMap();
    private static CachingRestClient cachingRestClientValintaTulosService;

    private String CAS_TICKET_FOR_VALINTAREKISTERI = "CAS_TICKET_FOR_VALINTAREKISTERI";
    private String LOGIN_HEADERS_FOR_VALINTAREKISTERI = "LOGIN_HEADERS_FOR_VALINTAREKISTERI";
    private static final HashMap<String, SoftReference<String>>valintarekisteriTicket = new HashMap<String, SoftReference<String>>();
    private static final HashMap<String, SoftReference<Header[]>>valintarekisteriHeaders = new HashMap<String, SoftReference<Header[]>>();
    private HttpClient httpClient;

    @Value("${valintarekisteri-default.timeout.millis:300000}")
    private int defaultValintarekisteriHttpRequestTimeoutMilliseconds;

    @Autowired
    public ValintaServiceImpl(OphProperties urlConfiguration) {
        this.urlConfiguration=urlConfiguration;
        casUrl = urlConfiguration.url("cas.url");
        setHttpClient(CachingRestClient.createDefaultHttpClient(defaultValintarekisteriHttpRequestTimeoutMilliseconds, 60));
    }

    public void setHttpClient(HttpClient client){
        httpClient = client;
    }

    @Override
    public HakemusDTO getHakemus(String asOid, String applicationOid) {
        String url = urlConfiguration.url("valintalaskenta-laskenta-service.hakemus", asOid, applicationOid);

        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            return gson.fromJson(getCachingRestClientValinta().getAsString(url), HakemusDTO.class);
        } catch (Exception e) {
            log.error("GET {} failed: ", url, e);
            return new HakemusDTO();
        }
    }

    @Override
    public HakijaDTO getHakija(String asOid, String applicationOid) {
        String url = urlConfiguration.url("valinta-tulos-service.hakija", asOid, applicationOid);
        CachingRestClient client = getCachingRestClientValintaTulosService();

        try {
            return client.get(url, HakijaDTO.class);
        } catch (Exception e) {
            log.error(String.format("GET %s with parameters hakuOid / hakemusOid %s / %s failed: ", url, asOid, applicationOid), e);
        }
        return new HakijaDTO();
    }

    @Override
    public HakijaDTO getHakijaFromValintarekisteri(String asOid, String applicationOid) {
        String url = urlConfiguration.url("valintarekisteri.hakija", asOid, applicationOid);
        try {
            HakijaDTO hdto = makeAuthenticatedRequestToValintarekisteri(url);
            return hdto;
        } catch (Exception e) {
            log.error(String.format("GET %s with parameters hakuOid / hakemusOid %s / %s failed: ", url, asOid, applicationOid), e);
        }
        return new HakijaDTO();
    }

    private HakijaDTO makeAuthenticatedRequestToValintarekisteri(String url){
        HttpGet req = new HttpGet(url);
        try {
            Header[] rekisteriHeaders = getCachedHeadersForValintarekisteri();
            req.setHeaders(rekisteriHeaders);
            HttpResponse httpresponse = httpClient.execute(req);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            if(statusCode == 200){
                return parseHakijaFromInputStream(httpresponse.getEntity().getContent());
            } else if (statusCode == 401) {
                authorizeValintarekisteri(true, true);
                rekisteriHeaders = getCachedHeadersForValintarekisteri();
                req.setHeaders(rekisteriHeaders);
                httpresponse = httpClient.execute(req);
                if(httpresponse.getStatusLine().getStatusCode() == 200) {
                    return parseHakijaFromInputStream(httpresponse.getEntity().getContent());
                }
            }
        } catch (IOException e){
            log.error(String.format("GET %s failed: ", url), e);
        } finally {
            req.releaseConnection();
        }
        return new HakijaDTO();
    }

    private HakijaDTO parseHakijaFromInputStream(InputStream stream) throws IOException {
        HakijaDTO hakijaDTO = new HakijaDTO();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String response = IOUtils.toString(stream);
        hakijaDTO = gson.fromJson(response, HakijaDTO.class);
        return hakijaDTO;
    }

    private synchronized boolean authorizeValintarekisteri(boolean reloadHeaders, boolean reloadTicket){
        if(reloadTicket) {
            valintarekisteriTicket.put(CAS_TICKET_FOR_VALINTAREKISTERI,null);
        }
        if(reloadHeaders) {
            setValintarekisteriHeaders(null);
        }
        String ticket = getTicketForValintarekisteri();

        HttpGet req2 = new HttpGet(targetServiceValintarekisteri + "/auth/login?ticket=" + ticket);
        req2.setHeader(CachingRestClient.CAS_SECURITY_TICKET, ticket);
        req2.setHeader(PERA.X_KUTSUKETJU_ALOITTAJA_KAYTTAJA_TUNNUS, clientAppUserValintarekisteri);
        req2.setHeader(PERA.X_PALVELUKUTSU_LAHETTAJA_KAYTTAJA_TUNNUS, clientAppUserValintarekisteri);
        try {
            HttpResponse ticketResponse = httpClient.execute(req2);
            if (ticketResponse.getStatusLine().getStatusCode() == 200) {
                setValintarekisteriHeaders(ticketResponse.getHeaders("session"));
                return true;
            } else {
                valintarekisteriTicket.put(CAS_TICKET_FOR_VALINTAREKISTERI,null);
                setValintarekisteriHeaders(null);
            }
            log.error(String.format("CAS ticket fetch failed with statuscode %s:", ticketResponse.getStatusLine().getStatusCode()));
            return false;
        } catch (IOException e) {
            log.error("CAS ticket fetch failed: ", e);
            return false;
        } finally {
            req2.releaseConnection();
        }
    }

    private synchronized Header[] getCachedHeadersForValintarekisteri(){
        SoftReference<Header[]> headers = valintarekisteriHeaders.get(LOGIN_HEADERS_FOR_VALINTAREKISTERI);
        if(headers == null) {
            authorizeValintarekisteri(true, false);
        }
        Header[] header = null == headers ? null : headers.get();
        return header;
    }

    private String getTicketForValintarekisteri() {
        if (valintarekisteriTicket.get(CAS_TICKET_FOR_VALINTAREKISTERI) == null) {
            String ticket = CasClient.getTicket(casUrl + "/v1/tickets", clientAppUserValintarekisteri, clientAppPassValintarekisteri, targetServiceValintarekisteri + "/auth/login", false);
            log.debug("ticket " + ticket);
            valintarekisteriTicket.put(CAS_TICKET_FOR_VALINTAREKISTERI, new SoftReference<String>(ticket));
            return ticket;
        } else {
            SoftReference<String> ticket = valintarekisteriTicket.get(CAS_TICKET_FOR_VALINTAREKISTERI);
            String stringTicket = null == ticket ? null : ticket.get();
            return stringTicket;
        }
    }

    public void setValintarekisteriHeaders(Header[] headers){
        valintarekisteriHeaders.put(LOGIN_HEADERS_FOR_VALINTAREKISTERI, new SoftReference<Header[]>(headers));
    }

    @Override
    public Map<String, String> fetchValintaData(Application application, Optional<Duration> valintaTimeout) throws ValintaServiceCallFailedException {
        String asId = application.getApplicationSystemId();
        String personOid = application.getPersonOid();
        String url = urlConfiguration.url("valintalaskentakoostepalvelu.valintadata", asId, personOid);
        CachingRestClient client = getCachingRestClientKooste(valintaTimeout);
        Map<String, String> valintadata = new HashMap<>();
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(HashMap.class, new MapJsonAdapter()).create();
            HttpResponse response = client.post(url, "application/json", new Gson().toJson(application));
            HttpEntity responseEntity = response.getEntity();
            InputStream stream = responseEntity.getContent();
            String json = IOUtils.toString(stream);
            IOUtils.closeQuietly(stream);
            valintadata = gson.fromJson(json, valintadata.getClass());
        } catch (Exception e) {
            log.error("POST {} failed: ", url, e);
            throw new ValintaServiceCallFailedException(e);
        }
        return valintadata;

    }

    private synchronized CachingRestClient getCachingRestClientKooste(Optional<Duration> valintaTimeout) {
        int timeoutMillis = (int) valintaTimeout.orElse(Duration.ofMillis(defaultValintaHttpRequestTimeoutMilliseconds)).toMillis();
        CachingRestClient cachingRestClientKooste = cachingRestClientKoosteWithTimeout.get(timeoutMillis);
        if (cachingRestClientKooste == null) {
            cachingRestClientKooste = new CachingRestClient(timeoutMillis).setClientSubSystemCode("haku.hakemus-api");
            cachingRestClientKooste.setWebCasUrl(casUrl);
            cachingRestClientKooste.setCasService(targetServiceKooste);
            cachingRestClientKooste.setUsername(clientAppUserKooste);
            cachingRestClientKooste.setPassword(clientAppPassKooste);
            log.info("Initialised cachingRestClientKooste"
                    + " timeout: " + timeoutMillis + " ms"
                    + " casUrl: " + casUrl
                    + " casService: " + targetServiceKooste
                    + " username: " + clientAppUserKooste
                    + " password: " + clientAppPassKooste
            );
            cachingRestClientKoosteWithTimeout.put(timeoutMillis, cachingRestClientKooste);
        }
        return cachingRestClientKooste;
    }

    private synchronized CachingRestClient getCachingRestClientValinta() {
        if (cachingRestClientValinta == null) {
            cachingRestClientValinta = new CachingRestClient(defaultValintaHttpRequestTimeoutMilliseconds).setClientSubSystemCode("haku.hakemus-api");
            cachingRestClientValinta.setWebCasUrl(casUrl);
            cachingRestClientValinta.setCasService(targetServiceValinta);
            cachingRestClientValinta.setUsername(clientAppUserValinta);
            cachingRestClientValinta.setPassword(clientAppPassValinta);
            log.info("Initialised getCachingRestClientValinta"
                    + " timeout: " + defaultValintaHttpRequestTimeoutMilliseconds + " ms"
                    + " casUrl: " + casUrl
                    + " casService: " + targetServiceValinta
                    + " username: " + clientAppUserValinta
                    + " password: " + clientAppPassValinta
            );
        }
        return cachingRestClientValinta;
    }

    /**
     * No CAS credentials, because from here we can use the private valinta-tulos-service API which does not
     * need CAS and the public CASsed API of valinta-tulos-service would need the CAS ticket to be passed in
     * differently than the others.
     */
    private synchronized CachingRestClient getCachingRestClientValintaTulosService() {
        if (cachingRestClientValintaTulosService == null) {
            cachingRestClientValintaTulosService = new CachingRestClient(valintaTulosServiceRequestTimeoutMilliseconds).
                setClientSubSystemCode("haku.hakemus-api");
            cachingRestClientValintaTulosService.setCasService(targetServiceValintatulosService);

            log.debug("getcachingRestClientValintaTulosService "
                    +" casService: "+cachingRestClientValintaTulosService.getCasService()
            );
        }
        return cachingRestClientValintaTulosService;
    }

    public void setCachingRestClientValinta(CachingRestClient cachingRestClientValinta) {
        ValintaServiceImpl.cachingRestClientValinta = cachingRestClientValinta;
    }
}
