package fi.vm.sade.haku.oppija.lomake.util;

import static org.apache.commons.lang.StringUtils.EMPTY;

public final class StringUtil {
    private StringUtil() {
    }

    public static String parseOidSuffix(final String oid) {
        if (oid != null) {
            String[] split = oid.split("\\.");
            if (split.length > 0) {
                return split[split.length - 1];
            }
        }
        return oid;
    }

    public static String safeToString(Object o) {
        return o == null ? EMPTY : o.toString();
    }

    public static String nameOrEmpty(Enum e) {
        return e == null ? EMPTY : e.name();
    }

}
