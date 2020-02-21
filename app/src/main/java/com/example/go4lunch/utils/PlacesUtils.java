package com.example.go4lunch.utils;

import android.net.Uri;

public class PlacesUtils {

    public static String getWebSiteUrl(Uri uri) {
        if (uri == null) {
            return "";
        } else {
            return uri.toString();
        }

    }
}
