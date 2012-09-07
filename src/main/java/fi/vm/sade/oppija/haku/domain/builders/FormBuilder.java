package fi.vm.sade.oppija.haku.domain.builders;

import fi.vm.sade.oppija.haku.domain.Element;
import fi.vm.sade.oppija.haku.domain.Form;


/**
 * @author jukka
 * @version 9/7/1212:54 PM}
 * @since 1.1
 */
public class FormBuilder extends ElementBuilder {

    public FormBuilder(String id) {
        super(new Form(id));
    }

    @Override
    public FormBuilder withChild(Element... categories) {
        super.withChild(categories);
        return this;
    }

    public Form build() {
        final Form form = (Form) element;
        form.produceCategoryMap();
        return form;
    }
}
