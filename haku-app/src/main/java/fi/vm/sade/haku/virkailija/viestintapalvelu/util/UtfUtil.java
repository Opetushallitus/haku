package fi.vm.sade.haku.virkailija.viestintapalvelu.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.springframework.stereotype.Component;

@Component
public class UtfUtil {

	public static String toUTF8(String s) throws UnsupportedEncodingException {
		String defaultCharset = Charset.defaultCharset().name();
		
		Charset fromCharset = Charset.forName(defaultCharset);
		Charset toCharset = Charset.forName("UTF-8");
		
		String utf8 = new String(s.getBytes(fromCharset), toCharset);
		return utf8;
	}
}
