package com.example.go4lunch.data.api;

import com.example.go4lunch.data.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String id, String name, String email, String photo) {
        User userToCreate = new User(name, email, photo);
        return getUsersCollection().document(id).set(userToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String id){
        return UserHelper.getUsersCollection().document(id).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateRestaurantId(String id, String restaurantId) {
        return UserHelper.getUsersCollection().document(id).update("restaurantId", restaurantId);
    }

    public static Task<Void> updateRestaurantName(String id, String restaurantName) {
        return UserHelper.getUsersCollection().document(id).update("restaurantName", restaurantName);
    }
}
