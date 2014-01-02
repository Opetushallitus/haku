package fi.vm.sade.haku.oppija.lomake.util;

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
}
