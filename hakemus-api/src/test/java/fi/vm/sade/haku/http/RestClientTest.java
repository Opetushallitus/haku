package fi.vm.sade.haku.http;

import com.google.api.client.util.Key;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.HttpRestClient.Response;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertNotNull;

public class RestClientTest {
    public static class Output {
        @Key
        public String koodiUri;
    }
    @Ignore
    public void fooTest() throws ExecutionException, InterruptedException {
        Future<String> f = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "foo";
            }
        });
        System.out.println(f.get());
    }
    @Test
    public void getTest() throws ExecutionException, InterruptedException, IOException {
        String url = "https://virkailija.opintopolku.fi/koodisto-service/rest/codeelement/valtioryhmat_2/1";
        ListenableFuture<Response<Output>> response = new HttpRestClient().get(url, Output.class);
        Response<Output> output = response.get();
        assertNotNull(output);
    }
}
