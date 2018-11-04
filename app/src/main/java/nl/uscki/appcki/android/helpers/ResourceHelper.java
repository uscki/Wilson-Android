package nl.uscki.appcki.android.helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

public class ResourceHelper {

    /**
     * Read an input stream for a raw resource to string
     * @param inputStream       Input stream for raw resource
     * @return                  String content of stream
     */
    public static String getRawStringResourceFromStream(InputStream inputStream) {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch(Exception e) {
            Log.e(ResourceHelper.class.getSimpleName(), e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(ResourceHelper.class.getSimpleName(), e.getMessage());
            }
        }

        return writer.toString();
    }
}
