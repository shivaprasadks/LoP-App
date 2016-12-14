package com.think.myopencart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.think.myopencart.FragmentProductList;
import com.think.myopencart.ProductViewActivity;
import com.think.myopencart.R;
import com.think.myopencart.model.Categories;

import java.util.Collections;
import java.util.List;

/**
 * Created by Super on 26-08-2016.
 */

public class AdapterCategory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Categories> data = Collections.emptyList();
    private Categories current;
    int currentPos = 0;

    //constructor
    public AdapterCategory(Context context, List<Categories> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_category, parent, false);
        AdapterCategory.MyHolder holder = new AdapterCategory.MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in recyclerview to bind data and assign values from list
        AdapterCategory.MyHolder myHolder = (AdapterCategory.MyHolder) holder;
        current = data.get(position);
        myHolder.p_name.setText(current.name);
        //  myHolder.textSize.setText("Size: " + current.sizeName);
        //  myHolder.textType.setText("Category: " + current.catName);
        //  myHolder.p_price.setText("₹ " + current.price + "/-");
        //  myHolder.p_price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        // load image into imageview using glide
        Glide.with(context).load(current.imaage)
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
        private AdapterView.OnItemClickListener listener;
        private TextView mTextView;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            p_name = (TextView) itemView.findViewById(R.id.Product_name);
            p_image = (ImageView) itemView.findViewById(R.id.Product_image);
            itemView.setOnClickListener(this);
            //  p_price.setSelected(true);
        }

        @Override
        public void onClick(View view) {
            int a = getAdapterPosition();
            Categories current = data.get(a);
            String mID = current.category_id;

            Intent i = new Intent(context, FragmentProductList.class);
            i.putExtra("CATEGORY_ID", mID);
            context.startActivity(i);
            Log.d("Click", "onClick " + mID + " " + mItem);

        }
    }


}
