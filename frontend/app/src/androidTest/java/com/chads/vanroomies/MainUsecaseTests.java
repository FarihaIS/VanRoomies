package com.chads.vanroomies;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

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
public class MainUsecaseTests {
    private UiDevice mUiDevice;
    public void loginHelper() throws Exception {
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

        UiObject addAccount = mUiDevice.findObject(new UiSelector().text("Add another account"));

        // We need to see if there is already an account registered or not
        if (addAccount.exists()) {
            UiObject account = mUiDevice.findObject(new UiSelector().textContains("@"));
            account.click();
        }
        else {
//            mUiDevice.waitForIdle(7000);
            UiObject loginText = mUiDevice.findObject(new UiSelector().className("android.widget.EditText"));
            loginText.setText(Constants.testEmail);
            mUiDevice.waitForIdle(1000);
            UiObject nextButton = mUiDevice.findObject(new UiSelector().textContains("Next"));
            nextButton.click();
            mUiDevice.waitForWindowUpdate(null, 7000);
            UiObject passwordText = mUiDevice.findObject(new UiSelector().className("android.widget.EditText"));
            passwordText.setText(Constants.testPassword);
            mUiDevice.waitForIdle(2000);
            nextButton = mUiDevice.findObject(new UiSelector().textContains("Next"));
            nextButton.click();

            mUiDevice.waitForWindowUpdate(null, 3000);

            // Security-related optional prompts
            UiObject skipButton = mUiDevice.findObject(new UiSelector().textContains("Skip"));
            if (skipButton.exists()){
                skipButton.click();
                mUiDevice.waitForWindowUpdate(null, 4000);
            }

            UiObject dontTurnOnButton = mUiDevice.findObject(new UiSelector().textContains("DON'T TURN ON"));
            if (dontTurnOnButton.exists()){
                dontTurnOnButton.click();
                mUiDevice.waitForWindowUpdate(null, 4000);
            }

            UiObject agreeButton = mUiDevice.findObject(new UiSelector().textContains("agree"));
            if (agreeButton.exists()){
                agreeButton.click();
                mUiDevice.waitForWindowUpdate(null, 4000);
            }
        }
    }

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Before
    public void before() throws Exception {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }
    @Test
    public void manageListingTest() throws Exception {
        UiObject login = mUiDevice.findObject(new UiSelector().textContains("Sign In"));
        if (login.exists()) {
            loginHelper();
        }

        UiObject googlePopup = mUiDevice.findObject(new UiSelector().text("Mphi Gamez (MphiGaming)"));
        googlePopup.click();

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.menu_listings), withContentDescription("Listings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // We check that the text says "Listings" here so we konw that the bottom navigation bar functions properly
        ViewInteraction textView = onView(
                Matchers.allOf(withId(com.google.android.material.R.id.navigation_bar_item_large_label_view), withText("Listings"),
                        withParent(Matchers.allOf(withId(com.google.android.material.R.id.navigation_bar_item_labels_group),
                                withParent(Matchers.allOf(withId(R.id.menu_listings), withContentDescription("Listings"))))),
                        isDisplayed()));
        textView.check(matches(withText("Listings")));

        ViewInteraction switchMaterial = onView(
                allOf(withId(R.id.listings_toggle),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.frame_layout),
                                        0),
                                1),
                        isDisplayed()));
        switchMaterial.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.createListingButton), withText("+"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.frame_layout),
                                        0),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction editText = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        editText.perform(replaceText("Studio in Kits"), closeSoftKeyboard());

        ViewInteraction editText2 = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        editText2.perform(replaceText("studio"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        editText3.perform(replaceText("This is a lovely and affordable studio right by Kits Beach"), closeSoftKeyboard());

        ViewInteraction editText4 = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                3),
                        isDisplayed()));
        editText4.perform(replaceText("1500"), closeSoftKeyboard());

        ViewInteraction editText5 = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                4),
                        isDisplayed()));
        editText5.perform(replaceText("N"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Create"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.idListingsRV),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(2, click()));

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.edit_housing_type),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction editText6 = onView(
                allOf(withText("studio"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editText6.perform(replaceText("1-bedroom"));

        ViewInteraction editText7 = onView(
                allOf(withText("1-bedroom"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editText7.perform(closeSoftKeyboard());

        ViewInteraction materialButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton4.perform(scrollTo(), click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.edit_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction editText8 = onView(
                allOf(withText("Studio in Kits"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editText8.perform(replaceText("Studio in Kitsilano"));

        ViewInteraction editText9 = onView(
                allOf(withText("Studio in Kitsilano"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editText9.perform(closeSoftKeyboard());

        ViewInteraction materialButton6 = onView(
                allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton6.perform(scrollTo(), click());

        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.edit_housing_type),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        materialButton7.perform(click());

        ViewInteraction editText10 = onView(
                allOf(withText("studio"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editText10.perform(replaceText("invalid"));

        ViewInteraction editText11 = onView(
                allOf(withText("invalid"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editText11.perform(closeSoftKeyboard());

        ViewInteraction materialButton8 = onView(
                allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton8.perform(scrollTo(), click());

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
