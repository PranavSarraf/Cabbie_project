package com.caravanexpedition.cabbie_app.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainLoginActivity extends Activity {

    ProgressDialog pDialog;
    EditText mail ;
    EditText pass;
    Button Log;
    TextView message;
    JSONParser jsonParser = new JSONParser();
    JSONObject mObject;
    String username,password;
    private static String url_Login = "http://192.168.56.1/Android/Login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    CheckBox Remember ;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        mail = (EditText) findViewById( R.id.EditEmail);
        pass = (EditText) findViewById(R.id.EditTextPassword);
        Log= (Button) findViewById(R.id.btnLogin);
        message = (TextView)findViewById(R.id.EditTextMessage);
        Remember = (CheckBox) findViewById(R.id.checkBox);
        ScrollView scroll = (ScrollView) findViewById(R.id.ScrollView02);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            mail.setText(loginPreferences.getString("username", ""));
            pass.setText(loginPreferences.getString("password", ""));
            Remember.setChecked(true);
        }
        scroll.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeRight() {
                Intent intent = new Intent(MainLoginActivity.this,FormActivity.class);
                startActivity(intent);
            }

            public void onSwipeLeft() {
                Intent intent = new Intent(MainLoginActivity.this,AdminLoginActivity.class);
                startActivity(intent);
            }


            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


        TextView link = (TextView) findViewById(R.id.link_to_register);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FormActivity.class);
                startActivity(intent);
            }
        });
        TextView link1 = (TextView) findViewById(R.id.link_to_admin);
        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AdminLoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public void doSomething(View v){
        if(v.getId()==R.id.btnLogin){
            username = mail.getText().toString();
            password = pass.getText().toString();

            if (Remember.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("username", username);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.commit();
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
            }
            if(isNetworkAvailable())
            {
                new EmployeeLogin().execute();


            }
            else {
                Toast.makeText(getApplicationContext(), "Connection not available: ", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void onLoginSuccessful(JSONObject object){
        try {
            if(mObject!=null) {
                int success = mObject.getInt(TAG_SUCCESS);
                String text = mObject.getString(TAG_MESSAGE);

                if (success == 1) {
                    android.util.Log.d("yayayayayya", "Connection Sucessful: " + success);
                    String mail = mObject.getString("Email");



                    message.setText(text);
                    EmailValue emailValue = new EmailValue(mail,"email");
                    Intent intent = new Intent(this,MainScreenActivity.class);

                   // intent.putExtra(EXTRA_MESSAGE,mail);
                    startActivity(intent);
                    //Intent intent = new Intent(this,MainScreenActivity.class);
                    //startActivity(intent);




                } else {
                    // failed to create product
                    android.util.Log.d("yayayayayyaya", "Connection  Not Sucessful: " + success);
                    message.setText(text);
                }
            }else{
                android.util.Log.e("JSONObject Returned is null!!!", "check network connection");
            }
        } catch (JSONException e) {
            android.util.Log.d("yayayayayyaya", "JSonException: " + e);
        }





    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if(networkInfo!=null&&networkInfo.isConnected()) {
            isAvailable= true;
        }
        return isAvailable;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class EmployeeLogin extends AsyncTask<String, Void, JSONObject> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainLoginActivity.this);
            pDialog.setMessage("Logging IN....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected JSONObject doInBackground(String... args) {


            String Email = mail.getText().toString();

            String Password = pass.getText().toString();





            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("Email", Email));

            params.add(new BasicNameValuePair("Password", Password));


            // getting JSON Object
            // Note that create product url accepts POST method

            return (jsonParser.makeHttpRequest(url_Login,"POST",params)) ;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(JSONObject result) {
            // dismiss the dialog once done
            pDialog.dismiss();
            mObject = result;
            onLoginSuccessful(mObject);

        }

    }

}
