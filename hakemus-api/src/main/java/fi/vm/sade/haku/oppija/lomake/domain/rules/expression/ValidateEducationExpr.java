package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        if(context.get(rowId + "-Koulutus-requiredBaseEducations") == null) {
            return false;
        }

        ImmutableSet<String> baseEducationRequirements =  new ImmutableSet.Builder<String>()
                .add(context.get(rowId + "-Koulutus-requiredBaseEducations").split(","))
                .build();

        return !HakumaksuService.validateBaseEdsAgainstAoRequirement(Types.MergedAnswers.of(context), baseEducationRequirements);
    }

    @Override
    public List<Expr> children() {
        return Arrays.asList();
    }
}
