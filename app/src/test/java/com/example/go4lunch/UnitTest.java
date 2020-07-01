package com.example.go4lunch;

import android.net.Uri;

import com.example.go4lunch.utils.PlacesUtils;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class UnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getWebsiteUrlTest(){
        Uri actual = null;
        String expected = "";
        assertEquals(expected, PlacesUtils.getWebsiteUrl(actual));

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.myawesomesite.com")
                .appendPath("turtles")
                .appendPath("types")
                .appendQueryParameter("type", "1")
                .appendQueryParameter("sort", "relevance")
                .fragment("section-name");
        expected = "https://www.myawesomesite.com/turtles/types?type=1&sort=relevance#section-name";
        assertEquals(expected, PlacesUtils.getWebsiteUrl(builder.build()));
    }
    @Test
    public void getDistanceBetweenTwoPointsTest() {
        LatLng annecy = new LatLng(45.899780, 6.128350);
        LatLng annemasse = new LatLng(46.194172, 6.238880);

        String actual = PlacesUtils.getDistanceBetweenTwoPoints(annecy, annemasse);
        String expected = "33822m";

        assertEquals(expected, actual);
    }

}