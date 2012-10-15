package fi.vm.sade.oppija.haku.service;

import java.util.Map;

public class TimeStampModifier {

    private final Map<String, String> values;

    public TimeStampModifier(Map<String, String> values) {
        this.values = values;
    }

    public void updateCreated() {
        this.values.put("created", "" + System.currentTimeMillis());
    }

    public void updateModified() {
        values.put("modified", System.currentTimeMillis() + "");
    }
}