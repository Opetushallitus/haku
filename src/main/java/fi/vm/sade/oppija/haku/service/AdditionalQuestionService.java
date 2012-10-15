package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public interface AdditionalQuestionService {

    public List<Question> findAdditionalQuestions(String teemaId, HakemusId hakemusId);

    public List<Question> findAdditionalQuestions(String teemaId, List<String> hakukohdeIds);

}
