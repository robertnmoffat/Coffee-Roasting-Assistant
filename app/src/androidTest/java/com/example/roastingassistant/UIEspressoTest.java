package com.example.roastingassistant;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.roastingassistant.user_interface.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UIEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void swipeFragmentLeft(){
        Log.i("UIT01", "Testing swiping fragment left...");
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.fragment_button_1)).check(matches(isDisplayed()));
    }

    @Test
    public void swipeFragmentRight(){
        Log.i("UIT02", "Testing swiping fragment right...");
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.fragment_button_0)).check(matches(isDisplayed()));
    }

    @Test
    public void scrollUp(){
        Log.i("UIT03", "Testing swiping fragment up...");
        onView(withId(R.id.view_pager)).perform(swipeUp());
    }

    @Test
    public void scrollDown(){
        Log.i("UIT04", "Testing swiping fragment down...");
        onView(withId(R.id.view_pager)).perform(swipeUp());
        onView(withId(R.id.view_pager)).perform(swipeDown());
    }

    @Test
    public void openMenuDropdown(){
        Log.i("UIT05", "Testing opening dropdown settings menu...");
        onView(withId(R.id.menuSpinner)).perform(click());
    }
}
