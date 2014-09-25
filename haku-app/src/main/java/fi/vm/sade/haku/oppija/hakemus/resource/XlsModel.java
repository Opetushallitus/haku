package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.OptionQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class XlsModel {

    public final String hakukausiVuosi;
    public final String aoid;
    public final String asId;
    public final String asName;

    private final ApplicationSystem applicationSystem;
    private final List<Map<String, Object>> applications;
    private final String lang;
    private final ArrayTable<String, Element, Object> table;
    private final Map<String, Element> questionMap;
    private final List<Element> columnKeyList;

    public XlsModel(final String aoid,
                    final ApplicationSystem applicationSystem,
                    final List<Map<String, Object>> applications,
                    final String lang) {

        this.aoid = aoid;
        this.applicationSystem = applicationSystem;
        this.applications = applications;
        this.lang = lang;
        this.hakukausiVuosi = applicationSystem.getHakukausiVuosi().toString();
        this.asId = applicationSystem.getId();
        this.asName = applicationSystem.getName().getTranslations().get(lang);

        List<Element> questions = findQuestions(applicationSystem, lang);

        List<String> asids = Lists.transform(applications, new Function<Map<String, Object>, String>() {
            @Override
            public String apply(Map<String, Object> input) {
                return (String) input.get("oid");
            }
        });

        questionMap = new HashMap<String, Element>();
        for (Element question : questions) {
            questionMap.put(question.getId(), question);
        }


        table = ArrayTable.create(asids, questions);

        for (Map<String, Object> application : applications) {

            Map<String, String> answers = getAllAnswers(application);
            List<Element> questionsWithAnswers = findQuestionsWithAnswers(applicationSystem, lang, answers);
            Map<String, Element> qMap = new HashMap<String, Element>();
            for (Element questionsWithAnswer : questionsWithAnswers) {
                qMap.put(questionsWithAnswer.getId(), questionsWithAnswer);
            }
            Map<String, Object> vastaukset = (Map<String, Object>) application.get("answers");
            for (Map.Entry<String, Object> vastauksetVaiheittain : vastaukset.entrySet()) {
                Map<String, String> vaiheenVastaukset = (Map<String, String>) vastauksetVaiheittain.getValue();
                for (Map.Entry<String, String> vastaus : vaiheenVastaukset.entrySet()) {
                    if (table.containsColumn(questionMap.get(vastaus.getKey())) && isNotEmpty(vastaus.getValue()) && qMap.containsKey(vastaus.getKey())) {
                        Element question = questionMap.get(vastaus.getKey());
                        String questionAnswer = getQuestionAnswer(vastaus.getValue(), question);
                        table.put((String) application.get("oid"), question, questionAnswer);
                    }
                }
            }
        }
        columnKeyList = Lists.newArrayList(Iterables.filter(table.columnKeyList(), new Predicate<Element>() {
            @Override
            public boolean apply(Element input) {
                return isQuestionAnswered(input);
            }
        }));

    }

    private Map<String, String> getAllAnswers(Map<String, Object> application) {
        Map<String, Object> vastaukset = (Map<String, Object>) application.get("answers");
        Map<String, String> allAnswers = new HashMap<String, String>();
        for (Map.Entry<String, Object> vastauksetVaiheittain : vastaukset.entrySet()) {
            vastaukset.putAll((Map<String, String>) vastauksetVaiheittain.getValue());
        }
        return allAnswers;
    }

    private List<Element> findQuestions(ApplicationSystem applicationSystem, final String lang) {
        return findQuestionsWithAnswers(applicationSystem, lang, null);
    }

    private List<Element> findQuestionsWithAnswers(ApplicationSystem applicationSystem, final String lang, Map<String, String> answers) {
        return ElementUtil.filterElements(applicationSystem.getForm(), new Predicate<Element>() {
            @Override
            public boolean apply(Element element) {
                return Question.class.isAssignableFrom(element.getClass())
                        && ElementUtil.getText(element, lang) != null;
            }
        });
    }

    public boolean isQuestionAnswered(final Element key) {
        Optional<Object> optional = Iterables.tryFind(table.column(key).values(), Predicates.notNull());
        return optional.isPresent();
    }


    public List<Map<String, Object>> getApplications() {
        return applications;
    }

    public ArrayTable<String, Element, Object> getTable() {
        return table;
    }

    private String getQuestionAnswer(String vastaus, Element question) {
        String value = vastaus;
        if (question instanceof OptionQuestion) {
            Option option = ((OptionQuestion) question).getData().get(vastaus);
            if (option != null) {
                value = ElementUtil.getText(option, lang);
            }
        } else if (question instanceof CheckBox) {
            return Boolean.TRUE.toString().equals(vastaus) ? "X" : "";
        }
        return value;
    }


    public String getValue(String rowKey, Element colKey) {
        return (String) table.get(rowKey, colKey);
    }

    public List<Element> columnKeyList() {
        return this.columnKeyList;
    }

    public String getText(Element element) {
        return ElementUtil.getText(element, lang);
    }

    public String getHakukausi(List<Option> options) {
        String hakukausi = null;
        for (Option option : options) {
            if (option.getValue().equals(applicationSystem.getHakukausiUri())) {
                hakukausi = ElementUtil.getText(option, lang);
            }
        }
        return hakukausi;
    }

}
