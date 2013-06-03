package fi.vm.sade.oppija.lomakkeenhallinta.resources;

import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormBuilderResourceTest {

    private FormModelHolder formModelHolder;
    private FormBuilderResource formBuilderResource;

    @Before
    public void setUp() throws Exception {
        formModelHolder = mock(FormModelHolder.class);
        formBuilderResource = new FormBuilderResource();
        formBuilderResource.setFormModelHolder(formModelHolder);
    }

    @Test
    public void testGenerateSuccess() throws Exception {
        when(formModelHolder.generateAndReplace()).thenReturn(true);
        assertTrue(formBuilderResource.generate().getStatus() == Response.Status.SEE_OTHER.getStatusCode());
    }

    @Test
    public void testGenerateFail() throws Exception {
        when(formModelHolder.generateAndReplace()).thenReturn(false);
        assertTrue(formBuilderResource.generate().getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
