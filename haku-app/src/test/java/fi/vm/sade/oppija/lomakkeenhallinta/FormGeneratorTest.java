package fi.vm.sade.oppija.lomakkeenhallinta;

import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.FormGeneratorMock;
import fi.vm.sade.oppija.lomakkeenhallinta.koodisto.impl.KoodistoServiceMockImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class FormGeneratorTest {

    private FormGenerator formGenerator;

    @Before
    public void setUp() throws Exception {
        this.formGenerator = new FormGeneratorMock(new KoodistoServiceMockImpl(), "asId");
    }

    @Test
    public void testGenerate() throws Exception {
        assertFalse(this.formGenerator.generate().isEmpty());
    }
}
