package babiy.findplaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import babiy.findplaces.adapter.MyRecycleAdapter;
import babiy.findplaces.adapter.MyResultAdapter;
import babiy.findplaces.model.DataOfPlace;
import babiy.findplaces.utils.DistanceComparator;
import babiy.findplaces.utils.Keys;

public class DisplayOfResultsActivity extends AppCompatActivity {

    private final String LOG = "ResultActivity";
    private static final String NEARBY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private static final String MY_KEY = "AIzaSyDc7ZuDwdIke2Upxj749Cpjb7aPDn8tyi8";

    private ArrayList<DataOfPlace> list;
    private double currentLat;
    private double currentLng;

    private RequestQueue requestQueue;

    private String nextPage, mCategory, mRadius;
    private ProgressDialog progressDialog;
    private ListView lvList;
    private MyResultAdapter adapter;
    private String language;
    //MyRecycleAdapter rvadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_of_results);

        list = new ArrayList<>();
        lvList = (ListView) findViewById(R.id.lvDisplay);
        adapter = new MyResultAdapter(this, list);
        requestQueue = Volley.newRequestQueue(this);
       lvList.setAdapter(adapter);

       // RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
      //  rv.setHasFixedSize(true);
     //   rvadapter = new MyRecycleAdapter(list);
      //  rv.setAdapter(rvadapter);


        language = Locale.getDefault().getLanguage();
        mCategory = getIntent().getStringExtra("Category for search");

        progressDialog = new ProgressDialog(DisplayOfResultsActivity.this);
        progressDialog.setMessage("Searching...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);


       lvList.setVisibility(View.INVISIBLE);


        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataOfPlace information = list.get(position);
                Intent intent = new Intent(DisplayOfResultsActivity.this, DataAboutPlaceActivity.class);
                intent.putExtra("place_id", information.getId())
                        .putExtra("get latOfPlace", information.getLocationLat())
                        .putExtra("get lngOfPlace", information.getLocationLng());
                startActivity(intent);

            }
        });

        currentLat = getIntent().getDoubleExtra("Get currentLat", 0);
        currentLng = getIntent().getDoubleExtra("Get currentLng", 0);
        mRadius = getIntent().getStringExtra("Get radius");
        nearbySearch(currentLat, currentLng, nextPage);

    }

    private void nearbySearch(final double parsCurrentLat, final double parsCurrentLng, String parsNextPage) {
        Log.i(LOG, "fetch data");
        progressDialog.show();

        String url;
        if (nextPage == null) {
            url = NEARBY_URL + currentLat + "," + currentLng + "&language=" + language +
                    "&type=" + mCategory + "&radius=" + mRadius + "&key=" + MY_KEY;
        } else {
            url = NEARBY_URL + "&key=" + MY_KEY + "&pagetoken=" + parsNextPage;
        }

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(LOG, "parsed data");

                            if (response.has(Keys.KEY_NEXT_PAGE)) {
                                nextPage = response.getString(Keys.KEY_NEXT_PAGE);
                            } else {
                                nextPage = null;
                                progressDialog.dismiss();
                               lvList.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                               // rvadapter.notifyDataSetChanged();
                            }

                            JSONArray jsonArray = response.getJSONArray(Keys.KEY_RESULT);

                            for (int indexJson = 0; indexJson < jsonArray.length(); indexJson++) {
                                final DataOfPlace data = new DataOfPlace();

                                JSONObject innerObject = jsonArray.getJSONObject(indexJson);

                                String name = innerObject.getString(Keys.KEY_NAME);
                                String rating = "0";
                                try {
                                    rating = innerObject.getString(Keys.KEY_RATING);
                                } catch (Exception e) {
                                }
                                String placeId = innerObject.getString(Keys.KEY_PLACE_ID);
                                String address = innerObject.getString(Keys.KEY_VICINITY);
                                float latOfPlace = Float.parseFloat(innerObject.getJSONObject(Keys.KEY_GEOMETRY)
                                        .getJSONObject(Keys.KEY_LOCATION)
                                        .getString(Keys.KEY_LOCATION_LAT));
                                float lngOfPlace = Float.parseFloat(innerObject.getJSONObject(Keys.KEY_GEOMETRY)
                                        .getJSONObject(Keys.KEY_LOCATION)
                                        .getString(Keys.KEY_LOCATION_LNG));
                                String opening = "";

                                if (innerObject.has(Keys.KEY_OPENING_HOURS)) {
                                    opening = innerObject.getJSONObject(Keys.KEY_OPENING_HOURS)
                                            .getString(Keys.KEY_OPENING);
                                }
                                data.setName(name);
                                data.setRating(rating);
                                data.setId(placeId);
                                data.setAddress(address);
                                if (opening.equals("true")) {
                                    data.setOpening(getString(R.string.string_open));
                                } else if (opening.equals("false")) {
                                    data.setOpening(getString(R.string.string_close));
                                } else {
                                    data.setOpening("");
                                }
                                data.setLocationLat(latOfPlace);
                                data.setLocationLng(lngOfPlace);
                                double distance = meterDistanceBetweenPoints(parsCurrentLat, parsCurrentLng, latOfPlace, lngOfPlace);
                                data.setDistance(distance);
                                list.add(data);
                                list = sortByDistance(list);
                            }

                            if (nextPage != null) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        nearbySearch(currentLat, currentLng, nextPage);
                                    }
                                }, 3000);
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        progressDialog.dismiss();
                    }
                }
        );
        requestQueue.add(getRequest);

    }

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f / Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public ArrayList<DataOfPlace> sortByDistance(ArrayList<DataOfPlace> distance) {
        Collections.sort(distance, new DistanceComparator());
        return distance;
    }

}
