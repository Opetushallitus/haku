package fi.vm.sade.oppija.haku.tools;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class FileHandling {


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
        return readFile(getStream(file));
    }

    public String readFile(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String filename, String contentAsString) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(filename));
            fileWriter.write(contentAsString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fileWriter);
        }
    }
}
