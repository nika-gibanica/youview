package com.example.youview.utils;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;

public class UploadDeviceInfo extends AsyncTask<String, String, String> {
    // url to create new product
    private static String url_upload_device = "https://youview.com.hr/deviceLog.php";

    /**
     * Before starting background thread Show Progress Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    /**
     * Creating product
     * */
    @Override
    public String doInBackground(String... args) {
        String id = args[0];
        String android_version = args[1];
        String api = args[2];
        String screen_size = args[3];
        String youtube_version = args[4];
        String device = args[5];
        String model =args [6];
        String app_version = "1.0";

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("android_version", android_version));
        params.add(new BasicNameValuePair("api", api));
        params.add(new BasicNameValuePair("screen_size", screen_size));
        params.add(new BasicNameValuePair("youtube_version", youtube_version));
        params.add(new BasicNameValuePair("device", device));
        params.add(new BasicNameValuePair("model", model));
        params.add(new BasicNameValuePair("app_version", app_version));

        JSONParser jparser = new JSONParser();
        try {
            String response = jparser.makeHttpRequest(url_upload_device, "POST", params);
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
