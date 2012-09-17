package fi.vm.sade.oppija.haku.domain.builders;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;

/**
 * @author jukka
 * @version 9/7/1212:47 PM}
 * @since 1.1
 */
public class FormModelBuilder {

    FormModel formModel = new FormModel();

    ApplicationPeriodBuilder applicationPeriodBuilder = new ApplicationPeriodBuilder(createId());
    private FormBuilder formBuilder = new FormBuilder(createId(), "test");

    private Category category = new Category(createId(), "category1");

    final Form form = formBuilder.withChild(category).build();

    public FormModelBuilder() {
    }

    public FormModelBuilder(Category category) {
        this.category = category;
    }

    public FormModel build() {
        return formModel;
    }

    private String createId() {
        return "test";
    }

    public FormModelBuilder withApplicationPeriods(ApplicationPeriod... periods) {
        for (ApplicationPeriod applicationPeriod : periods) {
            this.formModel.addApplicationPeriod(applicationPeriod);
        }
        return this;
    }

    public FormModelBuilder withDefaults() {
        this.formModel.addApplicationPeriod(applicationPeriodBuilder.withForm(form).build());
        return this;
    }

    public FormModelBuilder addChildToForm(Element... element) {
        for (Element element1 : element) {
            this.form.addChild(element1);
        }
        return this;

    }

    public FormModelBuilder addChildToCategory(Element... element) {
        for (Element element1 : element) {
            this.category.addChild(element1);
        }
        return this;
    }
}
