package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 9/7/1210:37 AM}
 * @since 1.1
 */
public abstract class Question extends Titled {
    public Question(final String id) {
        super(id);
    }

    protected Question(String id, String title) {
        super(id, title);
    }
}
