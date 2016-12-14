package com.think.myopencart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.think.myopencart.model.DataFish;
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
import java.util.HashMap;
import java.util.List;

public class ProductViewActivity extends AppCompatActivity {
    private TextView productName, productPrice, specialPrice, stockInfo, brandName, productRating, modelName;
    private ImageView tumbImg, productImg;
    private ProgressDialog dialog;
    private DataProduct productData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Button btn_add_to_cart;
    private String pID;
    public static DataProduct cartProduct;
    public static ArrayList<DataProduct> cartList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productName = (TextView) findViewById(R.id.product_name);
        productPrice = (TextView) findViewById(R.id.product_price);
        specialPrice = (TextView) findViewById(R.id.product_price_special);
        brandName = (TextView) findViewById(R.id.product_brand);
        productRating = (TextView) findViewById(R.id.tvView_Rating);
        stockInfo = (TextView) findViewById(R.id.tvView_product_status);

        tumbImg = (ImageView) findViewById(R.id.product_thumbnail);
        productImg = (ImageView) findViewById(R.id.Product_image);

        btn_add_to_cart = (Button) findViewById(R.id.bView_product_add_to_cart);

        productData = new DataProduct();

        Intent in = getIntent();
        pID = in.getStringExtra("PRODUCT_ID");
        // Swipe Refresh Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swifeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetProductAsync().execute("http://lifeofpet.com/shop/index.php?route=feed/rest_api/getMyProduct&pID=" + pID + "&key=Seyon@168");
            }
        });


        new GetProductAsync().execute("http://lifeofpet.com/shop/index.php?route=feed/rest_api/getMyProduct&pID=" + pID + "&key=Seyon@168");
        // Log.d("URL","http://storedoor.in/index.php?route=feed/rest_api/getMyProduct&pID="+pID+"&key=2201606");

        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customer_id = getSharedPreferences("PREF", MODE_PRIVATE).getString("customer_id", null);

                if (customer_id == null) {
                    Intent intent = new Intent(ProductViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    String pID = productData.productId;
                    String cID = customer_id;
                    int quantity = 1;


                    new addtoCartAsync().execute("http://lifeofpet.com/shop/index.php?route=feed/checkout_api/addToCart&pID=" + pID + "&cID=" + cID + "&quantity=" + quantity);

                }
                         }
        });
    }

    private class GetProductAsync extends AsyncTask<String, Void, Boolean> {
        List<DataProduct> product_data = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ProductViewActivity.this);
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
                    JSONArray jarray = jsono.getJSONArray("products");
                    //  JSONArray jarray = jsono.getJSONArray(data1);

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json_data = jarray.getJSONObject(i);


                        productData.productId = json_data.getString("id");
                        productData.productImg = json_data.getString("thumb");
                        productData.productName = json_data.getString("name");
                        productData.productPrice = json_data.getString("price");
                        productData.productRating = json_data.getInt("rating");
                        productData.productSpecialPrice = json_data.getString("special");

                        productData.manufacturerName = json_data.getString("manufacturer");
                        productData.modelType = json_data.getString("model");
                        productData.quantity = json_data.getInt("quantity");

                       /* fishData.sizeName= json_data.getString("size_name");*/

                        product_data.add(productData);
                    }
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
            mSwipeRefreshLayout.setRefreshing(false);
            if (result) {
                productName.setText(product_data.get(0).productName);
                productPrice.setText(product_data.get(0).productPrice);
                productRating.setText(String.valueOf(product_data.get(0).productRating));

                //   modelName.setText(product_data.get(0).modelType);

                brandName.setText(product_data.get(0).manufacturerName);
                specialPrice.setText(product_data.get(0).productSpecialPrice);
                if (product_data.get(0).quantity == 0) {
                    stockInfo.setText("Out of Stock");
                    stockInfo.setTextColor(Color.RED);
                } else {
                    stockInfo.setText(String.valueOf(product_data.get(0).quantity));
                }
                // load image into imageview using glide
                Glide.with(ProductViewActivity.this).load(product_data.get(0).productImg)
                        .placeholder(R.drawable.ic_img_error)
                        .error(R.drawable.ic_img_error)
                        .into(tumbImg);
                // load image into imageview using glide
                Glide.with(ProductViewActivity.this).load(product_data.get(0).productImg)
                        .placeholder(R.drawable.ic_img_error)
                        .error(R.drawable.ic_img_error)
                        .into(productImg);


                Log.d("m name", product_data.get(0).manufacturerName);

            } else {
                Toast.makeText(ProductViewActivity.this, "Server Down", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private class addtoCartAsync extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductViewActivity.this);
            pDialog.setMessage("Adding to cart");
            pDialog.setTitle("Please wait");
            pDialog.show();
            pDialog.setCancelable(false);

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


                }
                return true;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.cancel();
            if (result){
                Toast.makeText(ProductViewActivity.this,"Added to Cart",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(ProductViewActivity.this,"Failed to add",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
