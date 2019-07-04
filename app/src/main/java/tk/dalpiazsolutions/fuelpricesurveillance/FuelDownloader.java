package tk.dalpiazsolutions.fuelpricesurveillance;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Christoph on 08.06.2018.
 */

public class FuelDownloader extends AsyncTask<String, Void, String> {

    private URL url;
    private HttpURLConnection connection;
    private StringBuilder result = new StringBuilder();
    private String line;
    private String lineBefore;

    @Override
    protected String doInBackground(String... urls) {

        try {
            url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            if(urls.length == 1)
            {
                while((line = bufferedReader.readLine()) != null)
                {
                    //Log.i("line", line);
                    result.append(line);
                }
                return result.toString();
            }

            else {
                //while((line = bufferedReader.readLine()) != null && !line.contains("OMV - Engerwitzdorf"))
                while ((line = bufferedReader.readLine()) != null && !line.contains(urls[1]))
                {
                    //Log.i("line", line);
                    result.append(line);
                    lineBefore = line;
                }

                //Log.i("site", result.toString());
                return lineBefore;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
