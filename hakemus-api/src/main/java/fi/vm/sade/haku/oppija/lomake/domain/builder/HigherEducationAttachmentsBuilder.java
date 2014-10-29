package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.HigherEducationAttachments;

public class HigherEducationAttachmentsBuilder extends TitledBuilder {

    protected HigherEducationAttachmentsBuilder(String id) {
        super(id);
    }

    @Override
    Element buildImpl() {
        HigherEducationAttachments attachments = new HigherEducationAttachments(id, i18nText);
        String i18nTextKeyBase = "form.valmis.todistus.";
        String[] attachmentTypes = new String[]{"yo", "am", "amt", "kk", "ulk", "avoin", "muu"};
        for (String type : attachmentTypes) {
            attachments.addAttachmentNote(type, getI18nText(i18nTextKeyBase + type));
        }
        return attachments;
    }

    public static HigherEducationAttachmentsBuilder HigherEducationAttachments(final String id) {
        return new HigherEducationAttachmentsBuilder(id);
    }

}
