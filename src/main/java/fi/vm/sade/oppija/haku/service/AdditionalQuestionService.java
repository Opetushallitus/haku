package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves education specific additional questions related to different themes.
 *
 * @author Hannu Lyytikainen
 */
public interface AdditionalQuestionService {

    /**
     * Lists questions in a given teema based on current answers.
     *
     * @param teemaId teema id
     * @param hakemusId hakemus id
     * @return list of questions
     */
    public Set<Question> findAdditionalQuestions(String teemaId, HakemusId hakemusId);

    /**
     * Lists additional questions in a theme based on a list of education targets.
     *
     * @param teemaId teema id
     * @param hakukohdeIds education targets
     * @param hakemusId hakemus id
     * @return list of questions
     */
    public Set<Question> findAdditionalQuestions(String teemaId, List<String> hakukohdeIds, HakemusId hakemusId);

    /**
     * Lists all additional questions in a phase. Questions are grouped by the theme they are related to.
     *
     * @param hakemusId hakemus id
     * @return map with theme ids as keys and questions lists as values
     */
    public Map<String, Set<Question>> findAdditionalQuestionsInCategory(HakemusId hakemusId);

}
