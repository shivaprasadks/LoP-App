package com.think.myopencart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.think.myopencart.adapter.AdapterCart;
import com.think.myopencart.adapter.AdapterProductList;
import com.think.myopencart.model.DataProduct;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CartViewActivity extends AppCompatActivity {
    private TextView productName, productPrice, specialPrice, stockInfo, brandName, productRating, modelName, tv_subTotal;
    private ImageView tumbImg, productImg;
    private ProgressDialog dialog;
    private DataProduct productData;
    private RecyclerView productList;
    AdapterCart productAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String cID, str_subTotal;
    private Button btn_checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productList = (RecyclerView) findViewById(R.id.cart_product_list);
        tv_subTotal = (TextView) findViewById(R.id.tv_sub_total);

        btn_checkout = (Button) findViewById(R.id.btn_checkout);


        Intent in = getIntent();
        cID = in.getStringExtra("CATEGORY_ID");

        String customer_id = getSharedPreferences("PREF", MODE_PRIVATE).getString("customer_id", null);
        Log.d("URL_CARTGET", "http://lifeofpet.com/shop/index.php?route=feed/checkout_api/getCartItem&cID=" + customer_id);

        new GetCartAsync().execute("http://lifeofpet.com/shop/index.php?route=feed/checkout_api/getCartItem&cID=" + customer_id);

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CartViewActivity.this, Checkout.class);
                startActivity(i);
            }
        });

    }

    private class GetCartAsync extends AsyncTask<String, Void, Boolean> {
        List<DataProduct> product_data = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CartViewActivity.this);
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
                Log.d("CART_JSON", String.valueOf(status));
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data1 = EntityUtils.toString(entity);


                    JSONObject jsono = new JSONObject(data1);
                    Log.d("CART_JSON", String.valueOf(jsono));
                    JSONArray jarray = jsono.getJSONArray("cart");
                    //  JSONArray jarray = jsono.getJSONArray(data1);
                Log.d("CART_JSON", String.valueOf(jarray));
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json_data = jarray.getJSONObject(i);
                        productData = new DataProduct();

                        productData.productId = json_data.getString("product_id");
                        productData.productName = json_data.getString("product_name");
                        productData.productImg = json_data.getString("product_img");
                        productData.productPrice = json_data.getString("product_price");
                        productData.quantity = json_data.getInt("quantity");



                        product_data.add(productData);
                    }
                 str_subTotal = jsono.getString("sub_total");
                    return true;
                }

                //------------------>>

            } catch (ParseException | IOException | JSONException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.cancel();
            //     mSwipeRefreshLayout.setRefreshing(false);

            if (result) {

                LinearLayoutManager layoutManager = new LinearLayoutManager(CartViewActivity.this, LinearLayoutManager.VERTICAL, false);
                productAdapter = new AdapterCart(CartViewActivity.this, product_data);
                productList.setAdapter(productAdapter);
                productList.setLayoutManager(layoutManager);
                tv_subTotal.append(str_subTotal);
            } else {
                Toast.makeText(CartViewActivity.this, "No Item In Cart", Toast.LENGTH_SHORT).show();
                btn_checkout.setVisibility(View.GONE);
            }


        }
    }


}
