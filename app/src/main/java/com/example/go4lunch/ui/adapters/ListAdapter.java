package com.example.go4lunch.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.models.Restaurant;
import com.example.go4lunch.utils.RecyclerViewHolderListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder>{

    Context context;
    List<Restaurant> restaurantList;
    RecyclerViewHolderListener listener;
    PlacesClient placesClient;

    public ListAdapter(Context context,
                       List<Restaurant> restaurantList,
                       RecyclerViewHolderListener listener,
                       PlacesClient placesClient){
        this.context = context;
        this.restaurantList = restaurantList;
        this.listener = listener;
        this.placesClient = placesClient;
    }

    public static class ListHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.fragment_list_item_title) TextView name;
        @BindView(R.id.fragment_list_item_address) TextView address;
        @BindView(R.id.fragment_list_item_openingHours) TextView openingHours;
        @BindView(R.id.fragment_list_item_distance) TextView distance;
        @BindView(R.id.fragment_list_item_nb_workmates) TextView workmates;
        @BindView(R.id.fragment_list_item_ratingBar) RatingBar ratingBar;
        @BindView(R.id.fragment_list_item_photo) ImageView photo;

        public ListHolder (View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ListHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_item,parent,false);
        return new ListHolder(itemView);
    }

    //passe autant de fois qu'il y' a d'item//
    @Override
    public void onBindViewHolder(final ListHolder holder, final int position) {

        //position liée à la ligne donc change toute seule//
        final Restaurant restaurant = restaurantList.get(position);

        holder.name.setText(restaurant.getName());
        holder.address.setText(restaurant.getAddress());
        holder.openingHours.setText(restaurant.getOpeningHours());
        holder.distance.setText(restaurant.getDistance());
        holder.workmates.setText('(' + String.valueOf(restaurant.getWorkmates()) + ')');
        holder.ratingBar.setRating(5);

        if (restaurant.getRatingBar() == null) {
            holder.ratingBar.setNumStars(1);

        }else {
            if (restaurant.getRatingBar() < 2.3d) {
                holder.ratingBar.setNumStars(1);
            } else if (restaurant.getRatingBar() > 3.6d) {
                holder.ratingBar.setNumStars(3);
            } else {
                holder.ratingBar.setNumStars(2);
            }
        }

        if (restaurant.isOpen() == null){
            holder.openingHours.setTextColor(ContextCompat.getColor(context, R.color.gris));
            holder.openingHours.setTypeface(null, Typeface.ITALIC);
        }else {
            if (restaurant.isOpen()) {
                holder.openingHours.setTextColor(ContextCompat.getColor(context, R.color.gris));
                holder.openingHours.setTypeface(null, Typeface.ITALIC);
            } else {
                holder.openingHours.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                holder.openingHours.setTypeface(null, Typeface.BOLD);
            }
        }

        // Create a FetchPhotoRequest.
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(restaurant.getPhoto()).build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            holder.photo.setImageBitmap(bitmap);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("ERROR", "Place not found: " + exception.getMessage());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(holder,restaurant,position);
            }
        });

    }

    //Picasso.get().load("https://www.nytimes.com/" + doc.getMultimedia().get(0).getUrl()).into(holder.image);
    //        }

    //ITEM'S NUMBER//
    @Override
    public int getItemCount(){
        return restaurantList.size();
    }
}

