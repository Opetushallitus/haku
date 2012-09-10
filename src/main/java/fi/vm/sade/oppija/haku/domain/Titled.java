package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 9/7/1210:36 AM}
 * @since 1.1
 */
public abstract class Titled extends Element {

    final String title;

    public Titled(final String id, final String title) {
        super(id);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
