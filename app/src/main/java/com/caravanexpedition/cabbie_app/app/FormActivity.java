package com.caravanexpedition.cabbie_app.app;

import android.util.Log;



        import java.util.ArrayList;
        import java.util.List;

        import org.apache.http.NameValuePair;
        import org.apache.http.message.BasicNameValuePair;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.Toast;

public class FormActivity extends ActionBarActivity {
    private ProgressDialog pDialog ;
    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_create_product = "http://localhost/Android/create_product.php";


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG = FormActivity.class.getSimpleName();
    EditText name ;
    EditText id ;
    EditText Phone ;
    EditText email ;
    EditText address ;
    EditText password ;
    Spinner spinner;
    Button button ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        name = (EditText) findViewById(R.id.EditTextName);
        id = (EditText) findViewById(R.id.EditTextE_id);
        Phone = (EditText) findViewById(R.id.EditTextPhone);
        email = (EditText) findViewById(R.id.EditTextEmail);
        address = (EditText) findViewById(R.id.EditTextLocation);
        password = (EditText) findViewById(R.id.EditTextPassword);
        spinner = (Spinner) findViewById(R.id.SpinnerFeedbackType);
        button = (Button) findViewById(R.id.buttonRegister);
    }

    public void doSomethingElse(View v){
        if(v.getId()==R.id.buttonRegister)
        {
            new AddEmployee().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form, menu);
        return true;
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

    private class AddEmployee extends AsyncTask<String,String,String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String Name = name.getText().toString();
            String E_id = id.getText().toString();
            String Designation = spinner.getSelectedItem().toString();
            String Location = address.getText().toString();
            String Password = password.getText().toString();
            String Email = email.getText().toString();
            String Phone_number = Phone.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Name", Name));
            params.add(new BasicNameValuePair("E_id", E_id));
            params.add(new BasicNameValuePair("Designation", Designation));
            params.add(new BasicNameValuePair("Email", Email));
            params.add(new BasicNameValuePair("Phone_number", Phone_number));
            params.add(new BasicNameValuePair("Location", Location));
            params.add(new BasicNameValuePair("Password", Password));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.getJSONFromUrl(url_create_product,
                    params);


            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    Log.d(TAG, "Connection Sucessful: " + success);
                    // closing this screen

                } else {
                    // failed to create product
                    Log.d(TAG, "Connection  Not Sucessful: " +success);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}


