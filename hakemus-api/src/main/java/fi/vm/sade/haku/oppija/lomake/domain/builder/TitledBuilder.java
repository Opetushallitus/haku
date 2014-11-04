package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public abstract class TitledBuilder extends ElementBuilder {

    protected I18nText i18nText;
    protected String excelColumnLabelKey;
    protected I18nText excelColumnLabel;
    protected I18nText verboseHelp;

    protected TitledBuilder(String id) {
        super(id);
    }

    protected void prepareBuild() {
        if (this.i18nText == null) {
            this.i18nText = getI18nText(key, false);
        }
    }

    @Override
    protected Element finishBuild(Element element) {
        super.finishBuild(element);

        if (verboseHelp != null) {
            ElementUtil.setVerboseHelp(element, verboseHelp);
        } else {
            ElementUtil.setVerboseHelp(element, getI18nText(key + ".verboseHelp"));
        }
        I18nText placeholder = getI18nText(key + ".placeholder");
        if (placeholder != null) {
            ((Titled)element).setPlaceholder(placeholder);
        }
        if (isNotEmpty(excelColumnLabelKey)) {
            excelColumnLabel = getI18nText(excelColumnLabelKey);
        }
        ((Titled) element).setExcelColumnLabel(excelColumnLabel);
        return element;
    }

    public TitledBuilder verboseHelp(final I18nText verboseHelp) {
        this.verboseHelp = emptyToNull(verboseHelp);
        this.verboseHelp = ensureTranslations(this.verboseHelp);
        return this;
    }

    public TitledBuilder i18nText(I18nText i18nText) {
        this.i18nText = ensureTranslations(i18nText);
        return this;
    }

    public TitledBuilder excelColumnLabel(String labelKey) {
        this.excelColumnLabelKey = labelKey;
        return this;
    }

    public TitledBuilder excelColumnLabel(I18nText excelColumnLabel) {
        this.excelColumnLabel = excelColumnLabel;
        return this;
    }
}
