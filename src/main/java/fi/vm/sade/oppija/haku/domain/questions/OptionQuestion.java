package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public abstract class OptionQuestion extends Question {

    private final List<Option> options = new ArrayList<Option>();

    protected OptionQuestion(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title, @JsonProperty(value = "name") final String name) {
        super(id, title, name);
    }

    public void addOption(final String value, final String title) {
        this.options.add(new Option(System.currentTimeMillis() + "", value, title));
    }

    public List<Option> getOptions() {
        return options;
    }
}


