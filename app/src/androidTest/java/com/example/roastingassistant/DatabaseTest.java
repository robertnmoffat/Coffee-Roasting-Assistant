package com.example.roastingassistant;

import android.app.Instrumentation;
import android.content.Context;
import android.provider.ContactsContract;
import android.test.InstrumentationTestRunner;
import android.test.ProviderTestCase2;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.roastingassistant.user_interface.MainActivity;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import Database.Bean;
import Database.DatabaseHelper;
import Database.DatabaseTestActivity;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    @Rule
    public ActivityScenarioRule<DatabaseTestActivity> activityRule
            = new ActivityScenarioRule<>(DatabaseTestActivity.class);


    @Test
   public void DBT01() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.DBT01button)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void DBT02() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.DBT02button)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void DBT03() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.DBT03button)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void DBT04() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.DBT04button)).perform(click());
        Thread.sleep(1000);
    }

    public void DBT05() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.DBT05button)).perform(click());
        Thread.sleep(1000);
    }


}
