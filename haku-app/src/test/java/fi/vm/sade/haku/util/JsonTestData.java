package fi.vm.sade.haku.util;

import static java.lang.ClassLoader.getSystemResourceAsStream;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.mongodb.util.JSON;

public class JsonTestData {
    public static <A> A readTestData(final String filename) {
        String content = null;
        try {
            content = IOUtils.toString(getSystemResourceAsStream(filename), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (A) JSON.parse(content);
    }
}
