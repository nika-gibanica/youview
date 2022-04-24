package com.example.youview.utils;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class UploadEvent extends AsyncTask<String, String, String> {

    private static String url_upload_event = "https://youview.com.hr/eventLog.php";

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
        String android_id = args[0];
        String event = args[1];
        String connection = args[2];
        String video_id = args[3];

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("android_id", android_id));
        params.add(new BasicNameValuePair("event", event));
        params.add(new BasicNameValuePair("connection", connection));
        params.add(new BasicNameValuePair("video_id", video_id));

        JSONParser jparser = new JSONParser();
        try {
            String response = jparser.makeHttpRequest(url_upload_event, "POST", params);
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
