package babiy.findplaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import babiy.findplaces.adapter.MyResultAdapter;
import babiy.findplaces.model.DataOfPlace;
import babiy.findplaces.utils.DistanceComparator;
import babiy.findplaces.utils.Keys;
import babiy.findplaces.utils.Utils;

public class DisplayOfResultsActivity extends AppCompatActivity {

    private final String LOG = "ResultActivity";
    private static final String MAIN_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    private static final String NEARBY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private static final String MY_KEY = "AIzaSyDc7ZuDwdIke2Upxj749Cpjb7aPDn8tyi8";

    private ArrayList<DataOfPlace> list;
    private float currentLat;
    private float currentLng;
    private float latOfCity;
    private float lngOfCity;
    private RequestQueue requestQueue;
    private TextView textView;
    private String nextPage, mCategory, mCity, mRadius;
    private ProgressDialog progressDialog;
    private ListView lvList;
    private MyResultAdapter adapter;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_of_results);

        list = new ArrayList<>();
        lvList = (ListView) findViewById(R.id.lvDisplay);
        adapter = new MyResultAdapter(this, list);
        requestQueue = Volley.newRequestQueue(this);
        lvList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        textView = (TextView) findViewById(R.id.tvListSize);
        language = Utils.getLocale();
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

        String search = getIntent().getStringExtra("GetTypeSearch");
        if (search.equals("Text Search")) {
            latOfCity = Float.parseFloat(getIntent().getStringExtra("getLat"));
            lngOfCity = Float.parseFloat(getIntent().getStringExtra("getLng"));
            mCity = getIntent().getStringExtra("City for search");
            textSearch(mCity, mCategory, nextPage);
        } else {
            currentLat = Float.parseFloat(getIntent().getStringExtra("Get currentLat"));
            currentLng = Float.parseFloat(getIntent().getStringExtra("Get currentLng"));
            mRadius = getIntent().getStringExtra("Get radius");
            nearbySearch(currentLat, currentLng, nextPage);
        }
    }

    public void textSearch(final String city, String category, String parsNextPage) {

        Log.i(LOG, "fetch data");
        progressDialog.show();
        String url;

        if (nextPage == null) {
            url = MAIN_URL + category + "+in+" + city + "&language=" + language + "&key=" + MY_KEY;
        } else {
            url = MAIN_URL + "&key=" + MY_KEY + "&pagetoken=" + parsNextPage;
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
                                adapter.notifyDataSetChanged();
                                lvList.setVisibility(View.VISIBLE);
                            }
                            JSONArray jsonArray = response.getJSONArray(Keys.KEY_RESULT);

                            for (int indexJson = 0; indexJson < jsonArray.length(); indexJson++) {
                                DataOfPlace data = new DataOfPlace();

                                JSONObject innerObject = jsonArray.getJSONObject(indexJson);

                                String name = innerObject.getString(Keys.KEY_NAME);
                                String rating = "0";
                                try {
                                    rating = innerObject.getString(Keys.KEY_RATING);
                                } catch (Exception e) {
                                }
                                String placeId = innerObject.getString(Keys.KEY_PLACE_ID);
                                String address = innerObject.getString(Keys.KEY_ADDRESS);
                                float latOfPlace = Float.parseFloat(innerObject.getJSONObject(Keys.KEY_GEOMETRY)
                                        .getJSONObject(Keys.KEY_LOCATION)
                                        .getString(Keys.KEY_LOCATION_LAT));
                                float lngOfPlace = Float.parseFloat(innerObject.getJSONObject(Keys.KEY_GEOMETRY)
                                        .getJSONObject(Keys.KEY_LOCATION)
                                        .getString(Keys.KEY_LOCATION_LNG));
                                String opening = "";

                                if (innerObject.toString().contains(Keys.KEY_OPENING_HOURS)) {
                                    opening = innerObject.getJSONObject(Keys.KEY_OPENING_HOURS).getString(Keys.KEY_OPENING);
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
                                double distance = meterDistanceBetweenPoints(latOfCity, lngOfCity, latOfPlace, lngOfPlace);
                                data.setDistance(distance);

                                list.add(data);
                                list = sortByDistance(list);
                            }
                            if (nextPage != null) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        textSearch(mCity, mCategory, nextPage);
                                    }
                                }, 3000);

                            }

                        } catch (JSONException e) {
                            textView.setText(e.toString());
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(getRequest);

    }

    private void nearbySearch(final float parsCurrentLat, final float parsCurrentLng, String parsNextPage) {
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
                                list = sortByDistance(list);

                                progressDialog.dismiss();
                                lvList.setVisibility(View.VISIBLE);
                            }

                            JSONArray jsonArray = response.getJSONArray(Keys.KEY_RESULT);

                            for (int indexJson = 0; indexJson < jsonArray.length(); indexJson++) {
                                DataOfPlace data = new DataOfPlace();

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

                        textView.setText(String.valueOf(list.size()));

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

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

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
