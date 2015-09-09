package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionGroup;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static org.apache.commons.lang.StringUtils.isNotBlank;
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
    private I18nBundle i18nBundle;
    private final List<Element> columnKeyList;

    private static final String ELIGIBILITY_STATUS = "eligibility_status";
    private static final String ELIGIBILITY_SOURCE = "eligibility_source";

    public XlsModel(final ApplicationOption ao,
                    final ApplicationSystem applicationSystem,
                    final List<Map<String, Object>> applications,
                    final String lang,
                    final I18nBundle i18nBundle) {

        this.ao = ao;
        this.applicationSystem = applicationSystem;
        this.applications = applications;
        this.lang = lang;
        this.hakukausiVuosi = applicationSystem.getHakukausiVuosi().toString();
        this.asId = applicationSystem.getId();
        this.asName = applicationSystem.getName().getTranslations().get(lang);
        this.i18nBundle = i18nBundle;

        List<Element> questions = findQuestions(applicationSystem, ao, lang);
        Map<String, Element> additionalQuestions = getAdditionalQuestions(i18nBundle);
        questions.addAll(additionalQuestions.values());
        List<String> aids = Lists.transform(applications, ELEMENT_TO_OID_FUNCTION);

        table = ArrayTable.create(aids, questions);

        Map<String, Element> specialColumns = getSpecialColumns(table);

        for (Map<String, Object> application : applications) {
            @SuppressWarnings("unchecked")
            Map<String, String> preferenceEligibility = getPreferenceEligibility(
                    (List<Map<String, String>>) application.get("preferenceEligibilities"), this.ao
            );
            if (preferenceEligibility != null) {
                table.put(
                        (String) application.get("oid"),
                        additionalQuestions.get(ELIGIBILITY_STATUS),
                        getTranslatedAnswer(i18nBundle, lang, preferenceEligibility.get(ELIGIBILITY_STATUS), "hakukelpoisuus_")
                );
                table.put(
                        (String) application.get("oid"),
                        additionalQuestions.get(ELIGIBILITY_SOURCE),
                        getTranslatedAnswer(i18nBundle, lang, preferenceEligibility.get(ELIGIBILITY_SOURCE), "hakukelpoisuus_lahde_")
                );
            }

            Map<String, String> answers = getAllAnswers(application);
            List<Element> applicationQuestions = findQuestionsWithAnswers(applicationSystem, ao, lang, answers);

            for (Element applicationQuestion : applicationQuestions) {
                if (table.containsColumn(applicationQuestion) && isNotEmpty(answers.get(applicationQuestion.getId()))) {
                    String questionAnswer = getQuestionAnswer(answers, applicationQuestion.getId(), applicationQuestion);

                    Element overrideQuestion = specialColumns.get(applicationQuestion.getId());
                    if (overrideQuestion != null) {
                        applicationQuestion = overrideQuestion;
                    }

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

    private static Map<String, Element> getSpecialColumns(ArrayTable table) {
        Map<String, Element> map = new HashMap<>();

        map.put("ssnSex", getColumn(table, "sukupuoli"));
        map.put("ssnDateOfBirthh", getColumn(table, "syntymaaika"));

        return map;
    }

    private static Element getColumn(ArrayTable table, String id) {
        for (Object obj : table.columnKeySet()) {
            Element element = (Element) obj;
            if (id.equals(element.getId())) {
                return element;
            }
        }
        return null;
    }

    private Map<String, String> getAllAnswers(Map<String, Object> application) {
        Map<String, Object> vastaukset = (Map<String, Object>) application.get("answers");
        Map<String, String> allAnswers = new HashMap<String, String>();
        for (Map.Entry<String, Object> vastauksetVaiheittain : vastaukset.entrySet()) {
            allAnswers.putAll((Map<String, String>) vastauksetVaiheittain.getValue());
        }
        allAnswers.put("oid", (String) application.get("oid"));
        allAnswers.put("personOid", (String) application.get("personOid"));
        return allAnswers;
    }

    private List<Element> findQuestions(ApplicationSystem applicationSystem, final ApplicationOption ao, final String lang) {
        return findQuestionsWithAnswers(applicationSystem, ao, lang, null);
    }

    private List<Element> findQuestionsWithAnswers(ApplicationSystem applicationSystem, final ApplicationOption ao, final String lang, Map<String, String> answers) {
        List<Element> elements = ElementUtil.filterElements(applicationSystem.getForm(), new Predicate<Element>() {
            @Override
            public boolean apply(Element element) {
                if (Question.class.isAssignableFrom(element.getClass()) && ElementUtil.getText(element, lang) != null) {
                    String applicationOptionGroupId = ((Question) element).getApplicationOptionGroupId();
                    String applicationOptionId = ((Question) element).getApplicationOptionId();
                    List<ApplicationOptionGroup> groups = ao.getGroups();

                    if (applicationOptionGroupId == null && applicationOptionId == null) {
                        return true;
                    } else if (applicationOptionGroupId != null && groups != null) {
                        for(ApplicationOptionGroup group: groups) {
                            if (applicationOptionGroupId.equals(group.oid)) {
                                return true;
                            }
                        }
                    } else {
                        return ao.getId() != null && (ao.getId().equals(applicationOptionGroupId) || ao.getId().equals(applicationOptionId));
                    }
                }
                return false;
            }
        }, answers);
        Element applicationOid = TextQuestion("oid")
                .i18nText(i18nBundle.get("hakemusnumero"))
                .build();
        elements.add(0, applicationOid);
        Element personOid = TextQuestion("personOid")
                .i18nText(i18nBundle.get("oppijanumero"))
                .build();
        elements.add(1, personOid);
        elements.add(2, new CheckBox("turvakielto", i18nBundle.get("turvakielto")));
        int elementsLength = elements.size();
        for (int i = 0; i < elementsLength; i++) {
            Element element = elements.get(i);
            Element[] extraExcelColumns = element.getExtraExcelColumns(i18nBundle);
            if (extraExcelColumns != null && extraExcelColumns.length > 0) {
                for (Element extraColumn : extraExcelColumns) {
                    elements.add(i+1, extraColumn);
                    elementsLength++;
                    i++;
                    if (answers != null) {
                        String answer = answers.get(element.getId());
                        answers.put(extraColumn.getId(), answer);
                    }
                }
            }
        }
        Element hakukohteenPrioriteetti = new TextQuestion("hakukohteenPrioriteetti", i18nBundle.get("Hakukohteen.prioriteetti"));
        elements.add(hakukohteenPrioriteetti);
        if (answers != null) {
            for (Map.Entry<String, String> entry : answers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.startsWith("preference") && key.endsWith("-Koulutus-id")
                        && isNotBlank(value) && value.equals(ao.getId())) {
                    String index = key.replace("preference", "").replace("-Koulutus-id", "");
                    answers.put(hakukohteenPrioriteetti.getId(), index);
                    break;
                }
            }
        }
        return elements;
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


    private String getQuestionAnswer(Map<String, String> answers, String answerKey, Element question) {
        String value = answers.get(answerKey);

        if (Question.class.isAssignableFrom(question.getClass())) {
            value = ((Question) question).getExcelValue(answers.get(answerKey), lang);
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

    public String getText(final Element element) {
        if (element instanceof Titled) {
            I18nText i18nText = ((Titled) element).getExcelColumnLabel();
            if (i18nText != null) {
                return i18nText.getTranslations().get(lang);
            }
        }

        return null;
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

    public static Map<String, String> getPreferenceEligibility(List<Map<String, String>> preferenceEligibilities, ApplicationOption ao) {
        if (preferenceEligibilities != null) {
            for (Map<String, String> preference : preferenceEligibilities) {
                if (ao.getId().equals(preference.get("aoId"))) {
                    Map<String, String> map = Maps.newHashMap();
                    map.put(ELIGIBILITY_STATUS, preference.get("status"));
                    map.put(ELIGIBILITY_SOURCE, preference.get("source"));
                    return map;
                }
            }
        }
        return null;
    }

    public static Map<String, Element> getAdditionalQuestions(I18nBundle i18nBundle) {
        Map<String, Element> elements = new HashMap<>();

        elements.put(ELIGIBILITY_STATUS, TextQuestion("hakukelpoisuus").i18nText(i18nBundle.get("hakukelpoisuus")).build());
        elements.put(ELIGIBILITY_SOURCE, TextQuestion("hakukelpoisuus_lahde").i18nText(i18nBundle.get("hakukelpoisuus_lahde")).build());

        return elements;
    }

    private static String getTranslatedAnswer(I18nBundle i18nBundle, String lang, String answer, String translationPrefix) {
        if (i18nBundle.get(translationPrefix + answer) != null) {
            return i18nBundle.get(translationPrefix + answer).getText(lang);
        }
        return answer;
    }

}
