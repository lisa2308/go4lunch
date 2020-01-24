package com.example.go4lunch.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.models.Restaurant;
import com.example.go4lunch.utils.RecyclerViewHolderListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder>{

    List<Restaurant> restaurantList;
    RecyclerViewHolderListener listener;

    public ListAdapter(List<Restaurant> restaurantList, RecyclerViewHolderListener listener){
        this.restaurantList = restaurantList;
        this.listener = listener;
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
        holder.ratingBar.setRating(restaurant.getRatingBar());
        Picasso.get().load(restaurant.getPhoto()).into(holder.photo);
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

