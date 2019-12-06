package com.example.go4lunch.ui.activites;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.ui.fragments.ListFragment;
import com.example.go4lunch.ui.fragments.MapFragment;
import com.example.go4lunch.ui.fragments.WorkmatesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpToolbar();
        setUpBottomNavigationView();
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
                Toast.makeText(this,"Search", Toast.LENGTH_SHORT).show();
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
                replaceFragment(new MapFragment());
                break;
            case R.id.activity_main_bottom_list:
                replaceFragment(new ListFragment());
                break;
            case R.id.activity_main_bottom_workmates:
                replaceFragment(new WorkmatesFragment());
                break;
        }
        return true;
    }


    public void replaceFragment(Fragment fragment) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, fragment, fragment.getClass().getSimpleName());
        //retour vers ancienne vue
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }


}
