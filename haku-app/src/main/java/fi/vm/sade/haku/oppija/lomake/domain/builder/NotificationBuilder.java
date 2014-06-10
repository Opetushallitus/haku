package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Notification;
import fi.vm.sade.haku.oppija.lomake.validation.validators.AlwaysFailsValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class NotificationBuilder extends ElementBuilder {
    private Notification.NotificationType notificationType;

    protected NotificationBuilder(String id) {
        super(id);
    }

    public NotificationBuilder setI18nText(final I18nText i18nText) {
        this.i18nText = i18nText;
        return this;
    }

    public NotificationBuilder type(final Notification.NotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }
    public NotificationBuilder failValidation() {
        validator(new AlwaysFailsValidator(ElementUtil.createI18NAsIs("")));
        return this;
    }

    @Override
    public Element buildImpl() {
        return new Notification(this.id, this.i18nText, notificationType);
    }

    public static NotificationBuilder Notification(final String id) {
        return new NotificationBuilder(id);
    }

    public static NotificationBuilder Info(final String id) {
        return new NotificationBuilder(id).type(Notification.NotificationType.INFO);
    }

    public static NotificationBuilder Info() {
        return new NotificationBuilder(ElementUtil.randomId()).type(Notification.NotificationType.INFO);
    }
    public static NotificationBuilder Warning(final String id) {
        return new NotificationBuilder(id).type(Notification.NotificationType.WARNING);
    }

}
