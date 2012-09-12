package fi.vm.sade.oppija.haku.domain.builders;

import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;


/**
 * @author jukka
 * @version 9/7/1212:54 PM}
 * @since 1.1
 */
public class FormBuilder extends ElementBuilder {

    public FormBuilder(final String id, final String name) {
        super(new Form(id, name));
    }

    @Override
    public FormBuilder withChild(Element... categories) {
        super.withChild(categories);
        return this;
    }

    public Form build() {
        final Form form = (Form) element;
        form.init();
        return form;
    }
}
