package com.example.go4lunch.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.models.Restaurant;
import com.example.go4lunch.ui.activites.RestaurantDetailsActivity;
import com.example.go4lunch.ui.adapters.ListAdapter;
import com.example.go4lunch.utils.ApiKeys;
import com.example.go4lunch.utils.PlacesUtils;
import com.example.go4lunch.utils.RecyclerViewHolderListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.AuthUI.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ListFragment extends Fragment {

    @BindView(R.id.fragment_list_recycler_view)
    RecyclerView recyclerView;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private ListAdapter listAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);

        getActivity().setTitle("I'm Hungry !");
        initPlacesClient();
        initRecycler();
        askLocalisationPermission();

        return v;
    }

    private void initRecycler() {
        RecyclerViewHolderListener listener = new RecyclerViewHolderListener() {
            @Override
            public void onItemClicked(RecyclerView.ViewHolder viewHolder, Object item, int pos) {
                Intent i = new Intent(getActivity(), RestaurantDetailsActivity.class);
                startActivity(i);

            }
        };

        //AJOUT LISTENER QUAND ON CLICK + MODIF LIST ADAPTER//

        listAdapter = new ListAdapter(restaurantList, listener, placesClient);

        //ASSOCIATE ADAPTER WITH RECYCLER//
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    public void askLocalisationPermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        requestUserLocalisation();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getContext(), "L'autorisation est obligatoire pour utiliser l'application", Toast.LENGTH_LONG).show();

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
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            initGetPlacesRestaurants(location.getLatitude(),location.getLongitude());
                        } else {
                            Toast.makeText(getContext(), "Nous n'arrivons pas à accéder à votre localisation", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void initPlacesClient () {
        // Initialize the SDK
        Places.initialize(getContext(), ApiKeys.placesKey);

// Create a new Places client instance
        placesClient = Places.createClient(getActivity());
    }

    public void initGetPlacesRestaurants(double userLat, double userLng) {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.TYPES,
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER,
                Place.Field.PHOTO_METADATAS,
                Place.Field.UTC_OFFSET,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.WEBSITE_URI
        );

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.newInstance(placeFields);
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
                    Log.e("Error", "Place not found: " + apiException.getStatusCode());
                }
            }
        });

    }

    public void getPlacesDetails(List<PlaceLikelihood> placeLikelihoodList) {
        for (PlaceLikelihood placeLikelihood : placeLikelihoodList) {
            // Define a Place ID.
            String placeId = placeLikelihood.getPlace().getId();


// Specify the fields to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                Restaurant restau = new Restaurant(
                        place.getId(),
                        place.getName(),
                        place.getAddress(),
                        place.getOpeningHours().toString(),
                       "2m",
                        2,
                        place.getRating(),
                        place.getPhotoMetadatas().get(0),
                        PlacesUtils.getWebSiteUrl(place.getWebsiteUri()),
                        place.getPhoneNumber(),
                        place.isOpen()
                );
                restaurantList.add(restau);
                listAdapter.notifyDataSetChanged();






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
}

