package fi.vm.sade.oppija.haku.domain.questions;

import fi.vm.sade.oppija.haku.domain.Question;

import java.util.ArrayList;
import java.util.List;

public abstract class OptionQuestion extends Question {

    private final List<Option> options = new ArrayList<Option>();

    protected OptionQuestion(final String id, final String title, final String name) {
        super(id, title, name);
    }

    public void addOption(final String value, final String title) {
        this.options.add(new Option(value, title));
    }

    public List<Option> getOptions() {
        return options;
    }

    public class Option {
        private final String value;
        private final String title;

        public Option(String value, String title) {
            this.value = value;
            this.title = title;
        }

        public String getValue() {
            return value;
        }

        public String getTitle() {
            return title;
        }
    }
}
