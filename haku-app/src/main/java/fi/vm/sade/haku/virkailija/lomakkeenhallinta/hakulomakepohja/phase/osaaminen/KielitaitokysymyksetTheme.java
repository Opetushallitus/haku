package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import com.google.common.base.CaseFormat;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.And;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Not;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Or;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseYhteishakuKevat.haettuAmmatilliseenOppilaitokseenKielella;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public final class KielitaitokysymyksetTheme {

    private static final String[] LANGUAGE_QUESTIONS = new String[]{
            "aidinkieli",
            OppijaConstants.PERUSOPETUS_KIELI,
            OppijaConstants.LUKIO_KIELI,
    };

    private KielitaitokysymyksetTheme() {
    }

    public static Element createKielitaitokysymyksetTheme(final FormParameters formParameters) {
        Expr suomiOnAidinkieliTaiKouluSuomeksi = ExprUtil.atLeastOneVariableEqualsToValue("FI", LANGUAGE_QUESTIONS);
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksi = ExprUtil.atLeastOneVariableEqualsToValue("SV", LANGUAGE_QUESTIONS);

        Expr haettuSuomenkieliseenAmmatilliseenKoulutukseen = haettuAmmatilliseenOppilaitokseenKielella(OppijaConstants.EDUCATION_LANGUAGE_FI, 5);
        Expr haettuRuotsinkieliseenAmmatilliseenKoulutukseen = haettuAmmatilliseenOppilaitokseenKielella(OppijaConstants.EDUCATION_LANGUAGE_SV, 5);

        Expr kysytaankoSuomi = new And(haettuSuomenkieliseenAmmatilliseenKoulutukseen, new Not(suomiOnAidinkieliTaiKouluSuomeksi));
        Expr kysytaankoRuotsi = new And(haettuRuotsinkieliseenAmmatilliseenKoulutukseen, new Not(ruotsiOnAidinkieliTaiKouluRuotsiksi));

        Expr naytetaankoKielitaitoteema = new Or(kysytaankoSuomi, kysytaankoRuotsi);

        RelatedQuestionComplexRule naytetaankoTeema = new RelatedQuestionComplexRule(ElementUtil.randomId(), naytetaankoKielitaitoteema);

        Theme kielitaitokysymyksetTheme =
                new Theme("KielitaitokysymyksetTheme", ElementUtil.createI18NText("form.kielitaito.otsikko", formParameters.getFormMessagesBundle()), true);
        naytetaankoTeema.addChild(kielitaitokysymyksetTheme);

        RelatedQuestionComplexRule naytetaankoSuomi = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSuomi);
        RelatedQuestionComplexRule naytetaankoRuotsi = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoRuotsi);
        naytetaankoSuomi.addChild(createKielitutkinto("yleinen_kielitutkinto_fi", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", formParameters));
        naytetaankoRuotsi.addChild(createKielitutkinto("yleinen_kielitutkinto_sv", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", formParameters));
        kielitaitokysymyksetTheme.addChild(naytetaankoSuomi, naytetaankoRuotsi);
        ElementUtil.setVerboseHelp(kielitaitokysymyksetTheme, "form.kielitaito.otsikko.verboseHelp", formParameters);
        return naytetaankoTeema;
    }


    private static Radio createKielitutkinto(final String id, final FormParameters formParameters) {
        Radio radio = new Radio(id,
                createI18NText("form.kielitaito." +
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, id).replace('_', '.'), formParameters.getFormMessagesBundle()));
        addDefaultTrueFalseOptions(radio, formParameters);
        addRequiredValidator(radio, formParameters);
        return radio;
    }
}
