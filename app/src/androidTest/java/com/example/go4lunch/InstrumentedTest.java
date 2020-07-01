package com.example.go4lunch;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.go4lunch.ui.activites.MainActivity;
import com.example.go4lunch.ui.fragments.ListFragment;
import com.example.go4lunch.ui.fragments.MapFragment;
import com.example.go4lunch.ui.fragments.WorkmatesFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class InstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void showDialogCurrentRestaurantTest(){
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().showDialogCurrentRestaurant(null);
            }
        });
        onView(withText(activityRule.getActivity().getString(R.string.yourLunch))).check(matches(isDisplayed()));


    }

    @Test
    public void replaceFragmentTest(){
        activityRule.getActivity().replaceFragment(new MapFragment());
        //check if map is displayed
        onView(withId(R.id.fragment_map_root)).check(matches(isDisplayed()));

        activityRule.getActivity().replaceFragment(new ListFragment());
        //check if list is displayed
        onView(withId(R.id.fragment_list_root)).check(matches(isDisplayed()));

        activityRule.getActivity().replaceFragment(new WorkmatesFragment());
        //check if workmates is displayed
        onView(withId(R.id.fragment_workmates_root)).check(matches(isDisplayed()));


    }

    @Test
    public void checkBottomNavigationFragmentReplacementTest() {
        //check if map is displayed
        onView(withId(R.id.fragment_map_root)).check(matches(isDisplayed()));

        //check on list button
        onView(withId(R.id.activity_main_bottom_list)).perform(click());

        //check if list is displayed
        onView(withId(R.id.fragment_list_root)).check(matches(isDisplayed()));

        //check on workmates button
        onView(withId(R.id.activity_main_bottom_workmates)).perform(click());

        //check if workmates is displayed
        onView(withId(R.id.fragment_workmates_root)).check(matches(isDisplayed()));

    }

}
