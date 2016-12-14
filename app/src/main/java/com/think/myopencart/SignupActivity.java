package com.think.myopencart;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private EditText et_fname, et_lname, et_mail, et_telephone, et_fax, et_address1, et_address2, et_ladmark, et_city, et_postal;
    private EditText et_pass, et_confirmPass;
    private Button cancel, signup;
    private String s_lname, s_fname, s_mail, s_telephone, s_fax, s_address1, s_address2, s_landmark, s_city, s_postal, s_pass;
    private ProgressDialog dialog;
    public static String customer_id, newURL,myUrl;
    private int res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_mail = (EditText) findViewById(R.id.et_Email);
        et_telephone = (EditText) findViewById(R.id.et_tel);
        et_fax = (EditText) findViewById(R.id.et_fax);

        et_address1 = (EditText) findViewById(R.id.et_address1);
        et_address2 = (EditText) findViewById(R.id.et_Address2);
        et_ladmark = (EditText) findViewById(R.id.et_company);
        et_city = (EditText) findViewById(R.id.et_city);
        et_postal = (EditText) findViewById(R.id.et_pin);

        et_pass = (EditText) findViewById(R.id.et_pass1);
        et_confirmPass = (EditText) findViewById(R.id.et_pass2);

        cancel = (Button) findViewById(R.id.btn_cancel);
        signup = (Button) findViewById(R.id.btn_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_fname = et_fname.getText().toString();
                s_lname = et_lname.getText().toString();
                s_mail = et_mail.getText().toString();
                s_telephone = et_telephone.getText().toString();
                s_fax = et_fax.getText().toString();
                s_address1 = et_address1.getText().toString();
                s_address2 = et_address2.getText().toString();
                s_landmark = et_ladmark.getText().toString();
                s_city = et_city.getText().toString();
                s_postal = et_postal.getText().toString();
                s_pass = et_pass.getText().toString();


                if (allCheck()) {
                    try {
                        myUrl = "http://192.168.1.5/open_rep/index.php?route=feed/customer_api/addMyCustomer&firstname=" + URLEncoder.encode(s_fname, "UTF-8") + "&lastname=" + URLEncoder.encode(s_lname, "UTF-8") + "&email=" + URLEncoder.encode(s_mail, "UTF-8") + "&telephone=" + URLEncoder.encode(s_telephone, "UTF-8") + "&fax=" + URLEncoder.encode(s_fax, "UTF-8") + "&password=" + URLEncoder.encode(s_pass, "UTF-8") + "&company=" + URLEncoder.encode(s_landmark, "UTF-8") + "&address_1=" + URLEncoder.encode(s_address1, "UTF-8") + "&address_2=" + URLEncoder.encode(s_address2, "UTF-8") + "&city=" + URLEncoder.encode(s_city, "UTF-8") + "&postcode=" + URLEncoder.encode(s_postal, "UTF-8");

                        newURL = java.net.URLEncoder.encode(myUrl, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d("URL", myUrl);
                    new SignupAsync().execute(myUrl);

                } else {

                }


            }
        });
    }

    private boolean allCheck() {
        if (et_fname.getText().toString().isEmpty()) {
            et_fname.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_lname.getText().toString().isEmpty()) {
            et_lname.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_mail.getText().toString().isEmpty()) {
            et_mail.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_telephone.getText().toString().isEmpty()) {
            et_telephone.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_fax.getText().toString().isEmpty()) {
            et_fax.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_ladmark.getText().toString().isEmpty()) {
            et_ladmark.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_address1.getText().toString().isEmpty()) {
            et_address1.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_address2.getText().toString().isEmpty()) {
            et_address2.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_city.getText().toString().isEmpty()) {
            et_city.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_postal.getText().toString().isEmpty()) {
            et_postal.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_pass.getText().toString().isEmpty()) {
            et_pass.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (et_confirmPass.getText().toString().isEmpty()) {
            et_confirmPass.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_danger));
            return false;
        } else if (!et_pass.getText().toString().equals(et_confirmPass.getText().toString())) {
            Toast.makeText(SignupActivity.this, "Password Does Not Match", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    private class SignupAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SignupActivity.this);
            dialog.setMessage("Siging up");
            dialog.setTitle("Please wait");
            dialog.show();
            dialog.setCancelable(false);

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                // StatusLine stat = response.getStatusLine();

                Log.d("status", String.valueOf(status));
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data1 = EntityUtils.toString(entity);

                    JSONObject jsono = new JSONObject(data1);
                    res = jsono.getInt("status");
                    if (res == 1) {
                        customer_id = jsono.getString("Customer_id");
                        return true;
                    }

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.cancel();
            if (result) {
                Toast.makeText(SignupActivity.this, "Sign up Successfull" + customer_id, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(SignupActivity.this, "Status: Failed" + res, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
