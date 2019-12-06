package com.example.go4lunch.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.User;
import com.example.go4lunch.ui.adapters.WorkmatesAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends Fragment {

    @BindView(R.id.fragment_workmates_recycler_view)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, v);

        getActivity().setTitle("Available workmates");
        initRecycler();

        return v;
    }

    private void initRecycler() {
        User user = new User("Scarlett", "Johanson","sj@gmail.com","https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg");
        List<User> userList = new ArrayList<>();
        userList.add(user);
        WorkmatesAdapter workmatesAdapter = new WorkmatesAdapter(userList);

        //ASSOCIATE ADAPTER WITH RECYCLER//
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}
