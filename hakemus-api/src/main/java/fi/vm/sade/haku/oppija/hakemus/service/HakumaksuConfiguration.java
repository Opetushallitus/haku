package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil;
import fi.vm.sade.properties.OphProperties;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

@Configuration
@Profile({"default", "devluokka"})
public class HakumaksuConfiguration {

    @Bean(name = "hakumaksuUtil")
    public HakumaksuUtil hakumaksuUtil(RestClient restClient,
                                       OphProperties urlConfiguration,
                                       @Value("${haku.app.username.to.valintarekisteri}") String clientAppUser,
                                       @Value("${haku.app.password.to.valintarekisteri}") String clientAppPass) {
        PoolingClientConnectionManager connectionManager;
        connectionManager = new PoolingClientConnectionManager(SchemeRegistryFactory.createDefault(), 60, TimeUnit.MILLISECONDS);
        connectionManager.setDefaultMaxPerRoute(100); // default 2
        connectionManager.setMaxTotal(1000); // default 20
        final DefaultHttpClient actualClient = new DefaultHttpClient(connectionManager);
        HttpParams httpParams = actualClient.getParams();
        httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpConnectionParams.setConnectionTimeout(httpParams, 300000);
        HttpConnectionParams.setSoTimeout(httpParams, 300000);
        HttpConnectionParams.setSoKeepalive(httpParams, true); // prevent firewall to reset idle connections?
        return new HakumaksuUtil(restClient, urlConfiguration, actualClient, clientAppUser, clientAppPass);
    }

    @Bean(name = "hakumaksuService")
    public HakumaksuService hakumaksuService(OphProperties urlConfiguration,
                                             HakumaksuUtil hakumaksuUtil) {
        return new HakumaksuService(
                urlConfiguration,
                hakumaksuUtil
        );
    }

}
