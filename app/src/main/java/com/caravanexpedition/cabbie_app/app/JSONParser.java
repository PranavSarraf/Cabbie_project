package com.caravanexpedition.cabbie_app.app;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {
    }

public JSONObject getUrldata(String url){
    int responseCode =-1;
    JSONObject jsonObject = null ;
    try
    {  URL blogfeed = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) blogfeed.openConnection();
        connection.connect();
        responseCode = connection.getResponseCode();
        Log.i("JsonParser","Response Code: " + responseCode);
        InputStream input = connection.getInputStream();
        Reader reader = new InputStreamReader(input);
        int content_length = connection.getContentLength();
        char[] content = new char[content_length];
        reader.read(content);
        String employeedata = new String(content);
        jsonObject = new JSONObject(employeedata);




    }
    catch(MalformedURLException e)
    {
        Log.e("JsonParser", "Exception caught: " + e);
    }
    catch(IOException e){
        Log.e("JsonParser", "Exception caught: " + e);
    }
    catch(Exception e){
        Log.e("JsonParser", "Exception caught: " + e);
    }
    return jsonObject;
}

    // function get json from url
// by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

// Making HTTP request
        try {

// check for request method
            if(method == "POST"){
// request method is POST
// defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);



                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }else if(method == "GET"){
// request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                json = sb.toString();
            }
            Log.d("JSONdata: ",json);
            if(json!=null) {
                jObj = new JSONObject(json);
            }else{
               // TODO

                Log.e("Json Data not returned",json);
            }
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JsonException: ","ERROr: " + e);
        }
        try {
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jObj;}

}

