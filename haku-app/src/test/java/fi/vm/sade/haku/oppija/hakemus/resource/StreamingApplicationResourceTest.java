package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.CloseableIterator;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamingApplicationResourceTest {

    @Test
    public void testStreamingResourceStreams() throws IOException {
        final int expectedAmountOfData = 15;
        ApplicationService service = Mockito.mock(ApplicationService.class);
        List<Map<String, Object>> data = randomData(expectedAmountOfData);
        Mockito.when(service.findFullApplicationsStreaming(Mockito.any())).thenReturn(newCloseableIterator(data.iterator()));
        List<Map<String, Object>> response = streamingResponseAsJson(service);
        Assert.assertEquals(expectedAmountOfData, response.size());
    }
    @Test
    public void testStreamingResourceErrorResponse() throws IOException {
        final int expectedAmountOfData = 10;
        final String expectedError = "Mongo disconnected!";
        ApplicationService service = Mockito.mock(ApplicationService.class);
        Stream<Map<String, Object>> breakingStream = IntStream.range(1, expectedAmountOfData * 2).mapToObj((index) -> {
                if(index < 10) {
                return ImmutableMap.of("index", (Object) index);
        } else {
            throw new RuntimeException(expectedError);
                }});


        Mockito.when(service.findFullApplicationsStreaming(Mockito.any())).thenReturn(newCloseableIterator(breakingStream.iterator()));
        List<Map<String, Object>> response = streamingResponseAsJson(service);
        Assert.assertEquals(expectedAmountOfData, response.size());
        Assert.assertEquals(expectedError, Iterables.getLast(response).get("error"));
    }

    private List<Map<String,Object>> streamingResponseAsJson(ApplicationService service) throws IOException {
        return new Gson().fromJson(toString(new StreamingApplicationResource(service).findFullApplicationsPostStreaming(prepareEmptySearch())), Lists.newArrayList().getClass());
    }

    private String toString(Response response) throws IOException {
        StreamingOutput entity = (StreamingOutput)response.getEntity();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        entity.write(output);
        return new String(output.toByteArray());
    }

    private List<Map<String,Object>> randomData(int size) {
        return IntStream.range(0, size).mapToObj((index) -> ImmutableMap.of("index", (Object)index)).collect(Collectors.toList());
    }
    private <E> CloseableIterator<E> newCloseableIterator(Iterator<E> iterator) {

        return new CloseableIterator<E>() {
            @Override
            public void close() throws Exception {

            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next();
            }
        };
    }
    private ApplicationSearchDTO prepareEmptySearch() {
        return new ApplicationSearchDTO("",Collections.emptyList(), Collections.emptyList(),Collections.emptyList(),Collections.emptyList());
    }
}
