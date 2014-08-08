package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;

import java.util.List;

public class ThemeQuestionDAOMockImpl implements ThemeQuestionDAO {

    @Override
    public ThemeQuestion findById(String id) {
        return null;
    }

    @Override
    public List<ThemeQuestion> query(ThemeQuestionQueryParameters parameters) {
        return ImmutableList.of();
    }

    @Override
    public List<String> queryApplicationOptionsIn(ThemeQuestionQueryParameters parameters) {
        return ImmutableList.of();
    }

    @Override
    public void setOrdinal(String themeQuestionId, Integer newOrdinal) {
    //TODO =RS= Do something maybe someday....
    }

    @Override
    public Boolean validateLearningOpportunityAndTheme(String learningOpportunityId, String themeId, String... themeQuestionIds) {
        //TODO =RS= not too sane
        return false;
    }

    @Override
    public List<ThemeQuestion> find(ThemeQuestion themeQuestion) {
        return ImmutableList.of();
    }

    @Override
    public void update(ThemeQuestion o, ThemeQuestion n) {

    }

    @Override
    public void save(ThemeQuestion themeQuestion) {

    }
}
