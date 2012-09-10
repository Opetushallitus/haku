package fi.vm.sade.oppija.haku.domain.questions;

import fi.vm.sade.oppija.haku.domain.Question;

/**
 * @author jukka
 * @version 9/7/121:28 PM}
 * @since 1.1
 */
public class TextQuestion extends Question {
    public TextQuestion(final String id) {
        super(id);
    }

    public TextQuestion(String id, String title) {
        super(id, title);
    }
}
