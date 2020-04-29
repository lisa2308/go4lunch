package com.example.go4lunch.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.api.UserHelper;
import com.example.go4lunch.data.models.User;
import com.example.go4lunch.ui.adapters.WorkmatesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends Fragment {

    @BindView(R.id.fragment_workmates_recycler_view)
    RecyclerView recyclerView;
    FirebaseUser connectedUser;

    List<User> userList = new ArrayList<>();
    WorkmatesAdapter workmatesAdapter;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_search);
        if(item != null)
            item.setVisible(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, v);

        getActivity().setTitle("Available workmates");
        connectedUser = FirebaseAuth.getInstance().getCurrentUser();
        initRecycler();
        retrieveFirebaseWorkmates();

        return v;
    }

    private void initRecycler() {
        workmatesAdapter = new WorkmatesAdapter(userList, true);

        //ASSOCIATE ADAPTER WITH RECYCLER//
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void updateAdapter() {
        workmatesAdapter.notifyDataSetChanged();
    }

    private void retrieveFirebaseWorkmates() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (!connectedUser.getUid().equals(document.getId())) {
                            userList.add(user);
                        }
                    }
                    updateAdapter();
                }
            }
        });

    }
}
