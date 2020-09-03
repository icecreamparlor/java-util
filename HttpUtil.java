import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String get(String urlStr) throws IOException {

        String output = "";
        String readline = "";

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setUseCaches( false );

        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED
                && conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

            // Connection is Not Successful
            logger.error("HttpURLConnection Not Successful : GET " + conn.getResponseCode());
            logger.error(conn.getContent().toString());

            throw new IOException("HttpURLConnection Not Successful : GET " + conn.getResponseCode());

        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        while((readline = br.readLine()) != null) {
            output += readline;
        }

        if(br != null)      br.close();
        if(conn != null)    conn.disconnect();

        return output;

    }

    public static String post(String urlStr, String param, Map<String, String> properties) throws IOException {


        String output = "";
        String readline = "";

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        byte[] paramData = param.getBytes("UTF-8");
        int paramDataLength = paramData.length;

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(paramDataLength));
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setUseCaches( false );

        if(properties != null) {
            for(String key : properties.keySet()) {
                conn.setRequestProperty(key, properties.get(key));
            }
        }

        try( DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(paramData);
        }

        BufferedReader br = null;

        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED
                && conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

            // br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            // Connection is Not Successful
            logger.error("HttpURLConnection Not Successful : POST " + conn.getResponseCode());
            logger.error(conn.getContent().toString());

            throw new IOException("HttpURLConnection Not Successful : POST " + conn.getResponseCode());

        } else {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }

        while((readline = br.readLine()) != null) {
            output += readline;
        }

        if(br != null)      br.close();
        if(conn != null)    conn.disconnect();

        return output;

    }

    public static String appendParam(String uri, String key, String value) throws URISyntaxException {
        URI oldUri = new URI(uri);

        String newQuery = oldUri.getQuery();

        if (newQuery == null) {
            newQuery = key + "=" + value;
        } else {
            newQuery += "&" + key + "=" + value;
        }

        URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery, oldUri.getFragment());

        return newUri.toString();
    }

}