package com.example.go4lunch.utils;

import android.content.Context;
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

    public static String getHoursFromOpeningHours(OpeningHours openingHours, Boolean isOpen) {
        if (openingHours == null)
            return "non renseigné";
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        day = day -1;

        if (isOpen) {
            int hours;
            int minutes;
            if (openingHours.getPeriods().size()==6){
                day = day -1;
                if (day<0)
                    return "Fermé";
                 hours = openingHours.getPeriods().get(day).getClose().getTime().getHours();
                 minutes = openingHours.getPeriods().get(day).getClose().getTime().getMinutes();
            } else {
                 hours = openingHours.getPeriods().get(day).getClose().getTime().getHours();
                 minutes = openingHours.getPeriods().get(day).getClose().getTime().getMinutes();
            }

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
        if(userPosition==null||restaurantPosition==null)
            return "/";

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
