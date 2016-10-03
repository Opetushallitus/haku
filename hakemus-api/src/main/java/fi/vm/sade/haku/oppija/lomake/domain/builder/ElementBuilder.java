package fi.vm.sade.haku.oppija.lomake.domain.builder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ElementBuilder {

    final String id;
    Integer size;
    boolean required;
    String key;
    I18nText help;
    String pattern;
    Integer maxLength;
    Integer minOptions;
    Integer maxOptions;
    boolean inline;
    private List<Validator> validators = new ArrayList<Validator>();
    private String containsInField;
    private FormParameters formParameters;
    final List<Element> children = new ArrayList<Element>();
    private String applicationOptionId;
    private String applicationOptionGroupId;


    protected ElementBuilder(String id) {
        this.id = id;
    }

    public final Element build() {

        if (key == null) {
            key = id;
        }
        prepareBuild();
        Element element = buildImpl();
        if (help != null) {
            element.setHelp(help);
        } else {
            element.setHelp(getI18nText(key + ".help"));
        }

        if (size != null) {
            element.addAttribute("size", size.toString());
        }
        if (required) {
            String required = "required";
            element.addAttribute(required, required);
            element.setValidator(
                    new RequiredFieldValidator("yleinen.pakollinen"));
        }
        if (minOptions != null && maxOptions != null) {
            element.setValidator(new MinMaxOptionsValidator("yleinen.virheellinenarvo",
                    minOptions, maxOptions));
        }
        if (pattern != null) {
            element.setValidator(new RegexFieldValidator("yleinen.virheellinenarvo", pattern));
        }

        if (maxLength != null) {
            element.addAttribute("maxlength", maxLength.toString());
            element.setValidator(new LengthValidator("yleinen.virheellinenarvo", maxLength));
        }
        if (containsInField != null) {
            element.setValidator(new ContainedInOtherFieldValidator(containsInField, "yleinen.virheellinenarvo"));
        }
        element.setInline(this.inline);
        element.setValidators(validators);
        for (Element child : children) {
            element.addChild(child);
        }
        if (element instanceof Question) {
            ((Question) element).setApplicationOptionId(this.applicationOptionId);
            ((Question) element).setApplicationOptionGroupId(this.applicationOptionGroupId);
        }
        return finishBuild(element);
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

    protected void prepareBuild() {
        // NOP
    }

    protected Element finishBuild(Element element) {
        return element;
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

    public ElementBuilder maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public ElementBuilder maxOptions(int maxOptions) {
        this.maxOptions = maxOptions;
        return this;
    }

    public ElementBuilder minOptions(int minOptions) {
        this.minOptions = minOptions;
        return this;
    }

    public ElementBuilder validator(final Validator validator) {
        validators.add(validator);
        return this;
    }

    public ElementBuilder pattern(final String pattern) {
        this.pattern = pattern;
        return this;
    }

    public ElementBuilder containsInField(final String id) {
        this.containsInField = id;
        return this;
    }

    public ElementBuilder requiredInline() {
        return required().inline();
    }

    public ElementBuilder formParams(final FormParameters formParameters) {
        this.formParameters = formParameters;
        return this;
    }

    public ElementBuilder addChild(final ElementBuilder elementBuilder) {
        this.children.add(elementBuilder.formParams(this.formParameters).build());
        return this;
    }

    public ElementBuilder addChild(final Element... elements) {
        for (Element element : elements) {
            this.children.add(element);
        }
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

    public ElementBuilder help(final I18nText help) {
        this.help = emptyToNull(help);
        this.help = ensureTranslations(this.help);
        return this;
    }

    protected I18nText emptyToNull(final I18nText i18nText) {
        return i18nText == null || i18nText.isEmpty() ? null : i18nText;
    }

    protected I18nText ensureTranslations(final I18nText i18nText){
        if (null == i18nText)
            return null;
        return TranslationsUtil.ensureDefaultLanguageTranslations(i18nText);
    }

    public ElementBuilder applicationOptionGroupId(String applicationOptionGroupId) {
        this.applicationOptionGroupId = applicationOptionGroupId;
        return this;
    }

    public ElementBuilder applicationOptionId(String applicationOptionId) {
        this.applicationOptionId = applicationOptionId;
        return this;
    }
}
