package com.think.myopencart.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.think.myopencart.R;
import com.think.myopencart.model.DataFish;

import java.util.Collections;
import java.util.List;

/**
 * Created by Super on 21-08-2016.
 */

public class AdapterBrand extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<DataFish> data = Collections.emptyList();
    private DataFish current;
    int currentPos = 0;

    //constructor
    public AdapterBrand(Context context, List<DataFish> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_brand, parent, false);
        AdapterBrand.MyHolder holder = new AdapterBrand.MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in recyclerview to bind data and assign values from list
        AdapterBrand.MyHolder myHolder = (AdapterBrand.MyHolder) holder;
        current = data.get(position);
        myHolder.p_name.setText(current.fishName);
        //  myHolder.textSize.setText("Size: " + current.sizeName);
        //  myHolder.textType.setText("Category: " + current.catName);
        //  myHolder.p_price.setText("₹ " + current.price + "/-");
        //  myHolder.p_price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        // load image into imageview using glide
        Glide.with(context).load(current.fishImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(myHolder.p_image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView p_name;
        ImageView p_image;
        private String mItem;
        private TextView mTextView;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            p_name = (TextView) itemView.findViewById(R.id.Brand_name);
            p_image = (ImageView) itemView.findViewById(R.id.Brand_img);
            itemView.setOnClickListener(this);
            //  p_price.setSelected(true);
        }

        @Override
        public void onClick(View view) {
            int a = getAdapterPosition();
            DataFish current = data.get(a);
            String mID = current.manufactureId;
            Log.d("Click", "onClick " + mID + " " + mItem);
        }
    }
}
