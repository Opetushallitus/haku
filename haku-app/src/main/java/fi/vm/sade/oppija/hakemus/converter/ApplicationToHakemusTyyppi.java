package fi.vm.sade.oppija.hakemus.converter;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.util.OppijaConstants;
import fi.vm.sade.service.hakemus.schema.AvainArvoTyyppi;
import fi.vm.sade.service.hakemus.schema.HakemusTyyppi;
import fi.vm.sade.service.hakemus.schema.HakukohdeTyyppi;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */

public class ApplicationToHakemusTyyppi implements Converter<Application, HakemusTyyppi> {

    @Override
    public HakemusTyyppi convert(Application application) {
        HakemusTyyppi hakemusTyyppi = new HakemusTyyppi();
        hakemusTyyppi.setHakemusOid(application.getOid());
        hakemusTyyppi.setHakijaOid(application.getPersonOid());
        hakemusTyyppi.getAvainArvo().addAll(getKeyValues(application.getVastauksetMerged()));
        hakemusTyyppi.getAvainArvo().addAll(getKeyValues(application.getAdditionalInfo()));
        hakemusTyyppi.getHakutoive().addAll(getPreferences(application.getVastauksetMerged()));

        return hakemusTyyppi;
    }

    private Collection<AvainArvoTyyppi> getKeyValues(Map<String, String> keyValues) {
        return Collections2.transform(keyValues.entrySet(), new Function<Map.Entry<String, String>, AvainArvoTyyppi>() {
            @Override
            public AvainArvoTyyppi apply(Map.Entry<String, String> entry) {
                AvainArvoTyyppi avainArvo = new AvainArvoTyyppi();
                avainArvo.setAvain(entry.getKey());
                avainArvo.setArvo(entry.getValue());
                return avainArvo;
            }
        });
    }

    private List<HakukohdeTyyppi> getPreferences(Map<String, String> keyValues) {
        List<HakukohdeTyyppi> preferences = new ArrayList<HakukohdeTyyppi>();
        int i = 1;

        while(true) {
            String key = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (keyValues.containsKey(key)) {
                String value = keyValues.get(key);
                if (value != null && !value.isEmpty()) {
                    HakukohdeTyyppi preference = new HakukohdeTyyppi();
                    preference.setHakukohdeOid(value);
                    preference.setPrioriteetti(i);
                    preferences.add(preference);
                    ++i;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return preferences;
    }
}
