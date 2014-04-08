package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

import fi.vm.sade.haku.oppija.common.dao.BaseDAO;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;

public interface ThemeQuestionDAO extends BaseDAO<ThemeQuestion> {

    ThemeQuestion findById(String id);
}
