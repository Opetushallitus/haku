package fi.vm.sade.oppija.lomakkeenhallinta.service.tarjonta;

import java.util.Map;

public interface TarjontaService {
    Map<String, Map<String, String>> getApplicationSystemOidsAndNames();
}
