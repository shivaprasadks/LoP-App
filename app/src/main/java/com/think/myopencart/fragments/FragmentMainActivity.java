package com.think.myopencart.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.think.myopencart.CartViewActivity;
import com.think.myopencart.FragmentProductList;
import com.think.myopencart.LoginActivity;
import com.think.myopencart.ProductViewActivity;
import com.think.myopencart.R;
import com.think.myopencart.SignupActivity;
import com.think.myopencart.adapter.AdapterBrand;
import com.think.myopencart.adapter.AdapterFish;
import com.think.myopencart.adapter.AdapterHomeProduct;
import com.think.myopencart.model.DataFish;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Super on 25-08-2016.
 */
public class FragmentMainActivity extends Fragment implements ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String KEY_MOVIE_TITLE = "key_title";
    private RecyclerView bestSellerList, manufacturerList;
    private RecyclerView specialSellerList;
    private AdapterFish specialAdapter;
    private AdapterHomeProduct bestAdapter;
    private AdapterBrand manuAdapter;
    private SliderLayout mDemoSlider;
    private View cardBest, cardSpecial, cardBrand;
    private HashMap<String, String> file_maps;
    public HashMap<String, String> products_map = new HashMap<String, String>();
    private ImageView btn_search;
    private AutoCompleteTextView et_search;
    private Context context;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView tv_title;
    String product_suggestion[] = {"no Suggestions"};
    ArrayList<String> productList = new ArrayList<String>();

    private ArrayAdapter<String> search_suggestion;
    private boolean search_check = false;

    public FragmentMainActivity() {

    }

    public static FragmentMainActivity newInstance(String title) {

        FragmentMainActivity mfActivity = new FragmentMainActivity();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_TITLE, title);
        mfActivity.setArguments(args);
        return mfActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_home, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        cardBest = (View) view.findViewById(R.id.card_best);
        cardSpecial = (View) view.findViewById(R.id.card_special);
        cardBrand = (View) view.findViewById(R.id.card_brand);
        btn_search = (ImageView) view.findViewById(R.id.btn_search);
        et_search = (AutoCompleteTextView) view.findViewById(R.id.et_search);
        tv_title = (TextView) view.findViewById(R.id.title);

        manufacturerList = (RecyclerView) view.findViewById(R.id.top_brands);
        specialSellerList = (RecyclerView) view.findViewById(R.id.Special_products);
        bestSellerList = (RecyclerView) view.findViewById(R.id.best_Seller);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swifeRefresh);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_check) {
                    et_search.setVisibility(View.GONE);

                    tv_title.setVisibility(View.VISIBLE);
                    btn_search.setImageResource(android.R.drawable.ic_menu_search);
                    et_search.setText("");
                    search_check = false;


                } else {

                    et_search.setVisibility(View.VISIBLE);
                    tv_title.setVisibility(View.GONE);
                    btn_search.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    search_check = true;


                    search_suggestion = new ArrayAdapter<String>(view.getContext(),
                            android.R.layout.simple_list_item_1, product_suggestion);
                    et_search.setThreshold(1);

                    //Set adapter to AutoCompleteTextView
                    et_search.setAdapter(search_suggestion);
                    et_search.setOnItemSelectedListener(FragmentMainActivity.this);
                    et_search.setOnItemClickListener(FragmentMainActivity.this);
                }


            }
        });

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String customer_id = getActivity().getSharedPreferences("PREF", MODE_PRIVATE).getString("customer_id", null);
                if (customer_id == null) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), CartViewActivity.class);
                    getContext().startActivity(intent);
                }

            }
        });

     /*   mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new JSONAsyncTask().execute("http://lifeofpet.com/shop/index.php?route=feed/rest_api/getBestSeller&key=Seyon@168");
            }
        });*/
        new JSONAsyncTask().execute("http://lifeofpet.com/shop/index.php?route=feed/rest_api/getBestSeller&key=Seyon@168");
    }


    private class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {
        List<DataFish> special_data = new ArrayList<>();
        List<DataFish> best_data = new ArrayList<>();
        List<DataFish> manufacturer_data = new ArrayList<>();

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(true);
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
                    JSONArray product_array = jsono.getJSONArray("products");
                    //  JSONArray jarray = jsono.getJSONArray(data1);

                    for (int i = 0; i < product_array.length(); i++) {
                        JSONObject json_data = product_array.getJSONObject(i);


                        productList.add(json_data.getString("name"));
                        products_map.put(json_data.getString("name"), json_data.getString("id"));
                        //   best_data.add(fishData);
                    }
                    product_suggestion = productList.toArray(new String[productList.size()]);


                    JSONArray jarray = jsono.getJSONArray("best");
                    //  JSONArray jarray = jsono.getJSONArray(data1);

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json_data = jarray.getJSONObject(i);

                        DataFish fishData = new DataFish();
                        fishData.fishImage = json_data.getString("image");
                        fishData.fishName = json_data.getString("name");
                        fishData.price = json_data.getString("price");
                       /* fishData.sizeName= json_data.getString("size_name");*/
                        fishData.productId = json_data.getString("product_id");
                        best_data.add(fishData);
                    }
                    JSONArray jarray2 = jsono.getJSONArray("special");
                    //  JSONArray jarray = jsono.getJSONArray(data1);

                    for (int i = 0; i < jarray2.length(); i++) {
                        JSONObject json_data = jarray2.getJSONObject(i);

                        DataFish fishData2 = new DataFish();
                        fishData2.fishImage = json_data.getString("image");
                        fishData2.fishName = json_data.getString("name");
                        fishData2.price = json_data.getString("price");
                       /* fishData.sizeName= json_data.getString("size_name");*/
                        fishData2.productId = json_data.getString("product_id");
                        special_data.add(fishData2);

                    }

                    JSONArray jarray3 = jsono.getJSONArray("manufacturer");
                    //  JSONArray jarray = jsono.getJSONArray(data1);

                    for (int i = 0; i < jarray3.length(); i++) {
                        JSONObject json_data = jarray3.getJSONObject(i);

                        DataFish fishData2 = new DataFish();
                        fishData2.fishImage = json_data.getString("image");
                        fishData2.fishName = json_data.getString("name");
                        //   fishData2.price = json_data.getString("price");
                       /* fishData.sizeName= json_data.getString("size_name");*/
                        fishData2.manufactureId = json_data.getString("manufacturer_id");
                        manufacturer_data.add(fishData2);


                    }
                    JSONArray sliderarray = jsono.getJSONArray("slider");
                    file_maps = new HashMap<String, String>();
                    for (int i = 0; i <= sliderarray.length(); i++) {
                        JSONObject sliderObj = sliderarray.getJSONObject(i);
                        file_maps.put(sliderObj.getString("name"), sliderObj.getString("image"));
                    }

                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            mSwipeRefreshLayout.setRefreshing(false);
            cardBest.setVisibility(View.VISIBLE);
            cardSpecial.setVisibility(View.VISIBLE);
            cardBrand.setVisibility(View.VISIBLE);
            // Setup and Handover data to recyclerview
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            //   bestSellerList = (RecyclerView) findViewById(R.id.best_Seller);
            bestAdapter = new AdapterHomeProduct(getActivity(), best_data);
            bestSellerList.setAdapter(bestAdapter);
            bestSellerList.setLayoutManager(layoutManager);

            LinearLayoutManager layoutManager2
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            //   specialSellerList = (RecyclerView) findViewById(R.id.Special_products);
            specialAdapter = new AdapterFish(getActivity(), special_data);

            specialSellerList.setAdapter(specialAdapter);
            specialSellerList.setLayoutManager(layoutManager2);

            LinearLayoutManager layoutManager3
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            //   manufacturerList = (RecyclerView) findViewById(R.id.top_brands);
            manuAdapter = new AdapterBrand(getActivity(), manufacturer_data);
            manufacturerList.setAdapter(manuAdapter);
            manufacturerList.setLayoutManager(layoutManager3);


            //slider layout initialization
            for (String name : file_maps.keySet()) {
                TextSliderView textSliderView = new TextSliderView(getActivity());
                // initialize a SliderLayout
                textSliderView
                        .description(name)
                        .image(file_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(FragmentMainActivity.this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", name);

                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);
            mDemoSlider.addOnPageChangeListener(FragmentMainActivity.this);

            if (!result) {
            }
          //  Toast.makeText(getActivity(), "Welcome Back", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String pID = products_map.get(adapterView.getItemAtPosition(i));
      //  Toast.makeText(getContext(), pID,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), ProductViewActivity.class);
        intent.putExtra("PRODUCT_ID", pID);
        getActivity().startActivity(intent);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
