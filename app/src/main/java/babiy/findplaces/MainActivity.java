package babiy.findplaces;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import babiy.findplaces.adapter.ImageTextAdapter;
import babiy.findplaces.utils.InternetConnection;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    public String category;
    public String search;
    public String radius;
    private double latOfCity;
    private double lngOfCity;
    private Spinner spinnerSearch;
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

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ImageTextAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category = MainActivity.this.getResources().getStringArray(R.array.categories_array)[position].toLowerCase();
                if (InternetConnection.checkConnection(getApplicationContext())) {

                    Intent intent;
                    if (search.equals("Nearest Places")) {
                        intent = new Intent(MainActivity.this, DisplayOfResultsActivity.class);
                        intent.putExtra("Category for search", category)
                                .putExtra("Get currentLat", currentLat)
                                .putExtra("Get currentLng", currentLng)
                                .putExtra("Get radius", radius);
                        startActivity(intent);
                    } else if (search.equals("Text Search") && lngOfCity != 0) {
                        intent = new Intent(MainActivity.this, DisplayOfResultsActivity.class);
                        intent.putExtra("Get radius", radius)
                                .putExtra("Get currentLat", latOfCity)
                                .putExtra("Get currentLng", lngOfCity)
                                .putExtra("Category for search", category);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "You need write the city", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final TextView tvNumberOfSeekBar = (TextView) findViewById(R.id.tvNumberOfSeekBar);

        final SeekBar seekBarRadius = (SeekBar) findViewById(R.id.seekBar);
        seekBarRadius.setMax(50);
        seekBarRadius.setProgress(1);
        tvNumberOfSeekBar.setText("1 km");

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvNumberOfSeekBar.setText(String.valueOf(seekBar.getProgress())+ "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tvNumberOfSeekBar.setText(String.valueOf(seekBar.getProgress())+ "km");
                radius = String.valueOf(seekBar.getProgress() * 1000);
            }
        });

        tvNameOfRadius = (TextView) findViewById(R.id.tvNameOfRadius);
        spinnerSearch = (Spinner) findViewById(R.id.type_search_spinner);

        currentLat = getIntent().getDoubleExtra("Get currentLat", 0);
        currentLng = getIntent().getDoubleExtra("Get currentLng", 0);

        ArrayAdapter<CharSequence> adapterSearch = ArrayAdapter.createFromResource(this,
                R.array.type_search_array, android.R.layout.simple_spinner_item);
        adapterSearch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearch.setAdapter(adapterSearch);
        spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                search = spinnerSearch.getSelectedItem().toString();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                if (search.equals("Text Search")) {
                    ft.show(autocompleteFragment);
                } else {
                    ft.hide(autocompleteFragment);
                }
                ft.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "You should choose something", Toast.LENGTH_LONG).show();
            }
        });
    }
}
