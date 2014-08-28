package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

import fi.vm.sade.haku.oppija.common.dao.BaseDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;

import java.util.List;

public interface ThemeQuestionDAO extends BaseDAO<ThemeQuestion> {

    ThemeQuestion findById(String id);

    List<ThemeQuestion> query(ThemeQuestionQueryParameters parameters);

    List <String> queryApplicationOptionsIn(ThemeQuestionQueryParameters parameters);

    void setOrdinal(String themeQuestionId, Integer newOrdinal);

    void delete(String themeQuestionId);

    Boolean validateLearningOpportunityAndTheme(String learningOpportunityId, String themeId, String... themeQuestionIds);
}
