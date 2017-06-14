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

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import babiy.findplaces.utils.InternetConnection;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";


    public String city;
    public String category;
    public String search;
    public String radius;
    private double latOfCity;
    private double lngOfCity;
    private Spinner spinnerCategory;
    private Spinner spinnerSearch;
    private Spinner spinnerRadius;
    private double currentLat;
    private double currentLng;
    private TextView tvNameOfRadius;


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
                LatLng location = place.getLatLng();
                latOfCity = location.latitude;
                lngOfCity = location.longitude;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        tvNameOfRadius = (TextView) findViewById(R.id.tvNameOfRadius);

        Button btnSearch;
        btnSearch = (Button) findViewById(R.id.btnSearch);

        spinnerCategory = (Spinner) findViewById(R.id.categories_spinner);
        spinnerSearch = (Spinner) findViewById(R.id.type_search_spinner);
        spinnerRadius = (Spinner) findViewById(R.id.radius_spinner);

        currentLat = getIntent().getDoubleExtra("Get currentLat", 0);
        currentLng = getIntent().getDoubleExtra("Get currentLng", 0);

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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                if (search.equals("Text Search")) {

                    ft.show(autocompleteFragment);

                } else {
                    spinnerRadius.setVisibility(View.VISIBLE);
                    tvNameOfRadius.setVisibility(View.VISIBLE);
                    ft.hide(autocompleteFragment);
                }
                ft.commit();
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



                if (InternetConnection.checkConnection(getApplicationContext())) {
                    Intent intent;

                    if (search.equals("Nearest Places")) {
                        intent = new Intent(MainActivity.this, DisplayOfResultsActivity.class);
                        intent.putExtra("Category for search", category)
                                .putExtra("Get currentLat", currentLat)
                                .putExtra("Get currentLng", currentLng)
                                .putExtra("Get radius", radius);
                        startActivity(intent);
                    } else {
                        intent = new Intent(MainActivity.this, DisplayOfResultsActivity.class);
                        intent.putExtra("City for search", city)
                                .putExtra("Get radius", radius)
                                .putExtra("Get currentLat", latOfCity)
                                .putExtra("Get currentLng", lngOfCity)
                                .putExtra("Category for search", category);
                        startActivity(intent);

                    }


                } else {
                    Toast.makeText(MainActivity.this, "no internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
