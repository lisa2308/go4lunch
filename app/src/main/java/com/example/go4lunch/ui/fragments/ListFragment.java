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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.api.UserHelper;
import com.example.go4lunch.data.models.Restaurant;
import com.example.go4lunch.data.models.User;
import com.example.go4lunch.ui.activites.RestaurantDetailsActivity;
import com.example.go4lunch.ui.adapters.ListAdapter;
import com.example.go4lunch.utils.ApiKeys;
import com.example.go4lunch.utils.PlacesUtils;
import com.example.go4lunch.utils.RecyclerViewHolderListener;
import com.example.go4lunch.utils.SearchableFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment implements SearchableFragment {

    @BindView(R.id.fragment_list_recycler_view)
    RecyclerView recyclerView;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private ListAdapter listAdapter;

    LatLng userLatLng;

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
                Restaurant restaurant = (Restaurant) item;
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
        };

        //AJOUT LISTENER QUAND ON CLICK + MODIF LIST ADAPTER//
        listAdapter = new ListAdapter(getContext(), restaurantList, listener, placesClient);

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

                        if (location != null) {
                            userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            getPlacesRestaurants();
                        } else {
                            Toast.makeText(getContext(), "Nous n'arrivons pas à accéder à votre localisation", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
                getNbWorkmates(place);

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

    private void getNbWorkmates(Place place) {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int nbWorkmates = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (place.getId().equals(user.getRestaurantId())) {
                            nbWorkmates = nbWorkmates + 1;

                        }
                    }
                    addRestaurant(place, nbWorkmates);
                }
            }
        });

    }

    private void addRestaurant(Place place, int nbWorkmates) {
        Restaurant restau = new Restaurant(
                place.getId(),
                place.getName(),
                place.getAddress(),
                PlacesUtils.getHoursFromOpeningHours(place.getOpeningHours(), place.isOpen()),
                PlacesUtils.getDistanceBetweenTwoPoints(userLatLng, place.getLatLng()),nbWorkmates,
                place.getRating(),
                place.getPhotoMetadatas().get(0),
                PlacesUtils.getWebsiteUrl(place.getWebsiteUri()),
                place.getPhoneNumber(),
                place.isOpen()
        );
        restaurantList.add(restau);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void performSearch(Place place) {
        restaurantList.clear();
        getNbWorkmates(place);

    }
}

