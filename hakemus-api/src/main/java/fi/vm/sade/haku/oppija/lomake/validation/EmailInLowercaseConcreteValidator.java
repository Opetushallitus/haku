package fi.vm.sade.haku.oppija.lomake.validation;

import com.google.common.base.Strings;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailInLowercaseConcreteValidator implements Validator {
    private final I18nBundleService i18nBundleService;

    @Autowired
    public EmailInLowercaseConcreteValidator(I18nBundleService i18nBundleService) {
        this.i18nBundleService = i18nBundleService;
    }

    public ValidationResult validate(final ValidationInput validationInput) {
        final String email = validationInput.getValue();
        if (!Strings.nullToEmpty(lower(email)).equals(Strings.nullToEmpty(email))) {
            I18nText texts = i18nBundleService.getBundle(validationInput.getApplicationSystemId()).get("form.email.lowercase");
            return new ValidationResult(OppijaConstants.ELEMENT_ID_EMAIL, texts);
        }
        return new ValidationResult();
    }
    private String lower(String email) {
        if (!Strings.isNullOrEmpty(email)) {
            return email.toLowerCase();
        }
        return email;
    }
}
