package com.example.go4lunch.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.R;
import com.example.go4lunch.data.api.UserHelper;
import com.example.go4lunch.data.models.Restaurant;
import com.example.go4lunch.data.models.User;
import com.example.go4lunch.ui.activites.RestaurantDetailsActivity;
import com.example.go4lunch.utils.ApiKeys;
import com.example.go4lunch.utils.PlacesUtils;
import com.example.go4lunch.utils.SearchableFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, SearchableFragment {

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    LatLng userLatLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        initPlacesClient();

        getActivity().setTitle(getContext().getString(R.string.imHungry));
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_googleMap);
        mapFragment.getMapAsync(this);

        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMapWindowClickListener();
        askLocalisationPermission();
    }

    public void setUpMapWindowClickListener(){
        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker){
                Restaurant restaurant = (Restaurant) marker.getTag();
                Intent i = new Intent(getActivity(), RestaurantDetailsActivity.class);
                i.putExtra("placeId", restaurant.getId());
                i.putExtra("imageUrl", restaurant.getPhoto());
                i.putExtra("name", restaurant.getName());
                i.putExtra("rating", restaurant.getRatingBar());
                i.putExtra("address", restaurant.getAddress());
                i.putExtra("phone", restaurant.getPhoneNumber());
                i.putExtra("website", restaurant.getWebsiteUrl());

                startActivity(i);

            }

        });
    }

    public void drawMarker(float latitude, float longitude, Restaurant restaurant, int markerDrawable) {
        Drawable circleDrawable = getResources().getDrawable(markerDrawable);

        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(getMarkerIconFromDrawable(circleDrawable))
                .title(restaurant.getName()));
        m.setTag(restaurant);

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void askLocalisationPermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        requestUserLocalisation();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getContext(), getContext().getString(R.string.authorizationIsRequiredToUseApplication), Toast.LENGTH_LONG).show();

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @SuppressLint("MissingPermission")
    public void requestUserLocalisation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            zoomOnUserLocation(location.getLatitude(), location.getLongitude());
                            getPlacesRestaurants();
                        } else {
                            Toast.makeText(getContext(), getContext().getString(R.string.weCannotAccessYourLocation), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void zoomOnUserLocation(double userLat, double userLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLng), 16.5f));
    }

    public void initPlacesClient() {
        // Initialize the SDK
        Places.initialize(getActivity(), ApiKeys.placesKey);
        placesClient = Places.createClient(getActivity());
    }

    public void getPlacesRestaurants() {

        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.TYPES
        );
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        @SuppressLint("MissingPermission") Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FindCurrentPlaceResponse response = task.getResult();
                List<PlaceLikelihood> placeLikelihoodList = new ArrayList<>();
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    if (placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)){
                        placeLikelihoodList.add(placeLikelihood);
                    }
                }
                getPlacesDetails(placeLikelihoodList);
            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e("ERROR", "Place not found: " + apiException.getStatusCode());
                }
            }
        });
    }

    public void getPlacesDetails(List<PlaceLikelihood> placeLikelihoodList) {

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.WEBSITE_URI,
                Place.Field.PHONE_NUMBER,
                Place.Field.UTC_OFFSET
        );

        for (PlaceLikelihood placeLikelihood : placeLikelihoodList) {
            // Define a Place ID.
            String placeId = placeLikelihood.getPlace().getId();


            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                showRightMarker(place);

            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("ERROR", "Place not found: " + exception.getMessage());
                }
            });
        }
    }

    private void showRightMarker(Place place) {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean foundOnePerson = false;
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (place.getId().equals(user.getRestaurantId())) {
                            foundOnePerson = true;
                            break;
                        }
                    }
                    if (foundOnePerson){
                        drawMarker((float)place.getLatLng().latitude, (float) place.getLatLng().longitude, getRestaurantFromPlace(place), R.drawable.ic_marker_green);
                    } else{
                        drawMarker((float)place.getLatLng().latitude, (float) place.getLatLng().longitude, getRestaurantFromPlace(place), R.drawable.ic_marker_red);
                    }
                }
            }
        });

    }
    private Restaurant getRestaurantFromPlace (Place place) {
        return new Restaurant(
                place.getId(),
                place.getName(),
                place.getAddress(),
                PlacesUtils.getHoursFromOpeningHours(place.getOpeningHours(), place.isOpen()),
                PlacesUtils.getDistanceBetweenTwoPoints(userLatLng, place.getLatLng()),0,
                place.getRating(),
                place.getPhotoMetadatas().get(0),
                PlacesUtils.getWebsiteUrl(place.getWebsiteUri()),
                place.getPhoneNumber(),
                place.isOpen()
        );
    }

    @Override
    public void performSearch(Place place) {
        showRightMarker(place);
        zoomOnUserLocation(place.getLatLng().latitude, place.getLatLng().longitude);
    }
}
