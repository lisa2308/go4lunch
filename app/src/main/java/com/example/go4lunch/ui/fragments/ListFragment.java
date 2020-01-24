package com.example.go4lunch.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.models.Restaurant;
import com.example.go4lunch.ui.activites.RestaurantDetailsActivity;
import com.example.go4lunch.ui.adapters.ListAdapter;
import com.example.go4lunch.utils.RecyclerViewHolderListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {

    @BindView(R.id.fragment_list_recycler_view)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);

        getActivity().setTitle("I'm Hungry !");
        initRecycler();


        return v;
    }

    private void initRecycler() {
        Restaurant restau = new Restaurant("le Zinc","140 route de la mairie","open","2m",2,1.5f, "https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg");
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(restau);

        RecyclerViewHolderListener listener = new RecyclerViewHolderListener() {
            @Override
            public void onItemClicked(RecyclerView.ViewHolder viewHolder, Object item, int pos) {
                Intent i = new Intent(getActivity(), RestaurantDetailsActivity.class);
                startActivity(i);
            }
        };

        //AJOUT LISTENER QUAND ON CLICK + MODIF LIST ADAPTER//

        ListAdapter listAdapter = new ListAdapter(restaurantList, listener);

        //ASSOCIATE ADAPTER WITH RECYCLER//
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    }

