package family.kuziki.yaBR;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJSON  {

    HttpURLConnection urlConnection;
    URL url;

    public  GetJSON(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public JSONObject getJson() {

        StringBuilder result = new StringBuilder();

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }


        try {
            return new JSONObject(String.valueOf(result));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}