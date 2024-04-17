package com.example.gourmetcompass;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    Location currentLocation;
    FusedLocationProviderClient fusedClient;

    GoogleMap gMap;
    Marker marker;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get map fragment
        SupportMapFragment mapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit();

        // Get current location
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

//        searchView = findViewById(R.id.search_view);
//        searchView.clearFocus();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String location = searchView.getQuery().toString();
//                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
//                try {
//                    List<Address> addressList = geocoder.getFromLocationName(location, 1);
////                    assert addressList != null;
//                    if (addressList != null && !addressList.isEmpty()) {
//                        Address address = addressList.get(0);
//                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                        if (marker != null) {
//                            marker.remove();
//                        }
//                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
//                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
//                        gMap.animateCamera(cameraUpdate);
//                        marker = gMap.addMarker(markerOptions);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        if (currentLocation != null) {
            updateLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    if (gMap != null) {
                        updateLocation();
                    }
                } else {
                    Log.e("MapsActivity", "Last known location is null");
                }
            }
        });
    }

    private void updateLocation() {
        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        gMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow", Toast.LENGTH_LONG).show();
            }
        }
    }
}