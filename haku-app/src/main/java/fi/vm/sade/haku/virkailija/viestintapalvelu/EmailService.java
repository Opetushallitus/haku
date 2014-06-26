package fi.vm.sade.haku.virkailija.viestintapalvelu;

import java.io.IOException;

import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationByEmailDTO;

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
}
