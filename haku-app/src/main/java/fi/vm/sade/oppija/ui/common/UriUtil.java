package fi.vm.sade.oppija.ui.common;

import com.google.common.base.Joiner;

import java.net.URI;
import java.net.URISyntaxException;

public final class UriUtil {

    private UriUtil() {
    }

    public static final URI pathSegmentsToUri(String... paths) throws URISyntaxException {
        Joiner joiner = Joiner.on("/").skipNulls();
        return new URI(joiner.join(paths));
    }
}
