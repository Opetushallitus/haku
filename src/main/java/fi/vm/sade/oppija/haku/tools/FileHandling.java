package fi.vm.sade.oppija.haku.tools;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileHandling {

    public static final Logger LOG = LoggerFactory.getLogger(FileHandling.class);


    public String readStreamFromFile(String arg) {
        final InputStream streamFromFile = getStreamFromFile(arg);
        return readFile(streamFromFile);
    }

    public InputStream getStreamFromFile(String arg) {
        return getStream(new File(arg));
    }

    public InputStream getStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getStringFromFile(File file) {
        InputStream stream = getStream(file);
        String content = readFile(stream);
        IOUtils.closeQuietly(stream);
        return content;
    }

    public String readFile(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            LOG.error("Error reading stream", e);
        }
        return null;
    }

    public void writeFile(String filename, String contentAsString) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(filename));
            fileWriter.write(contentAsString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }
}
