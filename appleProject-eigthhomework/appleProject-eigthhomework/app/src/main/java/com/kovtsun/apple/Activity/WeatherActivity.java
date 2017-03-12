package com.kovtsun.apple.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.kovtsun.apple.ApiServices.ApiClient;
import com.kovtsun.apple.ApiServices.ApiInterface;
import com.kovtsun.apple.Interfaces.MyLocationListener;
import com.kovtsun.apple.R;
import com.kovtsun.apple.WeatherGson.Example;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView t1, t2, t3, t4, t5;
    private Toolbar toolbar;
    private final static String key = "4eea53de339c44399f8181049171302";

    private String loginPrefActive = "", passwordPrefActive = "";
    private NavigationView navigationView = null;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView imageView;
    private String myLocation;
    private LocationManager myLocationManager;
    private MyLocationListener locationListener;
    private static Location imHereLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        locationListener = new MyLocationListener();
        myLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        imHereLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        myLocation = imHereLocation.getLatitude() + "," + imHereLocation.getLongitude();

        imageView = (ImageView) findViewById(R.id.iv_from_url);

        toolbar = (Toolbar) findViewById(R.id.toolbar_clean_w);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        t1 = (TextView) findViewById(R.id.txtNameW);
        t2 = (TextView) findViewById(R.id.txtCountry);
        t3 = (TextView) findViewById(R.id.txtLocaltime);
        t4 = (TextView) findViewById(R.id.txtTempC);
        t5 = (TextView) findViewById(R.id.txtTempD);

        navigationView = (NavigationView) findViewById(R.id.navigation_view_weather);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(WeatherActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        new AsynkWeater().execute();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.contact_id){
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
            this.finish();
        }
        if (id==R.id.map_id){
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            this.finish();
        }
        if (id==R.id.logout_id){
            SharedPreferences sharedPrefActive = getSharedPreferences("userInfoActive", Context.MODE_PRIVATE);
            loginPrefActive = sharedPrefActive.getString("username", "");
            passwordPrefActive = sharedPrefActive.getString("password", "");
            if ((loginPrefActive != "")&&(passwordPrefActive != "")){
                SharedPreferences.Editor editorActive = sharedPrefActive.edit();
                editorActive.putString("username", "");
                editorActive.putString("password", "");
                editorActive.apply();

                int duration = Toast.LENGTH_SHORT;
                Toast toast;
                CharSequence textError = getString(R.string.logOut);
                toast = Toast.makeText(this, textError, duration);
                toast.show();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            }
            else {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                this.finish();
            }
        }
        return true;
    }

    public void getData() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Example> call = apiService.getCurrentWeather(key, myLocation);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                int code = response.code();
                Log.i("MyW", String.valueOf(code));
                Example item = response.body();
                t1.setText(item.getLocation().getName());
                t2.setText(item.getLocation().getCountry());
                t3.setText(item.getLocation().getLocaltime().toString());
                t4.setText(item.getCurrent().getTempC().toString() + "°C");
                t5.setText(item.getCurrent().getTempF().toString() + "F");
                String s = response.body().getCurrent().getCondition().getIcon();
                s = "http:"+s;
                Picasso.with(getApplicationContext()).load(s).into(imageView);
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.i("MyW", t.getMessage().toString());
            }
        });
    }

    private class AsynkWeater extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
