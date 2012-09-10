package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 9/7/1210:36 AM}
 * @since 1.1
 */
public abstract class Titled extends Element {

    String title = "";

    public Titled(String id) {
        super(id);
    }

    public String getTitle() {
        return title;
    }

    protected Titled(String id, String title) {
        super(id);
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
