package fi.vm.sade.haku.oppija.lomake.domain;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ApplicationSystemCreator {
    public static ApplicationSystem createActiveApplicationSystem(final String id, Form form) {
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, -1);
        Date start = new Date(instance.getTimeInMillis());
        instance.roll(Calendar.YEAR, 2);
        Date end = new Date(instance.getTimeInMillis());
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(start, end));
        return new ApplicationSystemBuilder().addId(id).addForm(form)
            .addName(ElementUtil.createI18NAsIs("test application period"))
            .addApplicationPeriods(applicationPeriods)
            .addHakukausiUri(OppijaConstants.HAKUKAUSI_SYKSY)
            .addApplicationSystemType(OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU)
            .addHakutapa(OppijaConstants.HAKUTAPA_YHTEISHAKU)
            .get();
    }
}
