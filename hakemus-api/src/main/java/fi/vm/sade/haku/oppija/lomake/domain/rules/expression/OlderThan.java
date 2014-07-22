package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.rules.RegexRule.getDateOfBirth;

public class OlderThan extends Expr {

    public OlderThan(final Expr left) {
        super(left, null, null);
        Preconditions.checkNotNull(left);
    }

    @Override
    public boolean evaluate(final Map<String, String> context) {
        String dateOfBirth = getDateOfBirth(context);
        if (dateOfBirth == null) {
            return false;
        } else {
            DateTime dateTime = DateTime.parse(dateOfBirth, DateTimeFormat.forPattern("dd.MM.yyyy"));
            return Integer.parseInt(getLeft().getValue(context)) <= Years.yearsBetween(new LocalDate(dateTime), new LocalDate()).getYears();
        }

    }
}
