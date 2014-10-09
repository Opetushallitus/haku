package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
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
    public final String asId;
    public final String asName;
    public final ApplicationOption ao;
    private final ApplicationSystem applicationSystem;
    private final List<Map<String, Object>> applications;
    private final ArrayTable<String, Element, Object> table;

    private final String lang;
    private final List<Element> columnKeyList;

    public XlsModel(final ApplicationOption ao,
                    final ApplicationSystem applicationSystem,
                    final List<Map<String, Object>> applications,
                    final String lang) {

        this.ao = ao;
        this.applicationSystem = applicationSystem;
        this.applications = applications;
        this.lang = lang;
        this.hakukausiVuosi = applicationSystem.getHakukausiVuosi().toString();
        this.asId = applicationSystem.getId();
        this.asName = applicationSystem.getName().getTranslations().get(lang);

        List<Element> questions = findQuestions(applicationSystem, ao, lang);
        List<String> aids = Lists.transform(applications, ELEMENT_TO_OID_FUNCTION);

        table = ArrayTable.create(aids, questions);

        for (Map<String, Object> application : applications) {
            Map<String, String> answers = getAllAnswers(application);
            List<Element> applicationQuestions = findQuestionsWithAnswers(applicationSystem, ao, lang, answers);

            for (Element applicationQuestion : applicationQuestions) {
                if (table.containsColumn(applicationQuestion) && isNotEmpty(answers.get(applicationQuestion.getId()))) {
                    String questionAnswer = getQuestionAnswer(answers.get(applicationQuestion.getId()), applicationQuestion);
                    table.put((String) application.get("oid"), applicationQuestion, questionAnswer);
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
            allAnswers.putAll((Map<String, String>) vastauksetVaiheittain.getValue());
        }
        return allAnswers;
    }

    private List<Element> findQuestions(ApplicationSystem applicationSystem, final ApplicationOption ao, final String lang) {
        return findQuestionsWithAnswers(applicationSystem, ao, lang, null);
    }

    private List<Element> findQuestionsWithAnswers(ApplicationSystem applicationSystem, final ApplicationOption ao, final String lang, Map<String, String> answers) {
        return ElementUtil.filterElements(applicationSystem.getForm(), new Predicate<Element>() {
            @Override
            public boolean apply(Element element) {
                if (Question.class.isAssignableFrom(element.getClass()) && ElementUtil.getText(element, lang) != null) {
                    String applicationOptionGroupId = ((Question) element).getApplicationOptionGroupId();
                    String applicationOptionId = ((Question) element).getApplicationOptionId();
                    List<String> groups = ao.getGroups();

                    if (applicationOptionGroupId == null && applicationOptionId == null) {
                        return true;
                    } else if (applicationOptionGroupId != null && groups != null) {
                        if (groups.contains(applicationOptionGroupId)) {
                            return true;
                        }
                    } else {
                        return ao.getId() != null && (ao.getId().equals(applicationOptionGroupId) || ao.getId().equals(applicationOptionId));
                    }
                }
                return false;
            }
        }, answers);
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


    private String getQuestionAnswer(String answer, Element question) {
        String value = answer;
        if (question instanceof OptionQuestion) {
            Option option = ((OptionQuestion) question).getData().get(answer);
            if (option != null) {
                value = ElementUtil.getText(option, lang);
            }
        } else if (question instanceof CheckBox) {
            return Boolean.TRUE.toString().equals(answer) ? "X" : "";
        }
        return value;
    }

    public String getValue(String rowKey, Element colKey) {
        return (String) table.get(rowKey, colKey);
    }

    public List<Element> columnKeyList() {
        return this.columnKeyList;
    }
    public List<String> rowKeyList() {
        return table.rowKeyList();
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

    public static final Function<Map<String, Object>, String> ELEMENT_TO_OID_FUNCTION = new Function<Map<String, Object>, String>() {
        @Override
        public String apply(Map<String, Object> input) {
            return (String) input.get("oid");
        }
    };

}
