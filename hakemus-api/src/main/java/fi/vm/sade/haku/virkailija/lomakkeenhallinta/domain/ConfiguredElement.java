package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

public interface ConfiguredElement {

    public Element generateElement(final FormParameters formParameters);
}
