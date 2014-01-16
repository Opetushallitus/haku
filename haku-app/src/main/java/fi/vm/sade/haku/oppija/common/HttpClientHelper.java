package fi.vm.sade.haku.oppija.common;

import fi.vm.sade.authentication.cas.CasClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHelper {

    Logger log = LoggerFactory.getLogger(HttpClientHelper.class);

    private String casUrl;
    private String targetService;
    private String resource;
    private String clientAppUser;
    private String clientAppPass;

    public HttpClientHelper(String casUrl, String targetService, String resource, String clientAppUser, String clientAppPass) {
        log.debug("creating HttpClientHelper casUrl: {} resource: {} user: {} passwd: {}",
                new String[]{casUrl, resource, clientAppUser, clientAppPass});
        this.casUrl = casUrl;
        this.targetService = targetService;
        this.resource = resource;
        this.clientAppUser = clientAppUser;
        this.clientAppPass = clientAppPass;
    }

    public String getServiceticket() {
        String realCasUrl = casUrl + "/v1/tickets";
        return CasClient.getTicket(realCasUrl, clientAppUser, clientAppPass, targetService + "/j_spring_cas_security_check");
    }

    public String getRealUrl(String url) {
        return targetService + resource + url + "?ticket=" + getServiceticket();
    }
}
