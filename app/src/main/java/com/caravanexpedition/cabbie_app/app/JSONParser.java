package com.caravanexpedition.cabbie_app.app;

/**
 * Created by Pranav Sarraf on 08-07-2014.
 */

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";

    //constructor
    public JSONParser() {

    }


    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            if(httpPost!=null) {
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }else
                Log.d("JsonParser","HtttpResponse is empty!!");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + '\n');
                }
                is.close();
                json = sb.toString();
                //TODO: add log comannd
            } else Log.v("JSONParser", "InputStream is Empty!!");
        }
        catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jobj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("Retrieved String to JSON",json);
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            Log.e("Json String", json);
        }

        // return JSON String
        return jobj;

    }
}