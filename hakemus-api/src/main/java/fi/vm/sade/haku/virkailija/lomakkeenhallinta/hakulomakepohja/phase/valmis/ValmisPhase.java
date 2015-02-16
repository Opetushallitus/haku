package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Link;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Answer;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.ApplicationAttachments;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Print;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;

import java.util.List;
import java.util.Set;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextBuilder.Text;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil.atLeastOneVariableEqualsToValue;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class ValmisPhase {

    private static final String REGEX_NON_EMPTY = ".*\\S.*";
    public static final Set<String> MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES =
            Sets.newHashSet(EDUCATION_CODE_MUSIIKKI, EDUCATION_CODE_TANSSI, EDUCATION_CODE_LIIKUNTA);


    public static List<Element> create(FormParameters formParameters, final String... paragraphs) {
        List<Element> elements = Lists.newArrayList();

        Element emailRule = ElementUtil.createRegexpRule("Sähköposti", REGEX_NON_EMPTY);
        Element emailP1 = Text("emailP1").labelKey("form.valmis.sinulleonlahetettyvahvistussahkopostiisi").formParams(formParameters).build();
        emailP1.addChild(new Answer("Sähköposti"));
        emailRule.addChild(emailP1);

        elements.add(emailRule);

        elements.add(Text("valmisP1").labelKey("form.lomake.valmis.p1").formParams(formParameters).build());
        elements.add(Text("valmisP2").labelKey("form.lomake.valmis.p2").formParams(formParameters).build());
        elements.add(Text("valmisP3").labelKey("form.lomake.valmis.p3").formParams(formParameters).build());

        elements.add(new Print("printLink", formParameters.getI18nText("form.valmis.button.tulosta")));

        Element attachments = new ApplicationAttachments("applicationAttachments");
        elements.add(attachments);

        elements.addAll(createAdditionalInformationElements(formParameters));

        Element muutoksenTekeminen = TitledGroup("muutoksenTekeminen").formParams(formParameters).build();

        for (int i = 0; i < paragraphs.length; i++) {
            muutoksenTekeminen.addChild(Text(ElementUtil.randomId()).formParams(formParameters).labelKey(paragraphs[i]).build());
        }
        elements.add(muutoksenTekeminen);

        elements.add(Text("palaute").labelKey("form.valmis.palaute").formParams(formParameters).build());

        elements.add(new Link("backLink", createI18NAsIs("https://opintopolku.fi"), formParameters.getI18nText(
          "form.valmis.takaisin.opintopolkuun.linkki")));

        return elements;
    }

    public static List<Element> createAdditionalInformationElements(FormParameters formParameters) {

        Element athleteRule = Rule(atLeastOneVariableEqualsToValue(ElementUtil.KYLLA,
                "preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference1_urheilijalinjan_lisakysymys",
                "preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference2_urheilijalinjan_lisakysymys",
                "preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference3_urheilijalinjan_lisakysymys",
                "preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference4_urheilijalinjan_lisakysymys",
                "preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference5_urheilijalinjan_lisakysymys")).build();
        Element athleteGroup = TitledGroup("atheleteGroup").formParams(formParameters).build();

        athleteGroup.addChild(Text("athleteP1").labelKey("form.valmis.haetturheilijana").formParams(formParameters).build());
        Link athleteLink = new Link("athleteLink", formParameters.getI18nText("form.valmis.haetturheilijana.linkki.url"),
                formParameters.getI18nText("form.valmis.haetturheilijana.linkki.text"));
        athleteLink.addAttribute("target", "_blank");
        athleteGroup.addChild(athleteLink);
        athleteRule.addChild(athleteGroup);

        //Hait musiikki-, tanssi- tai liikunta-alan koulutukseen.
        Expr isMusiikki = atLeastOneVariableEqualsToValue(EDUCATION_CODE_MUSIIKKI,
                String.format(EDUCATION_CODE_KEY, 1),
                String.format(EDUCATION_CODE_KEY, 2),
                String.format(EDUCATION_CODE_KEY, 3),
                String.format(EDUCATION_CODE_KEY, 4),
                String.format(EDUCATION_CODE_KEY, 5));
        Expr isTanssi = atLeastOneVariableEqualsToValue(EDUCATION_CODE_TANSSI,
                String.format(EDUCATION_CODE_KEY, 1),
                String.format(EDUCATION_CODE_KEY, 2),
                String.format(EDUCATION_CODE_KEY, 3),
                String.format(EDUCATION_CODE_KEY, 4),
                String.format(EDUCATION_CODE_KEY, 5));
        Expr isLiiKunta = atLeastOneVariableEqualsToValue(EDUCATION_CODE_LIIKUNTA,
                String.format(EDUCATION_CODE_KEY, 1),
                String.format(EDUCATION_CODE_KEY, 2),
                String.format(EDUCATION_CODE_KEY, 3),
                String.format(EDUCATION_CODE_KEY, 4),
                String.format(EDUCATION_CODE_KEY, 5));

        Element musiikkiTanssiLiikuntaRule = Rule(ExprUtil.any(ImmutableList.of(isMusiikki, isTanssi, isLiiKunta))).build();
        musiikkiTanssiLiikuntaRule.addChild(TitledGroup("musiikkitanssiliikunta.ryhma").formParams(formParameters).build()
                .addChild(Text(randomId()).labelKey("musiikkitanssiliikunta").formParams(formParameters).build()));
        return Lists.newArrayList(athleteRule, musiikkiTanssiLiikuntaRule);
    }

    public static List<Element> create(FormParameters formParameters) {
        if (formParameters.isLisahaku()) {
            return ValmisPhase.create(formParameters, "form.valmis.muutoksentekeminen.p1");
        } else {
            return ValmisPhase.create(formParameters,
                    "form.valmis.muutoksentekeminen.p1",
                    "form.valmis.muutoksentekeminen.p2",
                    "form.valmis.muutoksentekeminen.p3");
        }
    }
}
