package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 9/7/1210:38 AM}
 * @since 1.1
 */
public class Link extends Element {
    final String value;

    public Link(String value, String href) {
        super(System.currentTimeMillis() + "");
        this.value = value;
        attributes.add(new Attribute("href", href));
    }


}
