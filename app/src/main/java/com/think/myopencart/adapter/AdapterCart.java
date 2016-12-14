package com.think.myopencart.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.think.myopencart.CartViewActivity;
import com.think.myopencart.ProductViewActivity;
import com.think.myopencart.R;
import com.think.myopencart.model.DataProduct;
import com.think.myopencart.model.DataProduct;
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
import java.util.Collections;
import java.util.List;

/**
 * Created by Super on 31-08-2016.
 */

public class AdapterCart extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<DataProduct> data = Collections.emptyList();
    DataProduct current;
    int currentPos = 0;
    private ProgressDialog dialog;
    private DataProduct productData;
    private RecyclerView productList;
    AdapterCart productAdapter;

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterCart(Context context, List<DataProduct> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_cart_view, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        final DataProduct current = data.get(position);
        myHolder.p_name.setText(current.productName);
        //  myHolder.textSize.setText("Size: " + current.sizeName);
        //  myHolder.textType.setText("Category: " + current.catName);
        myHolder.p_price.setText("₹ " + current.productPrice + "/-");
        myHolder.p_price.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        String s_quantity = String.valueOf(current.quantity);
        myHolder.tv_quantity.append(s_quantity);

        // load image into imageview using glide
        Glide.with(context).load(current.productImg)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(myHolder.p_image);
        myHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateCartAsync().execute("http://lifeofpet.com/shop/index.php?route=feed/checkout_api/deleteCartItem&cID=9&pID=" + current.productId);

                Log.d("CART DELETE", "http://lifeofpet.com/shop/index.php?route=feed/checkout_api/deleteCartItem&cID=9&pID=" + current.productId);
            }
        });


    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener */ {

        TextView p_name;
        ImageView p_image;
        TextView textSize;
        TextView tv_quantity;
        TextView p_price;
        Button btn_remove;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            p_name = (TextView) itemView.findViewById(R.id.Product_name);
            p_image = (ImageView) itemView.findViewById(R.id.Product_image);
            btn_remove = (Button) itemView.findViewById(R.id.btn_remove);

            p_price = (TextView) itemView.findViewById(R.id.Product_price);
            tv_quantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            p_price.setSelected(true);
            //   itemView.setOnClickListener(this);
        }


    }

    public class UpdateCartAsync extends AsyncTask<String, Void, Boolean> {
        List<DataProduct> product_data = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
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
                    JSONArray jarray = jsono.getJSONArray("cart");
                    //  JSONArray jarray = jsono.getJSONArray(data1);


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
                Intent i = ((CartViewActivity) context).getIntent();
                ((CartViewActivity) context).finish();
                context.startActivity(i);

            } else {
                Toast.makeText(context, "Connection To Server Lost", Toast.LENGTH_SHORT).show();
            }


        }
    }

}