package com.think.myopencart;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    EditText email, pass;
    Button login;
    TextView signup;
    String cID, name;

    private ProgressDialog dialog;
    private static String eString = null, pString = null, msg;
    int res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.etEmail);
        pass = (EditText) findViewById(R.id.etPass);

        login = (Button) findViewById(R.id.btnSingIn);

        signup = (TextView) findViewById(R.id.tv_sign_up);

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                eString = email.getText().toString();
                pString = pass.getText().toString();

                Log.d("URL_login", "http://lifeofpet.com/shop/index.php?route=feed/customer_api/myCustomerLogin&email=" + eString + "&pass=" + pString);
                if (eString.isEmpty() || pString.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter Email & Password", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginAsync().execute("http://lifeofpet.com/shop/index.php?route=feed/customer_api/myCustomerLogin&email=" + eString + "&pass=" + pString);

                }

            }
        });
        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

    }

    private class LoginAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Siging in");
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

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data1 = EntityUtils.toString(entity);

                    JSONObject jsono = new JSONObject(data1);
                    res = jsono.getInt("status");
                    if (res == 1) {
                        cID = jsono.getString("customer_id");
                        name = jsono.getString("customer_name");
                    }

                    return true;
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
                if (res == 1) {

                    Toast.makeText(LoginActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                    getSharedPreferences("PREF", MODE_PRIVATE)
                            .edit()
                            .putString("customer_id", cID)
                            .commit();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(LoginActivity.this, "Status : Success  " + cID, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Error in network " + res, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}