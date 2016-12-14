package com.think.myopencart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.think.myopencart.ProductViewActivity;
import com.think.myopencart.R;
import com.think.myopencart.model.DataProduct;

import java.util.Collections;
import java.util.List;

/**
 * Created by Super on 27-08-2016.
 */
public class AdapterProductList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<DataProduct> data = Collections.emptyList();
    DataProduct current;
    int currentPos = 0;

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterProductList(Context context, List<DataProduct> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_productlist, parent, false);
        AdapterProductList.MyHolder holder = new AdapterProductList.MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        AdapterProductList.MyHolder myHolder = (AdapterProductList.MyHolder) holder;
        DataProduct current = data.get(position);
        myHolder.p_name.setText(current.productName);
        //  myHolder.textSize.setText("Size: " + current.sizeName);
        //  myHolder.textType.setText("Category: " + current.catName);
        myHolder.p_price.setText("₹ " + current.productPrice + "/-");
        myHolder.p_price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        // load image into imageview using glide
        Glide.with(context).load(current.productImg)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(myHolder.p_image);



    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView p_name;
        ImageView p_image;
        TextView textSize;
        TextView textType;
        TextView p_price;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            p_name = (TextView) itemView.findViewById(R.id.Product_name);
            p_image = (ImageView) itemView.findViewById(R.id.Product_image);

            p_price = (TextView) itemView.findViewById(R.id.Product_price);
            p_price.setSelected(true);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {
            int a = getAdapterPosition();
            DataProduct current = data.get(a);
            String pID = current.productId;

            Intent i = new Intent(context, ProductViewActivity.class);
            i.putExtra("PRODUCT_ID", pID);
            context.startActivity(i);
            //   context.startActivity(new Intent(context,ProductViewActivity.class));

            //   Toast.makeText(context, "super"+ pID, Toast.LENGTH_SHORT).show();

        }
    }



}