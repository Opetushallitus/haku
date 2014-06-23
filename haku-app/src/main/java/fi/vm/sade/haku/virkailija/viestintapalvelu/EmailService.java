package fi.vm.sade.haku.virkailija.viestintapalvelu;

import java.io.IOException;

public interface EmailService {
	public String sendApplicationByEmail(String applicationOID) throws IOException;
}
