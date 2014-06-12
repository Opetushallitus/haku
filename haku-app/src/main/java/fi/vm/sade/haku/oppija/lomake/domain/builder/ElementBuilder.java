package fi.vm.sade.haku.oppija.lomake.domain.builder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
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
    I18nText help;
    I18nText verboseHelp;
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

        if (key == null) {
            key = id;
        }
        if (this.i18nText == null) {
            this.i18nText = getI18nText(key, false);
        }
        Element element = buildImpl();
        if (help != null) {
            element.setHelp(help);
        } else {
            element.setHelp(getI18nText(key + ".help"));
        }
        if (verboseHelp == null) {
            ElementUtil.setVerboseHelp(element, verboseHelp);
        } else {
            ElementUtil.setVerboseHelp(element, getI18nText(key + ".verboseHelp"));
        }

        if (size != null) {
            element.addAttribute("size", size.toString());
        }
        if (required) {
            String required = "required";
            element.addAttribute(required, required);
            element.setValidator(
                    new RequiredFieldValidator(
                            getI18nText("yleinen.pakollinen", false)));
        }
        if (pattern != null) {
            element.setValidator(new RegexFieldValidator(getI18nText("yleinen.virheellinenArvo"), pattern));
        }
        if (placeholder != null) {
            element.addAttribute("placeholder", placeholder);
        }
        I18nText errorMessage = getI18nText("yleinen.virheellinenArvo");
        if (maxLength != null) {
            element.addAttribute("maxlength", maxLength.toString());
            element.setValidator(new LengthValidator(errorMessage, maxLength));
        }
        if (containsInField != null) {
            element.setValidator(new ContainedInOtherFieldValidator(containsInField, errorMessage));
        }
        element.setInline(this.inline);
        element.setValidators(validators);
        for (Element child : children) {
            element.addChild(child);
        }
        return element;
    }

    I18nText getI18nText(final String key) {
        return getI18nText(key, true);
    }

    I18nText getI18nText(final String key, boolean ignoreMissing) {
        if (this.formParameters != null) {
            return this.formParameters.getI18nText(key);
        }
        return (ignoreMissing ? null : ElementUtil.createI18NAsIs(key));
    }

    abstract Element buildImpl();

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

    public static Element[] buildAll(final FormParameters formParameters, final ElementBuilder... elementBuilders) {
        return Lists.transform(Lists.newArrayList(elementBuilders), new Function<ElementBuilder, Element>() {
            @Override
            public Element apply(ElementBuilder elementBuilder) {
                return elementBuilder.formParams(formParameters).build();
            }
        }).toArray(new Element[elementBuilders.length]);
    }

    public void help(I18nText help) {
        this.help = help;
    }

    public void verboseHelp(I18nText verboseHelp) {
        this.verboseHelp = verboseHelp;
    }
}
