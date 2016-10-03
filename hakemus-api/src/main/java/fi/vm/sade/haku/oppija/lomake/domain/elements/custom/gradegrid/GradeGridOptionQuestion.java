package fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid;

import com.google.common.base.Preconditions;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import java.util.*;

public class GradeGridOptionQuestion extends Element {

    private final List<Option> options;
    private final boolean selected;
    private final boolean sortByText;
    private final String defaultValueAttribute;
    @Transient
    private Map<String, List<Option>> optionsSortedByText;

    @PersistenceConstructor
    public GradeGridOptionQuestion(final String id,
                                   final List<Option> options,
                                   final boolean selected,
                                   final Boolean sortByText,
                                   final String defaultValueAttribute) {
        super(id);
        Preconditions.checkNotNull(options);
        Preconditions.checkNotNull(selected);
        this.options = options;
        this.selected = selected;
        this.sortByText = sortByText != null ? sortByText.booleanValue() : false;
        this.defaultValueAttribute = defaultValueAttribute;
    }

    public List<Option> getOptions() {
        return options;
    }

    @Transient
    public Map<String, List<Option>> getOptionsSortedByText() {
        if (optionsSortedByText == null) {
            initSortedOptions();
        }
        return optionsSortedByText;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isSortByText() {
        return sortByText;
    }

    public String getDefaultValueAttribute() {
        return defaultValueAttribute;
    }

    private void initSortedOptions() {
        optionsSortedByText = new HashMap<String, List<Option>>();
        for (Option option : options) {
            for (String lang : option.getI18nText().getAvailableLanguages()) {
                List<Option> optionListForLang = optionsSortedByText.get(lang);
                if (optionListForLang == null) {
                    optionListForLang = new ArrayList<Option>(options.size());
                    optionsSortedByText.put(lang, optionListForLang);
                }
                optionListForLang.add(option);
            }
        }
        for (Map.Entry<String, List<Option>> entry : optionsSortedByText.entrySet()) {
            List<Option> optionList = entry.getValue();
            final String lang = entry.getKey();
            Collections.sort(optionList, new Comparator<Option>() {
                @Override
                public int compare(Option o1, Option o2) {
                    String o1Trans = o1.getI18nText().getText(lang);
                    String o2Trans = o2.getI18nText().getText(lang);
                    return o1Trans.compareTo(o2Trans);
                }
            });
        }
    }
}
