package flowment.com.moviemanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

/**
 * Created by Khaled Reguieg <a href="mailto:Khaled.Reguieg@gmail.com">Khaled Reguieg</a> on 04.08.2015.
 * This class represents the input form for new movies.
 */

public class AddMovieActivity extends AppCompatActivity {

    public static final String TAG = AddMovieActivity.class.getName();

    public Button submitMovieButton;
    public EditText movieTitle;
    public EditText movieDirector;
    public EditText movieActors;
    public EditText movieWatchedOn;
    public EditText movieCity;
    public CheckBox useGPSForCityCheckBox;
    public Location currentLocation;
    public String mCurrentCity;

    LocationManager myLocManager;
    LocationListener myLocListener;

    MovieSQLiteHelper movieSQLiteHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        final String googleMapsApiKey = "AIzaSyDMYrzjSFl2lv_W_viqn9S_TfxxvSIIdCQ";
        final String googleMapsServerKey = "AIzaSyA1QbYUXC-5wD_fmsHVJbLjc_PC2dJBQUA";

        movieSQLiteHelper = new MovieSQLiteHelper(AddMovieActivity.this, "DBMovie", null, 1);

        myLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        myLocListener = new MyLocationListener();
        myLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocListener);

        submitMovieButton = (Button) findViewById(R.id.submitWatchedMovieButton);
        movieTitle = (EditText) findViewById(R.id.movieTitleEditText);
        movieDirector = (EditText) findViewById(R.id.movieDirectorEditText);
        movieActors = (EditText) findViewById(R.id.movieActorsEditText);
        movieWatchedOn = (EditText) findViewById(R.id.movieWatchedOnEditText);
        movieCity = (EditText) findViewById(R.id.movieCityEditText);
        useGPSForCityCheckBox = (CheckBox) findViewById(R.id.locationCheckBox);

        Toast.makeText(AddMovieActivity.this, "Please give me GPS DATA and INTERNET for working properly! :)", Toast.LENGTH_LONG).show();

        if (isNetworkAvailable()) {


            movieTitle.addTextChangedListener(new TextWatcher() {
                Handler handler = new Handler();

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(final Editable editable) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 4000);
                    String arguments = editable.toString().replace(" ", "%25");
                    String omdbURL = "http://www.omdbapi.com/?s=" + arguments;
                    System.out.println("OMDBString= " + omdbURL);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(omdbURL)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {

                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            try {
                                String jsonData = response.body().string();
                                System.out.println(jsonData);
                                if (response.isSuccessful()) {
                                    System.out.println("RESPONSE SUCCESSFULL!");
                                    JSONObject jsonResponse = new JSONObject(jsonData);
                                    System.out.println(jsonResponse.toString());
                                    JSONArray results = jsonResponse.getJSONArray("Search");
                                    System.out.println(results.toString());
                                    String[] movieResults = new String[results.length()];
                                    //ArrayAdapter<String> adapter_helper = new ArrayAdapter<String>(AddMovieActivity.this, android.R.layout.select_dialog_singlechoice);
                                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(AddMovieActivity.this, R.layout.adapter_helper);
                                    for (int i = 0; i < results.length(); i++) {
                                        movieResults[i] = results.getJSONObject(i).getString("Title");
                                        adapter.add(movieResults[i]);
                                        movieResults[i] = "" + i;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DialogPlus dialog = DialogPlus.newDialog(AddMovieActivity.this)
                                                    .setAdapter(adapter)
                                                    .setOnItemClickListener(new OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(final DialogPlus dialog, final Object item, View view, int position) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    movieTitle.setText(item.toString());
                                                                    movieDirector.requestFocus();
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .setGravity(Gravity.CENTER)
                                                    .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                                                    .create();
                                            dialog.show();
                                        }
                                    });
                                } else {
                                    alertUserAboutError();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Exception caught: ", e);
                            } catch (JSONException e) {
                                Log.e(TAG, "Exception caught: ", e);
                            }
                        }
                    });
                }
            });

            useGPSForCityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (currentLocation == null) {
                            useGPSForCityCheckBox.setChecked(false);
                            Toast.makeText(AddMovieActivity.this, "Please give me GPS DATA and INTERNET for working properly! :)", Toast.LENGTH_LONG).show();
                            return;
                        }
                        System.out.println("hello I'm checked!");
                        System.out.println(currentLocation);
                        String geocodeURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&sensor=true&key=" + googleMapsServerKey;//googleMapsApiKey;

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(geocodeURL)
                                .build();
                        Call call = client.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {

                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                try {
                                    String jsonData = response.body().string();
                                    if (response.isSuccessful()) {
                                        System.out.println("RESPONSE SUCCESSFULL!");
                                        mCurrentCity = getCurrentCity(jsonData);
                                        System.out.println("CURRENT CITY= " + mCurrentCity);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                movieCity.setText(mCurrentCity);
                                            }
                                        });
                                    } else {
                                        alertUserAboutError();
                                    }
                                } catch (IOException e) {
                                    Log.e(TAG, "Exception caught: ", e);
                                } catch (JSONException e) {
                                    Log.e(TAG, "Exception caught: ", e);
                                }
                            }
                        });
                    } else {
                        System.out.println("i'm UNchecked!");
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
        }

        submitMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = movieTitle.getText().toString();
                String director = movieDirector.getText().toString();
                String actors = movieActors.getText().toString();
                String watchedOn = movieWatchedOn.getText().toString();
                String city = movieCity.getText().toString();

                db = movieSQLiteHelper.getWritableDatabase();

                if (db != null) {
                    // preparing content values
                    ContentValues newRecord = new ContentValues();
                    newRecord.put("title", title);
                    newRecord.put("director", director);
                    newRecord.put("actors", actors);
                    newRecord.put("date", watchedOn);
                    newRecord.put("city", city);

                    // Insert records
                    db.insert("movie", null, newRecord);
                    db.close();
                }
                makeText(AddMovieActivity.this, "The Movie \"" + title + "\" has been added.", LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), MovieListActivity.class);
                intent.putExtra("activity", "second");
                startActivity(intent);
            }
        });
    }

    private String getCurrentCity(String jsonData) throws JSONException {
        JSONObject jsonResponse = new JSONObject(jsonData);
        System.out.println("JSON_RESPONSE= " + jsonResponse.toString());
        JSONArray results = jsonResponse.getJSONArray("results");
        System.out.println("RESULTS= " + results.toString());
        if (results.length() < 1) {
            Toast.makeText(AddMovieActivity.this, "No Results found, try again!", Toast.LENGTH_SHORT).show();
            return "";
        }
        JSONObject addresses = results.getJSONObject(0);
        System.out.println("ADDRESSES= " + addresses);
        JSONArray addressComponents = addresses.getJSONArray("address_components");
        System.out.println(addressComponents.toString());
        System.out.println(addressComponents.get(4));
        JSONObject city = addressComponents.getJSONObject(4);
        String cityName = city.getString("short_name");
        System.out.println(cityName);
        return cityName;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocationListener();
        myLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLocManager.removeUpdates(myLocListener);
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String coordinates = "Latitude= " + location.getLatitude() + "\nLongitutde= " + location.getLongitude();
            currentLocation = location;
            Toast.makeText(getApplicationContext(), coordinates, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            System.out.println("Status= " + i);
        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
        }
    }
}
