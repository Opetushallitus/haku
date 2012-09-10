package fi.vm.sade.oppija.haku.domain.questions;

import fi.vm.sade.oppija.haku.domain.Question;

/**
 * @author jukka
 * @version 9/7/122:03 PM}
 * @since 1.1
 */
public class TextArea extends Question {
    public TextArea(String id) {
        super(id);
    }

    public TextArea(String id, String title) {
        super(id, title);
    }
}
