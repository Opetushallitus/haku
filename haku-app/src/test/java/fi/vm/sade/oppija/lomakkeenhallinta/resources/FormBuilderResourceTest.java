package fi.vm.sade.oppija.lomakkeenhallinta.resources;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.oppija.lomakkeenhallinta.FormGenerator;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormBuilderResourceTest {

    private FormBuilderResource formBuilderResource;
    private FormGenerator formGenerator;
    private ApplicationSystemService applicationSystemService;

    @Before
    public void setUp() throws Exception {
        formGenerator = mock(FormGenerator.class);
        applicationSystemService = mock(ApplicationSystemService.class);
        I18nText test = ElementUtil.createI18NAsIs("test");
        ApplicationSystem applicationSystem = new ApplicationSystemBuilder().addId("1")
                .addForm(new Form("", test)).addName(test).addApplicationSystemType(OppijaConstants.VARSINAINEN_HAKU).get();
        when(formGenerator.generate()).thenReturn(ImmutableList.of(applicationSystem));
        formBuilderResource = new FormBuilderResource(formGenerator, applicationSystemService);
    }

    @Test
    public void testGenerateSuccess() throws Exception {
        assertTrue(formBuilderResource.generate().getStatus() == Response.Status.SEE_OTHER.getStatusCode());
    }

}
