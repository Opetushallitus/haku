package fi.vm.sade.oppija.haku.domain;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 10/18/129:48 AM}
 * @since 1.1
 */
public class PreferenceHelperTest {


    private static final String JOKUPAIKKA = "Jokupaikka";

    @Test
    public void testPreference() throws Exception {

        final HashMap<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", JOKUPAIKKA);
        final PreferenceHelper preferenpceHelper = new PreferenceHelper(values);
        assertEquals(JOKUPAIKKA, preferenpceHelper.getOpetuspisteet().get(0).getOpetusPiste());
    }

    @Test
    public void testPreferences() throws Exception {

        final HashMap<String, String> values = new HashMap<String, String>();
        values.put("preference2-Opetuspiste", "2");
        values.put("preference3-Opetuspiste", "3");
        values.put("preference1-Opetuspiste", "1");
        final PreferenceHelper preferenpceHelper = new PreferenceHelper(values);
        final List<Preference> opetuspisteet = preferenpceHelper.getOpetuspisteet();
        assertEquals(3, opetuspisteet.size());
        final Preference[] strings = opetuspisteet.toArray(new Preference[opetuspisteet.size()]);
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i].getOpetusPiste();
            assertEquals((i + 1) + "", s);
        }
    }
}
