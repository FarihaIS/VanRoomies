package com.chads.vanroomies;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NonFunctionalReqTests {
    final static String TAG = "NonFunctionalReqTests";
    int clicks = 0;
    long currTaskStartTime = 0;
    long currTaskElapsedTime = 0;
    private UiDevice mUiDevice;
    public void nonFuncLoginHelper() throws Exception {
        // Google Sign-In using UiDevice in conjunction with Espresso for third party popup compatibility
        ViewInteraction id = onView(
                allOf(withText("Sign in"),
                        childAtPosition(
                                allOf(withId(R.id.login_button),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        id.perform(click());

        mUiDevice.waitForIdle(3000);
        UiObject addAccount = mUiDevice.findObject(new UiSelector().text("Add another account"));

        // We need to see if there is already an account registered or not
        if (addAccount.exists()) {
            UiObject account = mUiDevice.findObject(new UiSelector().textContains("@"));
            account.click();
            clickReport("Login");
        }
        else {
            Log.d(TAG, "There is no Google account set up! Please refer to M6 testing instructions.");
        }
    }

    public void clickReport(String task) {
        assert clicks <= 3;
        Log.d(TAG, task + ": " + clicks + " click(s)");
        clicks = 0;
    }

    public void timeReport(String task) {
        currTaskElapsedTime = System.currentTimeMillis() - currTaskStartTime;
        assert currTaskElapsedTime <= 5000;
        Log.d(TAG, task + ": " + currTaskElapsedTime + " milliseconds");
    }

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Before
    public void before() {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }
    @Test
    public void maximumClicksTest() throws Exception {
        UiObject login = mUiDevice.findObject(new UiSelector().textContains("Sign In"));
        if (login.exists()) {
            nonFuncLoginHelper();
        }

        ViewInteraction bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_listings), withContentDescription("Listings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
        clicks++;

        ViewInteraction switchMaterial = onView(
                Matchers.allOf(withId(R.id.listings_toggle),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.frame_layout),
                                        0),
                                1),
                        isDisplayed()));
        switchMaterial.perform(click());
        clicks++;

        ViewInteraction recyclerView = onView(
                Matchers.allOf(withId(R.id.idListingsRV),
                        childAtPosition(
                                withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        clicks++;
        clickReport("View a Listing");

        pressBack();

        ViewInteraction bottomNavigationItemView2 = onView(
                Matchers.allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());
        clicks++;
        clickReport("View Matches");

        ViewInteraction bottomNavigationItemView3 = onView(
                Matchers.allOf(withId(R.id.menu_chat), withContentDescription("Chat"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());
        clicks++;

        ViewInteraction recyclerView2 = onView(
                Matchers.allOf(withId(R.id.chatlistrecycle),
                        childAtPosition(
                                withClassName(Matchers.is("android.widget.FrameLayout")),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));
        clicks++;
        clickReport("View a Chat");

        pressBack();

        ViewInteraction bottomNavigationItemView4 = onView(
                Matchers.allOf(withId(R.id.menu_profile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView4.perform(click());
        Log.d(TAG, "Finished Clicks Test");
    }

    @Test
    public void maximumLoadTimeTest() throws Exception {
        UiObject login = mUiDevice.findObject(new UiSelector().textContains("Sign In"));
        if (login.exists()) {
            nonFuncLoginHelper();
        }

        ViewInteraction bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_listings), withContentDescription("Listings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction switchMaterial = onView(
                Matchers.allOf(withId(R.id.listings_toggle),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.frame_layout),
                                        0),
                                1),
                        isDisplayed()));
        switchMaterial.perform(click());

        // Create Listing
        currTaskStartTime = System.currentTimeMillis();
        ViewInteraction materialButton = onView(
                Matchers.allOf(withId(R.id.createListingButton), withText("+"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.frame_layout),
                                        0),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction editText = onView(
                Matchers.allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        editText.perform(replaceText("Time test"), closeSoftKeyboard());

        ViewInteraction editText2 = onView(
                Matchers.allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        editText2.perform(replaceText("studio"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                Matchers.allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        editText3.perform(replaceText("This is for testing runtime"), closeSoftKeyboard());

        ViewInteraction editText4 = onView(
                Matchers.allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                3),
                        isDisplayed()));
        editText4.perform(replaceText("150"), closeSoftKeyboard());

        ViewInteraction editText5 = onView(
                Matchers.allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                4),
                        isDisplayed()));
        editText5.perform(replaceText("Y"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Create"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());
        timeReport("Create Listing");


        // Edit Listing
        currTaskStartTime = System.currentTimeMillis();
        ViewInteraction recyclerView = onView(
                Matchers.allOf(withId(R.id.idListingsRV),
                        childAtPosition(
                                withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction materialButton3 = onView(
                Matchers.allOf(withId(R.id.edit_housing_type),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction materialButton4 = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton4.perform(scrollTo(), click());

        ViewInteraction materialButton5 = onView(
                Matchers.allOf(withId(R.id.edit_pet_friendly),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                10),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction materialButton6 = onView(
                Matchers.allOf(withId(android.R.id.button2), withText("No"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("android.widget.ScrollView")),
                                        0),
                                2)));
        materialButton6.perform(scrollTo(), click());

        ViewInteraction materialButton7 = onView(
                Matchers.allOf(withId(R.id.edit_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        materialButton7.perform(click());

        ViewInteraction materialButton8 = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton8.perform(scrollTo(), click());

        ViewInteraction materialButton9 = onView(
                Matchers.allOf(withId(R.id.edit_housing_desc),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        materialButton9.perform(click());

        ViewInteraction materialButton10 = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton10.perform(scrollTo(), click());
        timeReport("Edit Listing");
        pressBack();

        // See Matches
        currTaskStartTime = System.currentTimeMillis();
        ViewInteraction bottomNavigationItemView2 = onView(
                Matchers.allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());
        timeReport("See Matches");

        // Send a Chat Message
        currTaskStartTime = System.currentTimeMillis();
        ViewInteraction bottomNavigationItemView3 = onView(
                Matchers.allOf(withId(R.id.menu_chat), withContentDescription("Chat"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction recyclerView2 = onView(
                Matchers.allOf(withId(R.id.chatlistrecycle),
                        childAtPosition(
                                withClassName(Matchers.is("android.widget.FrameLayout")),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatEditText = onView(
                Matchers.allOf(withId(R.id.edit_chat_message),
                        childAtPosition(
                                Matchers.allOf(withId(R.id.layout_chat_chatbox),
                                        childAtPosition(
                                                withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Time test!"), closeSoftKeyboard());

        ViewInteraction materialButton11 = onView(
                Matchers.allOf(withId(R.id.chat_send_button), withText("Send"),
                        childAtPosition(
                                Matchers.allOf(withId(R.id.layout_chat_chatbox),
                                        childAtPosition(
                                                withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        materialButton11.perform(click());
        timeReport("Send Message");
        pressBack();
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
