package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.haku.converter.JsonStringToFormModelConverter;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * @author jukka
 * @version 9/14/121:44 PM}
 * @since 1.1
 */
public class FormModelFactory {

    public static FormModel fromJSONString(String json) {
        return new JsonStringToFormModelConverter().convert(json);
    }

    public static FormModel fromFileName(String filename) {
        final FileHandling fileHandling = new FileHandling();
        final StringBuilder stringBuilder = fileHandling.readStreamFromFile(filename);
        return new JsonStringToFormModelConverter().convert(stringBuilder.toString());
    }

    public static FormModel fromFile(File file) {
        final StringBuilder json = new FileHandling().getStringFromFile(file);
        return new JsonStringToFormModelConverter().convert(json.toString());
    }

    public static FormModel fromClassPathResource(String fileName) {
        return fromClassPathResource(new ClassPathResource(fileName));
    }

    public static FormModel fromClassPathResource(ClassPathResource file) {
        StringBuilder json = new StringBuilder();
        try {
            json = new FileHandling().readFile(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonStringToFormModelConverter().convert(json.toString());
    }
}
