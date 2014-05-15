package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ContainedInOtherFieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.LengthValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class ElementBuilder {

    final String id;
    Integer size;
    boolean required;
    String key;
    String placeholder;
    I18nText i18nText;
    String pattern;
    Integer maxLength;
    boolean inline;
    private List<Validator> validators = new ArrayList<Validator>();
    private String containsInField;


    protected ElementBuilder(String id) {
        this.id = id;
    }

    public final Element build(final FormParameters formParameters) {
        if (this.i18nText == null) {
            if (key == null) {
                key = id;
            }
            this.i18nText = formParameters.getI18nText(key);
        }
        Element element = buildImpl(formParameters);

        ElementUtil.setHelp(element, key + ".help", formParameters);
        ElementUtil.addSizeAttribute(element, size);
        if (required) {
            ElementUtil.addRequiredValidator(element, formParameters);
        }
        if (pattern != null) {
            ElementUtil.createRegexValidator(id, pattern, formParameters);
        }
        if (placeholder != null) {
            element.addAttribute("placeholder", placeholder);
        }
        I18nText errorMessage = formParameters.getI18nText("yleinen.virheellinenArvo");
        if (maxLength != null) {
            element.addAttribute("maxlength", maxLength);
            element.setValidator(new LengthValidator(element.getId(), errorMessage, maxLength));
        }
        if (containsInField != null) {
            validators.add(new ContainedInOtherFieldValidator(id,
                    containsInField, errorMessage));
        }
        return element;
    }

    public final Element build() {
        Element element = buildImpl();
        ElementUtil.addSizeAttribute(element, size);
        return element;
    }

    public abstract Element buildImpl(final FormParameters formParameters);

    public abstract Element buildImpl();

    public ElementBuilder labelKey(final String key) {
        this.key = key;
        return this;
    }

    public ElementBuilder inline() {
        this.inline = true;
        return this;
    }

    public ElementBuilder required() {
        this.required = true;
        return this;
    }

    public ElementBuilder size(int s) {
        this.size = s;
        return this;
    }

    public ElementBuilder placeholder(final String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public ElementBuilder maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public ElementBuilder validator(final Validator validator) {
        validators.add(validator);
        return this;
    }

    public ElementBuilder i18nText(final I18nText i18nText) {
        this.i18nText = i18nText;
        return this;
    }

    public ElementBuilder pattern(final String pattern) {
        this.pattern = pattern;
        return this;
    }

    public ElementBuilder containsInField(String id) {
        this.containsInField = id;
        return this;
    }
}
