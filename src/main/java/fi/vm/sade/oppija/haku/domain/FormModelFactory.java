package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.haku.converter.JsonStringToFormModelConverter;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * @author jukka
 * @version 9/14/121:44 PM}
 * @since 1.1
 */
public final class FormModelFactory {
    public static final Logger LOG = LoggerFactory.getLogger(FormModelFactory.class);

    private FormModelFactory() {
    }

    public static FormModel fromJSONString(String json) {
        return new JsonStringToFormModelConverter().convert(json);
    }

    public static FormModel fromFileName(String filename) {
        final FileHandling fileHandling = new FileHandling();
        final String content = fileHandling.readStreamFromFile(filename);
        return new JsonStringToFormModelConverter().convert(content);
    }

    public static FormModel fromFile(File file) {
        final String json = new FileHandling().getStringFromFile(file);
        return new JsonStringToFormModelConverter().convert(json);
    }

    public static FormModel fromClassPathResource(String fileName) {
        return fromClassPathResource(new ClassPathResource(fileName));
    }

    public static FormModel fromClassPathResource(ClassPathResource file) {
        String json = "";
        try {
            json = new FileHandling().readFile(file.getInputStream());
        } catch (IOException e) {
            LOG.error("", e);
        }
        return new JsonStringToFormModelConverter().convert(json);
    }
}
