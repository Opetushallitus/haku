package fi.vm.sade.oppija.lomake.service.impl;

import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.junit.Test;

public class FormServiceImplTest {

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFirstCategoryNotFound() throws Exception {
        FormModelHolder holder = new FormModelHolder();
        FormServiceImpl formService = new FormServiceImpl(holder);
        formService.getFirstCategory(null, null);
    }
//    @Test
//    public void testGetFirstCategory() throws Exception {
//        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(new TextQuestion("1", "title"));
//        FormModelHolder holder = new FormModelHolder();
//        holder.updateModel(formModel);
//        FormServiceImpl formService = new FormServiceImpl(holder);
//        FormModelHelper formModelHelper = new FormModelHelper(formModel);
//        Category firstCategory = formService.getFirstCategory(formModelHelper.getFirstApplicationPerioid().getId(), formModelHelper.getFirstCategoryFormId());
//        assertEquals(formModelHelper.getFirstCategory(), firstCategory);
//    }
}
