package com.example.go4lunch.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesHolder>{

    List<User> userList;
    boolean isWorkmateView;
    Context context;

    public WorkmatesAdapter(Context context, List<User> userList, boolean isWorkmateView){
        this.userList = userList;
        this.isWorkmateView = isWorkmateView;
        this.context = context;
    }


    public static class WorkmatesHolder extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView textView;

        public WorkmatesHolder (View view) {
            super(view);
            imageView = view.findViewById(R.id.fragment_workmates_item_image);
            textView = view.findViewById(R.id.fragment_workmates_item_text);
        }
    }

    @Override
    public WorkmatesHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmates_item,parent,false);
        return new WorkmatesHolder(itemView);
    }

    //passe autant de fois qu'il y' a d'item//
    @Override
    public void onBindViewHolder(final WorkmatesHolder holder, final int position) {

        //position liée à la ligne donc change toute seule//
        final User user = userList.get(position);
        if (user.getRestaurantId() == null) {
            holder.textView.setText(user.getUsername() + " " +context.getString(R.string.hasntDecidedYet));
            holder.textView.setTextColor(Color.GRAY);
            holder.textView.setTypeface(null, Typeface.ITALIC);

        }
        else {
            if (isWorkmateView) {
            holder.textView.setText(user.getUsername() + " " + context.getString(R.string.isEatingAt) + user.getRestaurantName());
            holder.textView.setTextColor(Color.BLACK);
            holder.textView.setTypeface(null, Typeface.NORMAL);

        }else {
                holder.textView.setText(user.getUsername() + " " + context.getString(R.string.isJoining));
                holder.textView.setTextColor(Color.BLACK);
                holder.textView.setTypeface(null, Typeface.NORMAL);
            }

            }
        Picasso.get().load(user.getPhoto()).into(holder.imageView);
    }

    //Picasso.get().load("https://www.nytimes.com/" + doc.getMultimedia().get(0).getUrl()).into(holder.image);
    //        }

    //ITEM'S NUMBER//
    @Override
    public int getItemCount(){
        return userList.size();
    }
}

