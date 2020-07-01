package com.example.go4lunch.ui.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.data.api.UserHelper;
import com.example.go4lunch.ui.fragments.ListFragment;
import com.example.go4lunch.ui.fragments.MapFragment;
import com.example.go4lunch.ui.fragments.WorkmatesFragment;
import com.example.go4lunch.utils.SearchableFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.activity_main_nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_main_bottom_navigation)
    BottomNavigationView bottomNavigationView;

    int AUTOCOMPLETE_REQUEST_CODE = 1;
    SearchableFragment searchableFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpToolbar();
        setUpNavigationDrawer();
        setUpBottomNavigationView();

        MapFragment fragment = new MapFragment();
        searchableFragment = fragment;
        replaceFragment(new MapFragment());

    }

    public void setUpToolbar() {
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorText));
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mNavigationView.setNavigationItemSelectedListener(this);

    }

    public void setToolbarTitle(String title){
        setTitle(title);
    }

    public void setUpNavigationDrawer() {
        CircleImageView imageView = mNavigationView.getHeaderView(0).findViewById(R.id.activity_main_nav_header_circle_image);
        TextView name = mNavigationView.getHeaderView(0).findViewById(R.id.activity_main_nav_header_name);
        TextView email = mNavigationView.getHeaderView(0).findViewById(R.id.activity_main_nav_header_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        Picasso.get().load(user.getPhotoUrl()).into(imageView);
    }

    public void setUpBottomNavigationView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    //Click Listener menu item//
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search:
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

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, placeFields)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setCountry("FR")
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                return true;

            case android.R.id.home:
               drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return true;
        }
    }
//click listener bottom navigation view
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.activity_main_bottom_map:
                MapFragment fragment = new MapFragment();
                searchableFragment = fragment;
                replaceFragment(new MapFragment());
                break;
            case R.id.activity_main_bottom_list:
                ListFragment listFragment = new ListFragment();
                searchableFragment = listFragment;
                replaceFragment(new ListFragment());
                break;
            case R.id.activity_main_bottom_workmates:
                searchableFragment = null;
                replaceFragment(new WorkmatesFragment());
                break;
            case R.id.activity_main_drawer_lunch:
                checkIfUserParticipateToRestaurant();
                break;
            case R.id.activity_main_drawer_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.activity_main_drawer_logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                break;
        }
        return true;
    }

    public void checkIfUserParticipateToRestaurant() {

        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        String restaurantName = task.getResult().getString("restaurantName");
                        //si pas restaurant name
                        if (restaurantName == null) {
                            showDialogCurrentRestaurant(null);
                        } else {
                            showDialogCurrentRestaurant(restaurantName);
                        }
                    }
                }
            }
        });
    }

    public void showDialogCurrentRestaurant(String restaurantName) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.yourLunch));
        if (restaurantName == null) {
            alertDialog.setMessage(getString(R.string.didntSelectRestaurant));
        } else {
            alertDialog.setMessage(getString(R.string.youEatAt) + " " + restaurantName);
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (dialog, which) -> dialog.dismiss());
        alertDialog.show();

    }


    public void replaceFragment(Fragment fragment) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, fragment, fragment.getClass().getSimpleName());
        //retour vers ancienne vue
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if(searchableFragment !=null);
                searchableFragment.performSearch(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }


    }
