package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import com.google.common.collect.Sets;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeOptionQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestionCompact;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestionOption;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ThemeQuestionConverter {

    @Autowired
    private HakukohdeService hakukohdeService;

    public ThemeQuestionCompact convert(ThemeQuestion question, String lang) {
        ThemeQuestionCompact questionCompact = new ThemeQuestionCompact();

        questionCompact.setMessageText(question.getMessageText().getText(lang));
        questionCompact.setType(question.getClass().getSimpleName());
        questionCompact.setApplicationOptionOids(getApplicationOptionOids(question));

        if (question instanceof ThemeOptionQuestion) {
            questionCompact.setOptions(convertOptions((ThemeOptionQuestion) question, lang));
        }

        return questionCompact;
    }

    private Set<String> getApplicationOptionOids(ThemeQuestion question) {
        if (question.getTargetIsGroup()) {
            return Sets.newHashSet(hakukohdeService.findByGroupAndApplicationSystem(
                    question.getLearningOpportunityId(), question.getApplicationSystemId()
            ));
        } else {
            return Sets.newHashSet(question.getLearningOpportunityId());
        }
    }

    public static Map<String, String> convertOptions(ThemeOptionQuestion optionQuestion, String lang) {
        Map<String, String> options = new HashMap<>();

        for (ThemeQuestionOption option : optionQuestion.getOptions()) {
            options.put(option.getId(), option.getOptionText().getText(lang));
        }

        return options;
    }

}
