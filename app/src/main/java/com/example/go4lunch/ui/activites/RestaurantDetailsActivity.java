package com.example.go4lunch.ui.activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.go4lunch.R;

import butterknife.ButterKnife;

public class RestaurantDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_details);
        ButterKnife.bind(this);
    }

}
