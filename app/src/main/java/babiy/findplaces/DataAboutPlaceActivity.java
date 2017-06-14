package babiy.findplaces;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import babiy.findplaces.utils.Keys;
import babiy.findplaces.utils.Utils;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class DataAboutPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String MAIN_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private static final String API_KEY = "AIzaSyDaX8zPnlWyMPed3SgdjuSzmU8JzZwQ-es";

    RequestQueue requestQueue;

    SupportMapFragment myGoogleMap;

    double latOfPlace;
    double lngOfPlace;

    TextView tvName;
    TextView tvAddress;
    TextView tvPhone;
    TextView tvWebsite;
    TextView tvOpening;
    TextView tvRating;
    TextView tvHours;

    Button btnNavigate;
    double lat;
    double lng;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_about_place);

        requestQueue = Volley.newRequestQueue(this);
        language = Utils.getLocale();

        String idOfPlace = getIntent().getStringExtra("place_id");
        latOfPlace = (double) getIntent().getFloatExtra("get latOfPlace", 0);
        lngOfPlace = (double) getIntent().getFloatExtra("get lngOfPlace", 0);

        getDetailInformation(idOfPlace);

        myGoogleMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapOfPlace);
        myGoogleMap.getMapAsync(this);

        tvName = (TextView) findViewById(R.id.tvNameOfPlace);
        tvAddress = (TextView) findViewById(R.id.tvAddressOfPlace);
        tvPhone = (TextView) findViewById(R.id.tvPhoneOfPlace);
        tvWebsite = (TextView) findViewById(R.id.tvWebsiteOfPlace);
        tvOpening = (TextView) findViewById(R.id.tvOpeningOgPlace);
        tvRating = (TextView) findViewById(R.id.tvRatingOfPlace);
        tvHours = (TextView) findViewById(R.id.tvHoursOfPlace);
        if (tvWebsite.getText() != getString(R.string.website_available)) {
            tvWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(String.valueOf(tvWebsite.getText())));
                    startActivity(intent);
                }
            });
        }

        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_detail_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_navigate:
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + latOfPlace + "," + lngOfPlace + "&mode=w"));
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getPackageManager()) == null) {
                    intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + lat + "," + lng + "&daddr=" + latOfPlace + "," + lngOfPlace));
                }
                startActivity(intent);
                break;
            case R.id.menu_call:
                if (tvPhone.getText() != getString(R.string.phone_not_available)) {
                    intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                            "tel", String.valueOf(tvPhone.getText()), null));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getString(R.string.phone_not_available), Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public void getDetailInformation(String id) {
        String url;

        url = MAIN_URL + id + "&language=" + language + "&key=" + API_KEY;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonObject = response.getJSONObject(Keys.KEY_DETAIL_RESULT);

                            String name = response.getJSONObject(Keys.KEY_DETAIL_RESULT).getString(Keys.KEY_NAME);
                            String phoneNumber = getString(R.string.phone_not_available);
                            if (response.toString().contains(Keys.KEY_PHONE_NUMBER)) {
                                phoneNumber = jsonObject.getString(Keys.KEY_PHONE_NUMBER);
                            }
                            String address = jsonObject.getString(Keys.KEY_ADDRESS);
                            String website = getString(R.string.website_available);
                            if (response.toString().contains(Keys.KEY_WEBSITE)) {
                                website = jsonObject.getString(Keys.KEY_WEBSITE);
                            }
                            String opening = "";
                            if (response.toString().contains(Keys.KEY_OPENING)) {
                                opening = jsonObject.getJSONObject(Keys.KEY_OPENING_HOURS)
                                        .getString(Keys.KEY_OPENING);
                            }
                            String rating = "0";
                            if (response.toString().contains(Keys.KEY_RATING)) {
                                rating = jsonObject.getString(Keys.KEY_RATING);
                            }
                            String[] weekDayHours = new String[7];
                            if (response.toString().contains(Keys.KEY_WEEK_DAY_HOURS)) {
                                for (int i = 0; i < 7; i++) {
                                    weekDayHours[i] = jsonObject.getJSONObject(Keys.KEY_OPENING_HOURS)
                                            .getJSONArray(Keys.KEY_WEEK_DAY_HOURS)
                                            .getString(i);
                                }
                            }

                            StringBuilder sb = new StringBuilder();
                            for (String s : weekDayHours) {
                                sb.append(s + '\n');
                            }
                            String hoursOfPlace = sb.toString();
                            if (hoursOfPlace.contains("null")) {
                                hoursOfPlace = getString(R.string.working_hours);
                            }
                            tvName.setText(name);
                            tvAddress.setText(address);
                            tvPhone.setText(phoneNumber);
                            tvRating.setText(rating);
                            tvWebsite.setText(website);
                            if (opening.equals("true")) {
                                tvOpening.setText(getString(R.string.string_open));
                                tvOpening.setTextColor(Color.GREEN);
                            } else {
                                tvOpening.setText(getString(R.string.string_close));
                                tvOpening.setTextColor(Color.RED);
                            }
                            tvHours.setText(hoursOfPlace);

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latOfPlace, lngOfPlace)).title(getString(R.string.here)));
        CameraPosition liberty = CameraPosition.builder().target(new LatLng(latOfPlace, lngOfPlace)).zoom(15).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(liberty));

    }
}

