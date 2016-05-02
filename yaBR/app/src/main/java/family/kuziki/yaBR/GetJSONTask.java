package family.kuziki.yaBR;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Admin on 002 02.05.16.
 */
public class GetJSONTask extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        String target = args[0];

        try {
            URL url = new URL(target);
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


        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {

        Log.d("JSON", result.toString());

    }

}