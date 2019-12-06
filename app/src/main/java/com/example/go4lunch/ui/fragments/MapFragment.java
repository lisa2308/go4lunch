package com.example.go4lunch.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.go4lunch.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        getActivity().setTitle("I'm Hungry !");
        //onMapReady();
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_googleMap);
        mapFragment.getMapAsync(this);

        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("Hello world"));


    }


}
