package com.example.vacineaqui;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng center = new LatLng(-12.9704, -38.465987656789);
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom((float) 11.5).target(center).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        LatLng p1 = new LatLng(-12.979091651458296, -38.50843313287705);
        LatLng p2 = new LatLng(-12.966330006283034, -38.414380385076704);
        LatLng p3 = new LatLng(-12.986981583564294, -38.48173967015339);
        mMap.addMarker(new MarkerOptions().position(p1).title("UBS Ramiro De Azevedo"));
        mMap.addMarker(new MarkerOptions().position(p2).title("Unidade de Saúde da Familia Parque Pituaçu / Posto de Vacinação"));
        mMap.addMarker(new MarkerOptions().position(p3).title("UBS Manoel Vitorino"));
        mMap.setMinZoomPreference((float) 11.5);
    }

    public void gerarRota(View view) {
        LatLng center = new LatLng(-12.9704, -38.465987656789);
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom((float) 11.5).target(center).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}