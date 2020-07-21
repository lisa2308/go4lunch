package com.example.go4lunch.service;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.data.api.UserHelper;
import com.example.go4lunch.data.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "restaurantNotification";
    private Context mContext;
    private FirebaseUser connectedUser;
    private String workmatesNames;

    @Override
    public void onReceive(Context context, Intent intent) {
        connectedUser = FirebaseAuth.getInstance().getCurrentUser();

        mContext = context;
        checkIfUserEatsSomewhere();

    }


    private void checkIfUserEatsSomewhere(){
        UserHelper.getUser(connectedUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        String userRestaurantId = task.getResult().getString("restaurantId");
                        String userRestaurantName = task.getResult().getString("restaurantName");

                        if (userRestaurantId != null) {
                            getWorkmates(userRestaurantId,userRestaurantName);
                        }
                    }
                }
            }
        });

    }


    private void  getWorkmates(String restaurantId, String restaurantName){
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (!connectedUser.getUid().equals(document.getId())) {
                            if (restaurantId.equals(user.getRestaurantId())){
                                if(workmatesNames == null){
                                    workmatesNames = user.getUsername();

                                }else {
                                    workmatesNames+= user.getUsername();

                                }
                            }
                        }
                    }
                    createNotification(mContext, restaurantName, workmatesNames);
                }
            }
        });

    }


    private void createNotification(Context context, String restaurantName, String workmatesNames) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("You eat at" + restaurantName)
                .setContentText(getWorkmatesNotifText(workmatesNames))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Lunch informations";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("Receive daily notifications about your lunches");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    public String getWorkmatesNotifText(String workmatesNames) {
        if (workmatesNames == null) {
            return "No Workmate Will Join You";

        } else {
            return "You eat with " + workmatesNames;
        }

    }
}
