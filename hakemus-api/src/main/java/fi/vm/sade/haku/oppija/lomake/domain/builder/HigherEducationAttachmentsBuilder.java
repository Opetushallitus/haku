package fi.vm.sade.haku.oppija.lomake.domain.builder;

import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.HigherEducationAttachments;

import java.util.HashMap;
import java.util.Map;

public class HigherEducationAttachmentsBuilder extends TitledBuilder {

    protected HigherEducationAttachmentsBuilder(String id) {
        super(id);
    }

    @Override
    Element buildImpl() {

        String i18nTextKeyBase = "form.valmis.todistus.";
        String[] attachmentTypes = new String[]{"yo", "am", "amt", "kk", "ulk", "avoin", "muu"};
        Map<String, I18nText> attachmentNotes = Maps.newHashMapWithExpectedSize(attachmentTypes.length);
        for (String type : attachmentTypes) {
            attachmentNotes.put(type, getI18nText(i18nTextKeyBase + type));
        }
        return new HigherEducationAttachments(id, i18nText, attachmentNotes);
    }

    public static HigherEducationAttachmentsBuilder HigherEducationAttachments(final String id) {
        return new HigherEducationAttachmentsBuilder(id);
    }

}
