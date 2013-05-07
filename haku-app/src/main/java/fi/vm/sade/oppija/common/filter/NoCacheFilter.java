package fi.vm.sade.oppija.common.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class NoCacheFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        response.getHttpHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
        response.getHttpHeaders().add("Pragma", "no-cache");
        return response;
    }

}
