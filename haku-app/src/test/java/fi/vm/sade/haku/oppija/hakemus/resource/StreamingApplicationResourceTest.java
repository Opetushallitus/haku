package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.CloseableIterator;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Iterables.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamingApplicationResourceTest {

    @Test
    public void testStreamingResourceStreams() throws IOException {
        final int expectedAmountOfData = 15;
        ApplicationService service = mock(ApplicationService.class);
        List<Map<String, Object>> data = randomData(expectedAmountOfData);
        when(service.findFullApplicationsStreaming(any())).thenReturn(newCloseableIterator(data.iterator()));
        List<Map<String, Object>> response = streamingResponseAsJson(service);
        assertEquals(expectedAmountOfData, response.size());
    }
    @Test
    public void testStreamingResourceErrorResponse() throws IOException {
        final int expectedAmountOfData = 10;
        final String expectedError = "Mongo disconnected!";
        ApplicationService service = mock(ApplicationService.class);
        Stream<Map<String, Object>> breakingStream = range(1, expectedAmountOfData * 2).mapToObj((index) -> {
                if(index < 10) {
                return of("index", (Object) index);
        } else {
            throw new RuntimeException(expectedError);
                }});


        when(service.findFullApplicationsStreaming(any())).thenReturn(newCloseableIterator(breakingStream.iterator()));
        List<Map<String, Object>> response = streamingResponseAsJson(service);
        assertEquals(expectedAmountOfData, response.size());
        assertEquals(expectedError, getLast(response).get("error"));
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
        return range(0, size).mapToObj((index) -> of("index", (Object)index)).collect(toList());
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
        return new ApplicationSearchDTO("",emptyList(), emptyList(),emptyList(),emptyList());
    }
}
