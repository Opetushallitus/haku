package fi.vm.sade.oppija.haku.domain.rules;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jukka
 * @version 10/2/122:43 PM}
 * @since 1.1
 */
public class RegexRule {

    private final static Logger LOGGER = LoggerFactory.getLogger(RegexRule.class);

    public static boolean evaluate(String value, String expression) {
        final Pattern compile = Pattern.compile(expression);
        Matcher matcher = compile.matcher(value);
        LOGGER.debug("Using regexp: {} for value: {}, matches: {}", new Object[]{expression, value, matcher.matches()});
        return matcher.matches();
    }
}
