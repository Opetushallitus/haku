package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.MergedAnswers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil.answersFulfillBaseEducationRequirements;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class ValidateEducationExpr extends Expr {
    private final String rowId;

    public ValidateEducationExpr(String rowId) {
        this.rowId = rowId;
    }

    @Override
    public String getValue(final Map<String, String> context) {
        return ""+this.evaluate(context);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        String applicationOptionBaseEdRequirements = context.get(rowId + "-Koulutus-requiredBaseEducations");

        if (isEmpty(applicationOptionBaseEdRequirements)) {
            return false;
        } else {
            return !answersFulfillBaseEducationRequirements(MergedAnswers.of(context),
                    ImmutableSet.copyOf(applicationOptionBaseEdRequirements.split(",")));
        }
    }


    @Override
    public List<Expr> children() {
        return Collections.emptyList();
    }
}
