package com.kovtsun.apple.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.kovtsun.apple.DBHelper.MarkersDBHelper;
import com.kovtsun.apple.DBTables.Markers;
import com.kovtsun.apple.R;

import java.io.IOException;
import java.util.List;

import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;

public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private String loginPrefActive = "", passwordPrefActive = "";
    private NavigationView navigationView = null;
    private FirebaseAuth mAuth;
    ;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private EditText location_tf;
    private Marker marker;

    private MarkersDBHelper markersDBHelper = null;
    private MarkersDBHelper markersDBHelperDelete = null;
    private List<Markers> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_google);
        if (googleServicesAvailable()) {
            Log.i("TAG", "perfect");
            initMap();
        }
        location_tf = (EditText) findViewById(R.id.TFaddress);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MapsActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

//        markersDBHelper = getHelper(this, MarkersDBHelper.class);
//        RuntimeExceptionDao<Markers, Integer> markersDao = markersDBHelper.getMarkersRuntimeExceptionDao();
//
//        mList = markersDao.queryForAll();
//        if (mList.size() == 0){
//            Log.i("TAG","base 0");
//        }
//        OpenHelperManager.releaseHelper();
    }

    public void onSearch(View view) {
        String location = location_tf.getText().toString();
        List<Address> addressList = null;
        if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            String localy = address.getLocality();
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerOptions options = new MarkerOptions().title(localy).position(latLng);
            marker = mGoogleMap.addMarker(options);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        } else {
            goToLocation(new LatLng(0, 0));
        }
    }

    private void setMarker(String localy, LatLng latLng) {
        if(marker != null){
            marker.remove();
        }
        marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(localy).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (markersDBHelper != null) {
            OpenHelperManager.releaseHelper();
            markersDBHelper = null;
        }
    }

//    private MarkersDBHelper getHelper() {
//        if (markersDBHelper == null) {
//            markersDBHelper = OpenHelperManager.getHelper(this, MarkersDBHelper.class);
//        }
//        return markersDBHelper;
//    }

//    private MarkersDBHelper getHelperDelete() {
//        if (markersDBHelperDelete == null) {
//            markersDBHelperDelete = OpenHelperManager.getHelper(this, MarkersDBHelper.class);
//        }
//        return markersDBHelperDelete;
//    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Log.i("TAG", "bed");
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.markers_info, null);
                TextView loc = (TextView) v.findViewById(R.id.locality);
                loc.setText(marker.getTitle());

                return null;
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        mGoogleMap.setMyLocationEnabled(true);
        String location = "Chernivtsy";
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        String localy = address.getLocality();
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        MarkerOptions options = new MarkerOptions().title(localy).position(latLng).snippet("New location!");
        marker = mGoogleMap.addMarker(options);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//            if (mList.size() != 0){
//                for (Markers m: mList){
//                    double lat = marker.getPosition().latitude;
//                    double lng = marker.getPosition().longitude;
//                    LatLng l = new LatLng(lat, lng);
//                    String t = m.markersTitle.toString();
//                    setMarker(t, l);
//                }
//            }
        if (mGoogleMap != null) {
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
//                    String lat = String.valueOf(marker.getPosition().latitude);
//                    String lng = String.valueOf(marker.getPosition().longitude);for (Markers m: mList){
//                        if ((m.markersLat.equals(lat))&&(m.markersLng.equals(lng))){
////                            try {
////                                Dao<Markers, Integer> markersDao = getHelperDelete().getDao();
////                                DeleteBuilder<Markers, Integer> deleteBuilder = markersDao.deleteBuilder();
////                                deleteBuilder.where().eq("markers_id", m.markersId);
////                                deleteBuilder.delete();
////                            } catch (SQLException e) {
////                                e.printStackTrace();
////
////                            }
//                        }
//                    }
                    marker.remove();
                    return true;
                }
            });
        }

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Chernivtsi"));
//                final Markers markers = new Markers();
                Geocoder geocoder =  new Geocoder(MapsActivity.this);
                LatLng ll = marker.getPosition();
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
//                markers.markersTitle = address.getLocality().toString();
//                markers.markersLat = String.valueOf(ll.latitude);
//                markers.markersLng = String.valueOf(ll.longitude);
////                try{
////                    final Dao<Markers, Integer> fruitDao = getHelper().getDao();
////                    fruitDao.create(markers);
////                }catch (SQLException e){
////                        e.printStackTrace();
////                }
                setMarker(address.getLocality(), ll);
            }
        });
    }

    private void goToLocation(LatLng latLng) {
        CameraUpdate update = CameraUpdateFactory.newLatLng(latLng);
        mGoogleMap.moveCamera(update);
    }

    private void goToLocationZoom(LatLng latLng, float zoom) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.moveCamera(update);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.contact_id) {
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
            this.finish();
        }
        if (id == R.id.weather_id) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            this.finish();
        }
        if (id == R.id.logout_id) {
            SharedPreferences sharedPrefActive = getSharedPreferences("userInfoActive", Context.MODE_PRIVATE);
            loginPrefActive = sharedPrefActive.getString("username", "");
            passwordPrefActive = sharedPrefActive.getString("password", "");
            if ((loginPrefActive != "") && (passwordPrefActive != "")) {
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
            } else {
                mAuth.signOut();
                this.finish();
                //FirebaseAuth.getInstance().signOut();
            }
        }
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            Log.i("TAG", "location null");
        }else{
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 5);
            mGoogleMap.animateCamera(update);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
