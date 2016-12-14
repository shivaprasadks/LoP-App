package com.think.myopencart;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Checkout extends AppCompatActivity {
    private ProgressDialog dialog;
    String orderID;
    Button btn_checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        btn_checkout = (Button) findViewById(R.id.btn_confirm);

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customer_id = getSharedPreferences("PREF", MODE_PRIVATE).getString("customer_id", null);
                new CheckoutAsync().execute("http://lifeofpet.com/shop/index.php?route=feed/checkout_api/addMyOrder&cID="+customer_id);

            }
        });
    }

    private class CheckoutAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Checkout.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
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
                    int res = jsono.getInt("status");
                    if (res == 1) {
                        orderID = jsono.getString("order_id");
                        return true;
                    }
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.cancel();
            if (result) {
                Toast.makeText(Checkout.this, "Checkout Success \n ORDER ID: " + orderID, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Checkout.this, "Failed to Checkout", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
