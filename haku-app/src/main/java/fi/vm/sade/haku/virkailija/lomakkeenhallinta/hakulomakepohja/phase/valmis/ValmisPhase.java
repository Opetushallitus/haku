package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Link;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Answer;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.DiscretionaryAttachments;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Print;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class ValmisPhase {

    private static final String REGEX_NON_EMPTY = ".*\\S.*";
    public static final Set<String> MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES =
            Sets.newHashSet(EDUCATION_CODE_MUSIIKKI, EDUCATION_CODE_TANSSI, EDUCATION_CODE_LIIKUNTA);


    public static List<Element> create(final String formMessages, final String... paragraphs) {
        List<Element> elements = Lists.newArrayList();

        RelatedQuestionRule emailRule = new RelatedQuestionRule("emailRule", "Sähköposti", REGEX_NON_EMPTY, false);
        Text emailP1 = new Text("emailP1", createI18NText("form.valmis.sinulleonlahetettyvahvistussahkopostiisi",
                formMessages));
        emailP1.addChild(new Answer("Sähköposti"));
        emailRule.addChild(emailP1);

        elements.add(emailRule);

        elements.add(new Text("valmisP1", createI18NText("form.lomake.valmis.p1", formMessages)));
        elements.add(new Text("valmisP2", createI18NText("form.lomake.valmis.p2", formMessages)));
        elements.add(new Text("valmisP3", createI18NText("form.lomake.valmis.p3", formMessages)));

        elements.add(new Print("printLink", createI18NText("form.valmis.button.tulosta", formMessages)));

        elements.add(new DiscretionaryAttachments("discretionaryAttachments"));

        elements.addAll(createAdditionalInformationElements(formMessages));


        TitledGroup muutoksenTekeminen = new TitledGroup("muutoksenTekeminen", createI18NText("form.valmis.muutoksentekeminen",
                formMessages));

        for (int i = 0; i < paragraphs.length; i++) {
            muutoksenTekeminen.addChild(new Text("muutoksenTekeminenP" + (i + 1), createI18NText(paragraphs[i], formMessages)));
        }
        elements.add(muutoksenTekeminen);

        elements.add(new Text("palaute", createI18NText("form.valmis.palaute", formMessages)));

        elements.add(new Link("backLink", createI18NAsIs("https://opintopolku.fi"), createI18NText("form.valmis.takaisin.opintopolkuun.linkki",
                formMessages)));

        return elements;
    }

    public static List<Element> createAdditionalInformationElements(String formMessages) {
        RelatedQuestionRule athleteRule = new RelatedQuestionRule("athleteRule",
                Lists.newArrayList("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference1_urheilijalinjan_lisakysymys",
                        "preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference2_urheilijalinjan_lisakysymys",
                        "preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference3_urheilijalinjan_lisakysymys",
                        "preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference4_urheilijalinjan_lisakysymys",
                        "preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference5_urheilijalinjan_lisakysymys"),
                "^true", false);
        TitledGroup athleteGroup = new TitledGroup("atheleteGroup", createI18NText("form.valmis.haeturheilijana.header", formMessages));

        athleteGroup.addChild(new Text("athleteP1", createI18NText("form.valmis.haeturheilijana", formMessages)));
        Link athleteLink = new Link("athleteLink", createI18NText("form.valmis.haeturheilijana.linkki.url", formMessages),
                createI18NText("form.valmis.haeturheilijana.linkki.text", formMessages));
        athleteLink.addAttribute("target", "_blank");
        athleteGroup.addChild(athleteLink);
        athleteRule.addChild(athleteGroup);

        //Hait musiikki-, tanssi- tai liikunta-alan koulutukseen.
        Element musiikkiTanssiLiikuntaRule = new RelatedQuestionRule("musiikkiTanssiLiikuntaRule",
                Lists.newArrayList(String.format(EDUCATION_CODE_KEY, 1),
                        String.format(EDUCATION_CODE_KEY, 2),
                        String.format(EDUCATION_CODE_KEY, 3),
                        String.format(EDUCATION_CODE_KEY, 4),
                        String.format(EDUCATION_CODE_KEY, 5)),
                StringUtils.join(MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES, "|"), false);
        TitledGroup musiikkiTanssiLiikuntaGroup = new TitledGroup("mtlGroup", createI18NText("form.valmis.musiikkitanssiliikunta.header", formMessages));
        musiikkiTanssiLiikuntaGroup.addChild(new Text(randomId(), createI18NText("form.valmis.musiikkitanssiliikunta", formMessages)));
        musiikkiTanssiLiikuntaRule.addChild(musiikkiTanssiLiikuntaGroup);

        return Lists.newArrayList(athleteRule, musiikkiTanssiLiikuntaRule);
    }
}
