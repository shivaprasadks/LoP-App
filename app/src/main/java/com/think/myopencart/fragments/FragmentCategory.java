package com.think.myopencart.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.think.myopencart.HomeActivity;
import com.think.myopencart.R;
import com.think.myopencart.adapter.AdapterCategory;
import com.think.myopencart.model.Categories;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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

import static com.think.myopencart.util.SharedPrefUtil.getSharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCategory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCategory extends Fragment {
    private RecyclerView categoryList;
    private AdapterCategory categoryAdapter;
    private static final String KEY_MOVIE_TITLE = "key_title";

    public FragmentCategory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment FragmentAction.
     */
    public static FragmentCategory newInstance(String movieTitle) {
        FragmentCategory fragmentAction = new FragmentCategory();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_TITLE, movieTitle);
        fragmentAction.setArguments(args);

        return fragmentAction;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String categoryTitle = getArguments().getString(KEY_MOVIE_TITLE);



String parent_id = HomeActivity.subCat.get(categoryTitle);
       // Toast.makeText(getContext(), parent_id ,Toast.LENGTH_SHORT).show();


        categoryList = (RecyclerView) view.findViewById(R.id.category_list);
        TextView tv_catergoryTitle = (TextView) view.findViewById(R.id.category_title);
        tv_catergoryTitle.setText(categoryTitle);

        new CategoryAsync().execute("http://www.lifeofpet.com/shop/index.php?route=feed/rest_api/categories&parent="+parent_id+"&level=2&key=Seyon@168");
    }

    private class CategoryAsync extends AsyncTask<String, Void, Boolean> {
        List<Categories> best_data = new ArrayList<>();
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
                    JSONArray jarray = jsono.getJSONArray("category");
                    //  JSONArray jarray = jsono.getJSONArray(data1);

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json_data = jarray.getJSONObject(i);

                        Categories categoryData = new Categories();
                        categoryData.parent_id = json_data.getString("parent_id");
                        categoryData.category_id = json_data.getString("category_id");
                        categoryData.imaage = json_data.getString("image");
                        categoryData.name = json_data.getString("name");

                       /* fishData.sizeName= json_data.getString("size_name");*/

                        best_data.add(categoryData);
                    }
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.cancel();
            if (result) {
                // Setup and Handover data to recyclerview
                LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                //   bestSellerList = (RecyclerView) findViewById(R.id.best_Seller);

                categoryAdapter = new AdapterCategory(getActivity(), best_data);
                categoryList.setAdapter(categoryAdapter);
                categoryList.setLayoutManager(layoutManager);
            } else {

            }

        }
    }

}
