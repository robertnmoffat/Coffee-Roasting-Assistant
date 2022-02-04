package com.example.roastingassistant;

import android.text.Html;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.roastingassistant.user_interface.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import Database.Roast;

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
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withTagKey;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class UIEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void swipeFragmentLeft(){
        Log.i("Test UIT01", "Testing swiping fragment left...");
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.fragment_button_1)).check(matches(isDisplayed()));
    }

    @Test
    public void swipeFragmentRight(){
        Log.i("Test UIT02", "Testing swiping fragment right...");
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.fragment_button_0)).check(matches(isDisplayed()));
    }

    @Test
    public void scrollUp(){
        Log.i("Test UIT03", "Testing swiping fragment up...");
        onView(withId(R.id.view_pager)).perform(swipeUp());
    }

    @Test
    public void scrollDown(){
        Log.i("Test UIT04", "Testing swiping fragment down...");
        onView(withId(R.id.view_pager)).perform(swipeUp());
        onView(withId(R.id.view_pager)).perform(swipeDown());
    }

    @Test
    public void openMenuDropdown(){
        Log.i("Test UIT05", "Testing opening dropdown settings menu...");
        onView(withId(R.id.menuButton)).perform(click());
    }

    @Test
    public void createNewBeanEntry() throws InterruptedException {
        Log.i("Test UIT06", "Testing filling out and adding new bean entry...");
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        Thread.sleep(1000);
        onView(withId(R.id.fragment_button_1)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.beanactivity_name_edittext)).perform(click()).perform(typeText("Colombian Supremo"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_origin_edittext)).perform(click()).perform(typeText("Colombia"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_drymethod_edittext)).perform(click()).perform(typeText("Sun"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_flavour_edittext)).perform(click()).perform(typeText("chocolate, honey, nutty"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_process_edittext)).perform(click()).perform(typeText("natural"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_farm_edittext)).perform(click()).perform(typeText("Anei S.N."), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_add_button)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void createNewRoastEntry() throws InterruptedException {
        Log.i("Test UIT07", "Testing filling out and adding new roast entry...");
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        Thread.sleep(1000);
        onView(withId(R.id.fragment_button_1)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.beanactivity_name_edittext)).perform(click()).perform(typeText("Colombian Supremo"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_origin_edittext)).perform(click()).perform(typeText("Colombia"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_drymethod_edittext)).perform(click()).perform(typeText("Sun"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_flavour_edittext)).perform(click()).perform(typeText("chocolate, honey, nutty"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_process_edittext)).perform(click()).perform(typeText("natural"), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_farm_edittext)).perform(click()).perform(typeText("Anei S.N."), closeSoftKeyboard());
        onView(withId(R.id.beanactivity_add_button)).perform(click());
        Thread.sleep(1000);

        //Swipt right
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.fragment_button_0)).check(matches(isDisplayed()));
        Thread.sleep(1000);

        onView(withId(R.id.fragment_button_0)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.roastparamactivity_name_edittext)).perform(click()).perform(typeText("Med Colombian"),closeSoftKeyboard());
        onView(withId(R.id.roastparamactivity_bean_spinner)).perform(click());
        Thread.sleep(1000);
        //onView(allOf(withId(R.id.roastparamactivity_bean_spinner), withText("Colombian Supremo"))).perform(click());
        //onData(allOf(is(instanceOf(String.class)), is("Colombian Supremo"))).perform(click());
        onView(withId(R.id.roastparamactivity_roastlevel_edittext)).perform(click()).perform(typeText("Medium"),closeSoftKeyboard());
        onView(withId(R.id.roastparamactivity_droptemp_edittext)).perform(click()).perform(typeText("430"),closeSoftKeyboard());
        onView(withId(R.id.roastparamactivity_scrollview)).perform(swipeUp());
        Thread.sleep(500);
        onView(withId(R.id.roastparamactivity_add_checkpoint_button)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.checkpoint_name_edittext)).perform(click()).perform(typeText("Drop time"),closeSoftKeyboard());
        onView(withId(R.id.checkpoint_trigger_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Temperature"))).perform(click());
        onView(withId(R.id.checkpoint_temp_edittext)).perform(click()).perform(typeText("430"),closeSoftKeyboard());
        onView(withId(R.id.checkpoint_add_button)).perform(click());

        onView(withId(R.id.roastparamactivity_add_button)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void createNewBlendEntry() throws InterruptedException {
        Log.i("Test UIT08", "Testing filling out and adding new blend entry...");
        onView(withId(R.id.view_pager)).perform(swipeLeft()).perform(swipeLeft());
        Thread.sleep(1000);
        onView(withId(R.id.fragment_button_2)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.blend_name_edittext)).perform(click()).perform(typeText("Blend"), closeSoftKeyboard());
        onView(withId(R.id.roast_for_blend_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Roast"))).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.blend_addroast_button)).perform(click());
        onView(withId(R.id.blend_add_button)).perform(click());
    }

    @Test
    public void testDataBrowser() throws InterruptedException {
        Log.i("Test UIT09", "Testing opening browser and downloading roast...");
        onView(withId(R.id.view_pager)).perform(swipeUp());
        onView(withId(R.id.roast_remotedata_button)).perform(click());
        Thread.sleep(1000);
        onView(withText("ROAST: SUMATRA")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.roastparamactivity_add_button));
        Thread.sleep(1000);
        onView(withId(R.id.databrowser_done_button));
    }

    @Test
    public void testOpenRoastingActivity() throws InterruptedException {
        Log.i("Test UIT10", "Testing opening roast activity...");
        Thread.sleep(1000);
        onView(withId(R.id.test_roast_id)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.roastparamactivity_startroast_button)).perform(click());
        Thread.sleep(1000);

    }

    @Test
    public void testStartAndStopRoast() throws InterruptedException {
        testOpenRoastingActivity();
        String initialTime = "Time:0.0";
        onView(withId(R.id.roastactivity_time_textview)).check(matches(withText(initialTime)));
        onView(withId(R.id.roastactivity_checkpoint_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.roastactivity_time_textview)).check(matches(not(withText(initialTime))));

    }
}
