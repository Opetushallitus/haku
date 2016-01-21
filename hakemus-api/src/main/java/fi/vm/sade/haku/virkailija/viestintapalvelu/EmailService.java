package fi.vm.sade.haku.virkailija.viestintapalvelu;

import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationByEmailDTO;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;

import java.io.IOException;

/**
 * The interface of e-mail service which one sends application by e-mail
 * 
 * @author vehei1
 *
 */
public interface EmailService {
	/**
	 * Sends generated PDF of application by e-mail to applicant's e-mail address
	 * 
	 * @param applicationByEmail The required application data for e-mail sending
	 * @return The ID of e-mail
	 * @throws IOException
	 */
	public String sendApplicationByEmail(ApplicationByEmailDTO applicationByEmail) throws IOException;

	public String sendEmail(EmailData emailData);
}
