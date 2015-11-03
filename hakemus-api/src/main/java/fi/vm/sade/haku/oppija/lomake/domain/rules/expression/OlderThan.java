package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.rules.RegexRule.getDateOfBirth;

public class OlderThan extends Expr {
    private final Expr left;
    private boolean demoMode;

    public OlderThan(final Expr left, boolean demoMode) {
        this.left = left;
        this.demoMode = demoMode;
        Preconditions.checkNotNull(left);
    }

    public OlderThan(final Expr left) {
        this.left = left;
        Preconditions.checkNotNull(left);
    }

    @Override
    public List<Expr> children() {
        return Arrays.asList(left);
    }


    @Override
    public boolean evaluate(final Map<String, String> context) {

        String dateOfBirth = getDateOfBirth(context);
        if(demoMode) {
            return true;
        }

        if (dateOfBirth == null) {
            return false;
        } else {
            DateTime dateTime = DateTime.parse(dateOfBirth, DateTimeFormat.forPattern("dd.MM.yyyy"));
            return Integer.parseInt(left.getValue(context)) <= Years.yearsBetween(new LocalDate(dateTime), new LocalDate()).getYears();
        }

    }
}
