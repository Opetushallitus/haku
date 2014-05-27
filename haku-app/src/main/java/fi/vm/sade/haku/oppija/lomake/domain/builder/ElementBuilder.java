package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ContainedInOtherFieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.LengthValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RegexFieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RequiredFieldValidator;
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
    private FormParameters formParameters;
    final List<Element> children = new ArrayList<Element>();


    protected ElementBuilder(String id) {
        this.id = id;
    }

    public final Element build() {
        if (this.i18nText == null) {
            if (key == null) {
                key = id;
            }
            this.i18nText = getI18nText(key);
        }
        Element element = buildImpl();

        element.setHelp(getI18nText(key + ".help"));
        ElementUtil.setVerboseHelp(element, getI18nText(key + ".verboseHelp"));

        if (size != null) {
            element.addAttribute("size", size.toString());
        }
        if (required) {
            String required = "required";
            element.addAttribute(required, required);
            element.setValidator(
                    new RequiredFieldValidator(
                            id,
                            getI18nText("yleinen.pakollinen")));
        }
        if (pattern != null) {
            element.setValidator(new RegexFieldValidator(id, getI18nText("yleinen.virheellinenArvo"), pattern));
        }
        if (placeholder != null) {
            element.addAttribute("placeholder", placeholder);
        }
        I18nText errorMessage = getI18nText("yleinen.virheellinenArvo");
        if (maxLength != null) {
            element.addAttribute("maxlength", maxLength.toString());
            element.setValidator(new LengthValidator(element.getId(), errorMessage, maxLength));
        }
        if (containsInField != null) {
            element.setValidator(new ContainedInOtherFieldValidator(id,
                    containsInField, errorMessage));
        }
        element.setInline(this.inline);
        element.setValidators(validators);
        for (Element child : children) {
            element.addChild(child);
        }
        return element;
    }

    private I18nText getI18nText(final String key) {
        if (this.formParameters != null) {
            return this.formParameters.getI18nText(key);
        }
        return ElementUtil.createI18NAsIs(key);
    }

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

    public ElementBuilder requiredInline() {
        return required().inline();
    }

    public ElementBuilder formParams(FormParameters formParameters) {
        this.formParameters = formParameters;
        return this;
    }

    public ElementBuilder addChild(ElementBuilder elementBuilder) {
        this.children.add(elementBuilder.formParams(this.formParameters).build());
        return this;
    }

    public ElementBuilder addChild(Element element) {
        this.children.add(element);
        return this;
    }
}
