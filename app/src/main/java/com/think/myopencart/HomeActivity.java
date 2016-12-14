package com.think.myopencart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.think.myopencart.adapter.CustomExpandableListAdapter;
import com.think.myopencart.constants.ExpandableListDataSource;
import com.think.myopencart.fragments.navigation.FragmentNavigationManager;
import com.think.myopencart.fragments.navigation.NavigationManager;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String[] items;

    private ExpandableListView mExpandableListView;
    private CustomExpandableListAdapter mExpandableListAdapter, expandaleAdapter;
    private List<String> mExpandableListTitle;
    private NavigationManager mNavigationManager;

    private Map<String, List<String>> mExpandableListData;


    // declare array List for all headers in list
    List<String> categoryHeader = new ArrayList<>();

    // Declare Hash map for all headers and their corresponding values
    Map<String, List<String>> categoryChild = new HashMap<String, List<String>>();

    public static HashMap<String, String> subCat = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        boolean firstboot = getSharedPreferences("BOOT_PREF", MODE_PRIVATE).getBoolean("firstboot", true);

        if (firstboot){

            //	Toast.makeText(getApplicationContext(), "in true", Toast.LENGTH_LONG).show();
            // 1) Launch the authentication activity

            //	  setContentView(R.layout.welcome_screen);


            // 2) Then save the state
            getSharedPreferences("BOOT_PREF", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstboot", false)
                    .commit();
            // Toast.makeText(getApplicationContext(), "in false", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            //    finish();

        }

        else {
            // Toast.makeText(getApplicationContext(), "in false", Toast.LENGTH_LONG).show();
          //  Intent i = new Intent(this, LoginActivity.class);
          //  startActivity(i);
            //    finish();
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mExpandableListView = (ExpandableListView) findViewById(R.id.navList);
        mNavigationManager = FragmentNavigationManager.obtain(this);

        initItems();

        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.nav_header_home, null, false);
        mExpandableListView.addHeaderView(listHeaderView);

        mExpandableListData = ExpandableListDataSource.getData(this);
        mExpandableListTitle = new ArrayList(mExpandableListData.keySet());

        addDrawerItems();
        setupDrawer();

        if (savedInstanceState == null) {
            selectFirstItemAsDefault();
        }

        new getCategoryJSON().execute("http://www.lifeofpet.com/shop/index.php?route=feed/rest_api/categories&parent=0&level=2&key=Seyon@168");

        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //   getSupportActionBar().setHomeButtonEnabled(true);
    }

    private class getCategoryJSON extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(HomeActivity.this);
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
                        categoryHeader.add(json_data.getString("name"));

                        if (!(json_data.getString("categories")).equals("null")) {
                            Log.d("sub category", json_data.getString("name"));


                            JSONArray subcatogary = json_data.getJSONArray("categories");

                            List<String> sub = new ArrayList<String>();
                            for (int j = 0; j < subcatogary.length(); j++) {

                                JSONObject json_sub = subcatogary.getJSONObject(j);
                                //      editor.putString(json_sub.getString("name"), json_sub.getString("category_id"));
                                //   SharedPreferences.Editor editor = sharedpreferences.edit();


                                subCat.put(json_sub.getString("name"), json_sub.getString("category_id"));

                                sub.add(json_sub.getString("name"));

                            }
                            categoryChild.put(categoryHeader.get(i), sub);
                        }


                    }
                }
                return true;
            } catch (ParseException e1) {
                e1.printStackTrace();
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

        }
    }

    private void selectFirstItemAsDefault() {
        if (mNavigationManager != null) {
            String firstActionMovie = getResources().getStringArray(R.array.actionFilms)[0];
            mNavigationManager.showMainFragmentActivity(firstActionMovie);
            //   getSupportActionBar().setTitle(firstActionMovie);
        }
    }

    private void initItems() {
        items = getResources().getStringArray(R.array.film_genre);
    }

    private void addDrawerItems() {
        mExpandableListAdapter = new CustomExpandableListAdapter(this, categoryHeader, categoryChild);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //    getSupportActionBar().setTitle(mExpandableListTitle.get(groupPosition).toString());
            }
        });

        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //    getSupportActionBar().setTitle(R.string.film_genres);
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (categoryChild.get(categoryHeader.get(groupPosition))))
                        .get(childPosition).toString();
                //   getSupportActionBar().setTitle(selectedItem);
                for (int i = 0; i < items.length; i++) {
                    //   Toast.makeText(HomeActivity.this, selectedItem, Toast.LENGTH_SHORT).show();
                    mNavigationManager.showFragmentCategory(selectedItem);
                }
               /*  if (items[0].equals(mExpandableListTitle.get(groupPosition))) {
                    mNavigationManager.showFragmentAction(selectedItem);
                }else if (items[1].equals(mExpandableListTitle.get(groupPosition))) {
                    mNavigationManager.showFragmentComedy(selectedItem);
                } else if (items[2].equals(mExpandableListTitle.get(groupPosition))) {
                    mNavigationManager.showFragmentDrama(selectedItem);
                } else if (items[3].equals(mExpandableListTitle.get(groupPosition))) {
                    mNavigationManager.showFragmentMusical(selectedItem);
                } else if (items[4].equals(mExpandableListTitle.get(groupPosition))) {
                    mNavigationManager.showFragmentThriller(selectedItem);
                } else {
                    throw new IllegalArgumentException("Not supported fragment type");
                }*/

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //     getSupportActionBar().setTitle(R.string.film_genres);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //    getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getHashMap(String name) {
        return subCat.get(name);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mNavigationManager == null) {
           finish();
        }else {
            String firstActionMovie = getResources().getStringArray(R.array.actionFilms)[0];
            mNavigationManager.showMainFragmentActivity(firstActionMovie);
            //   getSupportActionBar().setTitle(firstActionMovie);
        }
    }
}

