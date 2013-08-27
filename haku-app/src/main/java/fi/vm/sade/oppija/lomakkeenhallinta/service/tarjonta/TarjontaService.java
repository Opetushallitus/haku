package fi.vm.sade.oppija.lomakkeenhallinta.service.tarjonta;

import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;

import java.util.List;
import java.util.Map;

public interface TarjontaService {
    Map<String, Map<String, String>> getApplicationSystemOidsAndNames();

    List<ApplicationPeriod> getApplicationPeriods(final String asId);
}
