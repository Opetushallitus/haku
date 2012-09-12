package fi.vm.sade.oppija.haku.domain.builders;

import fi.vm.sade.oppija.haku.domain.elements.Element;

/**
 * @author jukka
 * @version 9/7/121:10 PM}
 * @since 1.1
 */
public class ElementBuilder {
    final protected Element element;

    public ElementBuilder(Element element) {
        this.element = element;
    }

    public ElementBuilder withChild(Element... categories) {
        for (Element category : categories) {
            element.addChild(category);
        }
        return this;
    }

    public Element build() {
        return element;
    }
}
