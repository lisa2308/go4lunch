package com.example.go4lunch.utils;

import android.location.Location;
import android.net.Uri;

import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class PlacesUtils {

    public static String getWebsiteUrl(Uri uri) {
        if (uri == null) {
            return "";
        } else {
            return uri.toString();
        }
    }

    public static String getHoursFromOpeningHours(OpeningHours openingHours, boolean isOpen) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (isOpen) {
            int hours = openingHours.getPeriods().get(day).getClose().getTime().getHours();
            int minutes = openingHours.getPeriods().get(day).getClose().getTime().getMinutes();
            String minutesStr;
            if (minutes > 9) {
                minutesStr = String.valueOf(minutes);
            } else {
                minutesStr = '0' + String.valueOf(minutes);
            }

            return "Ouvert jusqu'à " + hours + "h" + minutesStr;
        } else {
            return "Fermé";
        }
    }

    public static String getDistanceBetweenTwoPoints(LatLng userPosition, LatLng restaurantPosition) {
        float[] distance = new float[2];
        Location.distanceBetween(
                userPosition.latitude,
                userPosition.longitude,
                restaurantPosition.latitude,
                restaurantPosition.longitude,
                distance
        );

        int metres = (int) distance[0];
        return metres + "m";
    }
}