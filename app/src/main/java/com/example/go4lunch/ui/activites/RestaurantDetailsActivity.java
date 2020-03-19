package com.example.go4lunch.ui.activites;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.api.UserHelper;
import com.example.go4lunch.data.models.User;
import com.example.go4lunch.ui.adapters.WorkmatesAdapter;
import com.example.go4lunch.utils.ApiKeys;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RestaurantDetailsActivity extends AppCompatActivity {

    @BindView(R.id.activity_rest_details_image)
    ImageView imageView;

    @BindView(R.id.activity_rest_details_participate)
    FloatingActionButton fabParticipate;

    @BindView(R.id.activity_rest_details_name)
    TextView nameView;

    @BindView(R.id.activity_rest_details_rating)
    RatingBar ratingBarView;

    @BindView(R.id.activity_rest_details_address)
    TextView addressView;

    @BindView(R.id.activity_rest_details_recycler)
    RecyclerView recyclerView;

    String placeId;
    PhotoMetadata imageUrl;
    String name;
    Double rating;
    String address;
    String phone;
    String website;

    FirebaseUser connectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_details);
        ButterKnife.bind(this);
        connectedUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent i = getIntent();

        if (i != null) {
            placeId = i.getStringExtra("placeId");
            imageUrl = i.getParcelableExtra("imageUrl");
            name = i.getStringExtra("name");
            rating = i.getDoubleExtra("rating", 0);
            address = i.getStringExtra("address");
            phone = i.getStringExtra("phone");
            website = i.getStringExtra("website");

            initViews();
        }

        initRecycler();
        checkIfUserParticipateToRestaurant();

        // todo: quand on clique sur le bouton favoris, mettre à jour la base de donnees en donnant au restaurant ID la valeur du restaurant ID

        // todo: iterer sur la liste de tous les utilisateurs et verifier si ils mangent dans ce restaurant (avec le restaurant ID)
        // si oui -> on les ajoute à la liste de workmate puis on rafraichit l'adapteur
        // si non -> on l'ajoute pas
    }

    private void initViews() {
        nameView.setText(name);
        ratingBarView.setRating(5);

        if (rating < 2.3d) {
            ratingBarView.setNumStars(1);
        } else if (rating > 3.6d) {
            ratingBarView.setNumStars(3);
        } else {
            ratingBarView.setNumStars(2);
        }

        addressView.setText(address);

        Places.initialize(this, ApiKeys.placesKey);
        PlacesClient placesClient = Places.createClient(this);

        // Create a FetchPhotoRequest.
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(imageUrl).build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            imageView.setImageBitmap(bitmap);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("ERROR", "Place not found: " + exception.getMessage());
            }
        });

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

    public void checkIfUserParticipateToRestaurant() {

        UserHelper.getUser(connectedUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        String userRestaurantId = task.getResult().getString("restaurantId");
                        if (userRestaurantId.equals(placeId)) {
                            activatePaticipateButton();
                        }
                    }
                }
            }
        });

    }

    public void activatePaticipateButton() {
        fabParticipate.setSelected(true);
//        fabParticipate.setColorFilter(ContextCompat.getColor(this, R.color.quantum_googgreen));
    }

    @OnClick(R.id.activity_rest_details_participate)
    public void clickParticipate(View v) {
        if (v.isSelected()) {
            UserHelper.updateRestaurantId(connectedUser.getUid(), null);
        } else {
            UserHelper.updateRestaurantId(connectedUser.getUid(), placeId);
        }
    }

    @OnClick(R.id.activity_rest_details_call)
    public void clickOnCall() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: " + phone));
        startActivity(intent);
    }

    @OnClick(R.id.activity_rest_details_like)
    public void clickOnLike() {
        String urlString = "https://www.google.com/maps/place/?q=place_id:" + placeId;
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    @OnClick(R.id.activity_rest_details_website)
    public void clickOnWebsite() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(website));
        startActivity(i);
    }
}