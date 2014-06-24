package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Profile(value = {"dev", "it"})
public class EmailServiceMockImpl implements EmailService {

    @Autowired
    public EmailServiceMockImpl(ApplicationService applicationService,
                                AuthenticationService authenticationService, PDFService pdfService) {
    }

	@Override
	public String sendApplicationByEmail(String applicationOID) throws IOException {
		return "1";
	}

}
