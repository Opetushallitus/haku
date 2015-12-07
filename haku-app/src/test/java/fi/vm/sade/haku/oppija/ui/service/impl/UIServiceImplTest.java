package fi.vm.sade.haku.oppija.ui.service.impl;

import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.oppija.hakemus.HakumaksuTest;
import fi.vm.sade.haku.oppija.hakemus.TestApplicationData;
import org.junit.Test;

import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.Pohjakoulutus.MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA;
import static fi.vm.sade.haku.oppija.hakemus.Pohjakoulutus.SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_OLEN_SUORITTANUT_YLIOPPILASTUTKINNON_OHELLA_AMMATILLISEN_TUTKINNON_KAKSOISTUTKINTO;
import static fi.vm.sade.haku.oppija.hakemus.TestApplicationData.*;
import static org.junit.Assert.assertEquals;

public class UIServiceImplTest extends HakumaksuTest {

    private UIServiceImpl uiService = new UIServiceImpl(null, null, null, null, null, null, null, null, service, false, null);

    @Test
    public void testThatPaymentNotificationIsVisibleWhenPaymentIsRequired() {
        Map<String, String> result = uiService.paymentNotificationAnswers(TestApplicationData.getAnswers(
                ImmutableSet.of(
                        APPLICATION_OPTION_WITHOUT_PAYMENT_EDUCATION_REQUIREMENTS,
                        APPLICATION_OPTION_WITH_IGNORE_AND_PAYMENT_EDUCATION_REQUIREMENTS,
                        APPLICATION_OPTION_WITH_MULTIPLE_BASE_EDUCATION_REQUIREMENTS // Requires payment
                ),
                ImmutableSet.of(
                        MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA // Requires payment
                )
        ));

        assertEquals(2, result.size());
        assertEquals("true", result.get("preference2_payment_notification_visible"));
        assertEquals("true", result.get("preference3_payment_notification_visible"));
    }

    @Test
    public void testThatPaymentNotificationIsNotShownWhenPaymentIsNotRequired() {
        Map<String, String> result = uiService.paymentNotificationAnswers(TestApplicationData.getAnswers(
                ImmutableSet.of(
                        APPLICATION_OPTION_WITHOUT_PAYMENT_EDUCATION_REQUIREMENTS,
                        APPLICATION_OPTION_WITH_MULTIPLE_BASE_EDUCATION_REQUIREMENTS,
                        APPLICATION_OPTION_WITH_IGNORE_AND_PAYMENT_EDUCATION_REQUIREMENTS
                ),
                ImmutableSet.of(
                        SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_OLEN_SUORITTANUT_YLIOPPILASTUTKINNON_OHELLA_AMMATILLISEN_TUTKINNON_KAKSOISTUTKINTO
                )
        ));

        assertEquals(0, result.size());
    }

}
