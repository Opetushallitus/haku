package fi.vm.sade.haku.oppija.common.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DBObjectUtils {
    public static boolean isGZIP(byte[] binary) {
        return (binary[0] == (byte) 0x1f) && (binary[1] == (byte) 0x8b);
    }

    public static byte[] compressDBObject(BasicDBObject dbo) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        String jsonString = dbo.toString();
        gzipOut.write(jsonString.getBytes());
        gzipOut.close();

        return baos.toByteArray();
    }

    public static DBObject decompressDBObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipIn = new GZIPInputStream(bais);
        Reader decoder = new InputStreamReader(gzipIn, "UTF8");

        StringWriter w = new StringWriter();
        IOUtils.copy(decoder, w);

        DBObject dbo = (DBObject) JSON.parse(w.toString());
        gzipIn.close();
        return dbo;
    }
}
