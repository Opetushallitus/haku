package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import fi.vm.sade.haku.virkailija.viestintapalvelu.ApplicationPrintViewService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value = {"dev", "it"})
public class ApplicationPrintViewServiceMockImpl implements ApplicationPrintViewService {

	@Override
	public String getApplicationPrintView(String applicationOID) {
        return "<html><head><title>Testi</title></head><body>testibody</body></html>";
	}
}
