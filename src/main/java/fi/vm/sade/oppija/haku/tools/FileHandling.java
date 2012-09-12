package fi.vm.sade.oppija.haku.tools;

import java.io.*;

public class FileHandling {


    public StringBuilder readStreamFromFile(String arg) {
        final InputStream streamFromFile = getStreamFromFile(arg);
        return readFile(streamFromFile);
    }

    public InputStream getStreamFromFile(String arg) {
        try {
            return new FileInputStream(new File(arg));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public StringBuilder readFile(InputStream inputStream) {
        StringBuilder buffer = new StringBuilder();

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String newLine = reader.readLine();

            while (newLine != null) {

                buffer.append(newLine);
                newLine = reader.readLine();
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(reader);
        }
        return buffer;
    }

    public void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void writeFile(String filename, String contentAsString) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(filename));
            fileWriter.write(contentAsString);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            close(fileWriter);
        }
    }
}