package fi.vm.sade.oppija.lomakkeenhallinta;

import fi.vm.sade.oppija.lomakkeenhallinta.service.tarjonta.TarjontaService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormGeneratorTest {

    private FormGenerator formGenerator;
    private final List<String> oids = new ArrayList<String>();

    @Before
    public void setUp() throws Exception {
        TarjontaService tarjontaService = mock(TarjontaService.class);
        when(tarjontaService.getApplicationSystemOids()).thenReturn(oids);
        this.formGenerator = new FormGenerator(tarjontaService, null, null);
    }

    @Test
    public void testGenerate() throws Exception {
        assertTrue(this.formGenerator.generate().isEmpty());
    }
}
