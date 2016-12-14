package com.think.myopencart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.think.myopencart.adapter.AdapterProductList;
import com.think.myopencart.model.DataProduct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Super on 27-08-2016.
 */

public class FragmentProductList extends AppCompatActivity {
    private TextView productName, productPrice, specialPrice, stockInfo, brandName, productRating, modelName;
    private ImageView tumbImg, productImg;
    private ProgressDialog dialog;
    private DataProduct productData;
    private RecyclerView productList;
    AdapterProductList productAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String cID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_prodectlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productList = (RecyclerView) findViewById(R.id.product_list);




        Intent in = getIntent();
        cID = in.getStringExtra("CATEGORY_ID");
        // Swipe Refresh Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swifeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetProductAsync().execute("http://www.lifeofpet.com/shop/index.php?route=feed/rest_api/products&category=" + cID + "&key=Seyon@168");
            }
        });


        new GetProductAsync().execute("http://www.lifeofpet.com/shop/index.php?route=feed/rest_api/products&category="+cID+"&key=Seyon@168");
        // Log.d("URL","http://storedoor.in/index.php?route=feed/rest_api/getMyProduct&pID="+pID+"&key=2201606");
    }

    private class GetProductAsync extends AsyncTask<String, Void, Boolean> {
        List<DataProduct> product_data = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FragmentProductList.this);
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
                        productData = new DataProduct();

                        productData.productId = json_data.getString("id");
                        productData.productImg = json_data.getString("thumb");
                        productData.productName = json_data.getString("name");
                        productData.productPrice = json_data.getString("price");
                     /*   productData.productRating = json_data.getInt("rating");
                        productData.productSpecialPrice = json_data.getString("special");

                        productData.manufacturerName = json_data.getString("manufacturer");
                        productData.modelType = json_data.getString("model");
                        productData.quantity = json_data.getInt("quantity");*/

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

                LinearLayoutManager layoutManager = new GridLayoutManager(FragmentProductList.this, 2);
                productAdapter = new AdapterProductList(FragmentProductList.this, product_data);
                productList.setAdapter(productAdapter);
                productList.setLayoutManager(layoutManager);
            } else {
                Toast.makeText(FragmentProductList.this, "fucked up", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
