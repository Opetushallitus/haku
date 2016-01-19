package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import java.io.IOException;

import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailDataBuilder;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationByEmailDTO;

@Service
@Profile(value = {"dev", "it"})
public class EmailServiceMockImpl implements EmailService {

    public EmailServiceMockImpl() {
    }

	@Override
	public String sendApplicationByEmail(ApplicationByEmailDTO applicationByEmail) throws IOException {
		return "1";
	}

	@Override
	public String sendEmail(EmailData emailData) {
		return "1";
	}
}
