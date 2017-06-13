package babiy.findplaces;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import babiy.findplaces.utils.InternetConnection;
import babiy.findplaces.utils.Keys;


public class MainActivity extends AppCompatActivity {

    private static final String PLACE_ID_URL = "http://maps.googleapis.com/maps/api/geocode/json?address=%22";
    private static final String TAG = "";

    private EditText etCity;

    public String city;
    public String category;
    public String search;
    public String radius;
    private RequestQueue requestQueue;
    private String latOfCity;
    private String lngOfCity;
    private Spinner spinnerCategory;
    private Spinner spinnerSearch;
    private Spinner spinnerRadius;
    private String currentLat;
    private String currentLng;
    private TextView tvNameOfRadius;
    String st;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                st = (String) place.getName();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });




        etCity = (EditText) findViewById(R.id.etCity);
        etCity.setVisibility(View.INVISIBLE);
        tvNameOfRadius = (TextView) findViewById(R.id.tvNameOfRadius);
        requestQueue = Volley.newRequestQueue(this);

        Button btnSearch;
        btnSearch = (Button) findViewById(R.id.btnSearch);

        spinnerCategory = (Spinner) findViewById(R.id.categories_spinner);
        spinnerSearch = (Spinner) findViewById(R.id.type_search_spinner);
        spinnerRadius = (Spinner) findViewById(R.id.radius_spinner);

        currentLat = getIntent().getStringExtra("Get currentLat");
        currentLng = getIntent().getStringExtra("Get currentLng");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = spinnerCategory.getSelectedItem().toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "You should choose something", Toast.LENGTH_LONG).show();
            }
        });

        ArrayAdapter<CharSequence> adapterSearch = ArrayAdapter.createFromResource(this,
                R.array.type_search_array, android.R.layout.simple_spinner_item);
        adapterSearch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearch.setAdapter(adapterSearch);
        spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search = spinnerSearch.getSelectedItem().toString();
                if (search.equals("Text Search")) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    ft.hide(autocompleteFragment);
                    ft.commit();
                    etCity.setVisibility(View.VISIBLE);
                    spinnerRadius.setVisibility(View.INVISIBLE);
                    tvNameOfRadius.setVisibility(View.INVISIBLE);

                } else {
                    etCity.setVisibility(View.INVISIBLE);
                    spinnerRadius.setVisibility(View.VISIBLE);
                    tvNameOfRadius.setVisibility(View.VISIBLE);
                    etCity.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "You should choose something", Toast.LENGTH_LONG).show();
            }
        });

        ArrayAdapter<CharSequence> adapterRadius = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        adapterSearch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRadius.setAdapter(adapterRadius);
        spinnerRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                radius = spinnerRadius.getSelectedItem().toString();
                switch (radius) {
                    case "1 km":
                        radius = "1000";
                        break;
                    case "2 km":
                        radius = "2000";
                        break;
                    case "5 km":
                        radius = "5000";
                        break;
                    case "10 km":
                        radius = "10000";
                        break;
                    case "20 km":
                        radius = "20000";
                        break;
                    case "50 km":
                        radius = "50000";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "You should choose something", Toast.LENGTH_LONG).show();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                city = String.valueOf(etCity.getText()).trim();

                if (InternetConnection.checkConnection(getApplicationContext())) {

                    if (city.equals("")) {
                        Intent intent = new Intent(MainActivity.this, DisplayOfResultsActivity.class);
                        intent.putExtra("Category for search", category)
                                .putExtra("Get currentLat", currentLat)
                                .putExtra("Get currentLng", currentLng)
                                .putExtra("Get radius", radius)
                                .putExtra("GetTypeSearch", search);
                        String r = st;

                        startActivity(intent);
                    } else {
                        getPositionOfCity();

                    }


                } else {
                    Toast.makeText(MainActivity.this, "no internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getPositionOfCity() {

        String url = PLACE_ID_URL + city;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            latOfCity = response.getJSONArray(Keys.KEY_RESULT).getJSONObject(0)
                                    .getJSONObject(Keys.KEY_GEOMETRY)
                                    .getJSONObject(Keys.KEY_LOCATION)
                                    .getString(Keys.KEY_LOCATION_LAT);

                            lngOfCity = response.getJSONArray(Keys.KEY_RESULT).getJSONObject(0)
                                    .getJSONObject(Keys.KEY_GEOMETRY)
                                    .getJSONObject(Keys.KEY_LOCATION)
                                    .getString(Keys.KEY_LOCATION_LNG);

                            Intent intent = new Intent(MainActivity.this, babiy.findplaces.DisplayOfResultsActivity.class);
                            intent.putExtra("City for search", city)
                                    .putExtra("Category for search", category)
                                    .putExtra("getLat", latOfCity)
                                    .putExtra("getLng", lngOfCity)
                                    .putExtra("GetTypeSearch", search);

                            startActivity(intent);


                        } catch (JSONException e) {
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
}
