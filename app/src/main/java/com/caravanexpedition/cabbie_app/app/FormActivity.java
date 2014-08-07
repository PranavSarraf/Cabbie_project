package com.caravanexpedition.cabbie_app.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FormActivity extends ActionBarActivity {
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    GPSTracker gps;
    // url to create new product
    private static String url_create_employee = "http://192.168.56.1/Android/create_employee.php";
    double latitude;
    double longitude;
    String sb =null;
    JSONObject mObject = null;
     double lat ;
    double lon;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG = FormActivity.class.getSimpleName();
    EditText name;
    EditText id;
    EditText Phone;
    EditText email;
    EditText E_address;
    EditText password;
    Spinner spinner;
    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        name = (EditText) findViewById(R.id.EditName);
        id = (EditText) findViewById(R.id.EditTextE_id);
        Phone = (EditText) findViewById(R.id.EditPhone);
        email = (EditText) findViewById(R.id.EditEmail);
        E_address = (EditText) findViewById(R.id.EditTextLocation);
        password = (EditText) findViewById(R.id.EditTextPassword);
        spinner = (Spinner) findViewById(R.id.SpinnerFeedbackType);
        button = (Button) findViewById(R.id.buttonRegister);
       ScrollView scrollView = (ScrollView) findViewById(R.id.ScrollView01);
       scrollView.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeRight() {
                Intent intent = new Intent(FormActivity.this,MainLoginActivity.class);
                startActivity(intent);
            }

            public void onSwipeLeft() {
                Intent intent = new Intent(FormActivity.this,MainLoginActivity.class);
                startActivity(intent);
            }


            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

    }

    public void doSomethingElse(View v) {
        if (v.getId() == R.id.buttonRegister) {
            FormEditText[] allFields    = {(FormEditText) findViewById(R.id.EditName), (FormEditText) findViewById(R.id.EditTextE_id), (FormEditText)findViewById(R.id.EditPhone), (FormEditText)findViewById(R.id.EditEmail)  };


            boolean allValid = true;
            for (FormEditText field: allFields) {
                allValid = field.testValidity() && allValid;
            }

            if (allValid) {
                if(isNetworkAvailable()) {
                    new AddEmployee().execute();
                    //  Toast.makeText(getApplicationContext(),"registration sucessful: ",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Connection not available: ",Toast.LENGTH_LONG).show();
                }

            } else {
                // EditText are going to appear with an exclamation mark and an explicative message.
            }

        }
    }    @Override
        public void onResume(){
            super.onResume();
        gps = new GPSTracker(FormActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

              lat = gps.getLatitude();
              lon = gps.getLongitude();
            Toast.makeText(getApplicationContext(),"Latitude: "+ lat +  "\n" + "Longitude"+lon,Toast.LENGTH_LONG).show();

            //E_address.setText("Latitude: "+ latitude +  "\n" + "Longitude"+longitude);
            // \n is for new line

            final Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            if(geocoder.isPresent()) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                            for(int i = 0;i<3;i++) {
                                if(addresses.get(0).getAddressLine(i)!=null) {
                                    sb += addresses.get(0).getAddressLine(i);
                                }
                            }


                            E_address.post(new Runnable() {
                                @Override
                                public void run() {
                                    E_address.setText(" ");
                                    E_address.setText(sb.toString());
                                }
                            });
                        } catch (IOException e){
                            Log.e("yayayayaya ","Execption Occued!: "+ e);
                        }
                    }
                }).start();

            }



        }else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
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
      public  void onPause()
      { // stop using gps resources when activity loses focus
          super.onPause();
          if(gps!= null){
          gps.stopUsingGPS();}

      }

    public void onRegistrationSuccessful(JSONObject mObject){
        // check for success tag
        try {
              if(mObject!=null) {
                  int success = mObject.getInt(TAG_SUCCESS);

                  if (success == 1) {
                      Log.d("yayayayayya", "Connection Sucessful: " + success);

                      AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

// Setting Dialog Title
                      alertDialog.setTitle("Registration Successful!!");

// Setting Dialog Message
                      alertDialog.setMessage(" Registration  Successful!! \n. Do you want to go to Login menu?");

// On pressing Settings button
                      alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog,int which) {
                              Intent intent = new Intent(getApplicationContext()
                                      ,MainLoginActivity.class);
                              startActivity(intent);}

                      });
// on pressing cancel button
                      alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int which) {
                              dialog.cancel();}

                      });
// Showing Alert Message
                      alertDialog.show();

                  } else {
                      // failed to create product
                      Log.d("yayayayayyaya", "Connection  Not Sucessful: " + success);
                  }
              }else{
                  Log.e("JSONObject Returned is null!!!", "check network connection");
              }
        } catch (JSONException e) {
            Log.d("yayayayayyaya", "JSonException: " + e);
        }


    }
    public boolean getLatitudeAndLongitudeFromGoogleMapForAddress(String searchedAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {

            address = coder.getFromLocationName(searchedAddress,5);
            if (address == null) {
                Log.d(TAG, "############Address not correct #########");
            }
            Address location = address.get(0);
            latitude = location.getLatitude();
            longitude = location.getLongitude();


            return true;

        }catch(Exception e){
            Log.d(TAG, "MY_ERROR : ############Address Not Found");
            return false;
        }
    }

    private class AddEmployee extends AsyncTask<String, String, JSONObject> {
            /**
             * Before starting background thread Show Progress Dialog
             * */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(FormActivity.this);
                pDialog.setMessage("Creating Employee Record....");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            protected JSONObject doInBackground(String... args) {


                String Name = name.getText().toString();
                String E_id = id.getText().toString();
                String Designation = spinner.getSelectedItem().toString();
                String Location = E_address.getText().toString();
                String Password = password.getText().toString();
                String Email = email.getText().toString();
                String Phone_number = Phone.getText().toString();

                  if(getLatitudeAndLongitudeFromGoogleMapForAddress(Location))
                  {
                      latitude = lat;
                      longitude = lon;
                  }


                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("Name", Name));
                params.add(new BasicNameValuePair("E_id", E_id));
                params.add(new BasicNameValuePair("Phone_number", Phone_number));
                params.add(new BasicNameValuePair("Email", Email));
                params.add(new BasicNameValuePair("Designation", Designation));
                params.add(new BasicNameValuePair("Location", Location));
                params.add(new BasicNameValuePair("Password", Password));
                params.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
                params.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));

                // getting JSON Object
                // Note that create product url accepts POST method
                JSONObject jsonResponse = jsonParser.makeHttpRequest(url_create_employee,"POST",params);
                 return jsonResponse;
            }

            /**
             * After completing background task Dismiss the progress dialog
             * **/
            protected void onPostExecute(JSONObject result) {
                // dismiss the dialog once done
                pDialog.dismiss();
                mObject = result;
                onRegistrationSuccessful(mObject);

            }

        }
    }



