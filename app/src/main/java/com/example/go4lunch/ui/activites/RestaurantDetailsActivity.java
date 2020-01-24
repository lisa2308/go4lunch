package com.example.go4lunch.ui.activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.models.User;
import com.example.go4lunch.ui.adapters.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantDetailsActivity extends AppCompatActivity {

    @BindView(R.id.fragment_workmates_recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_details);
        ButterKnife.bind(this);

        //getSupportActionBar().hide();

        initRecycler();

    }

    private void initRecycler() {
        User user = new User("Scarlett Johanson", "sj@gmail.com", "https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg");
        List<User> userList = new ArrayList<>();
        userList.add(user);
        WorkmatesAdapter workmatesAdapter = new WorkmatesAdapter(userList);

        //ASSOCIATE ADAPTER WITH RECYCLER//
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}