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
public class MainUsecaseTests {
    final static String TAG = "MainUsecaseTests";
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

        mUiDevice.waitForIdle(3000);
        UiObject addAccount = mUiDevice.findObject(new UiSelector().text("Add another account"));

        // We need to see if there is already an account registered or not
        if (addAccount.exists()) {
            UiObject account = mUiDevice.findObject(new UiSelector().textContains("@"));
            account.click();
        }
        else {
            Log.d(TAG, "There is no Google account set up! Please refer to M6 testing instructions.");
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

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.menu_listings), withContentDescription("Listings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // We check that the text says "Listings" here so we know that the bottom navigation bar functions properly
        ViewInteraction textView = onView(
                Matchers.allOf(withId(com.google.android.material.R.id.navigation_bar_item_large_label_view), withText("Listings"),
                        withParent(Matchers.allOf(withId(com.google.android.material.R.id.navigation_bar_item_labels_group),
                                withParent(Matchers.allOf(withId(R.id.menu_listings), withContentDescription("Listings"))))),
                        isDisplayed()));
        textView.check(matches(withText("Listings")));

        ViewInteraction toggleListingType = onView(
                allOf(withId(R.id.listings_toggle),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.frame_layout),
                                        0),
                                1),
                        isDisplayed()));
        toggleListingType.perform(click());

        // Creating a listing
        ViewInteraction createListingButton = onView(
                allOf(withId(R.id.createListingButton), withText("+"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.frame_layout),
                                        0),
                                3),
                        isDisplayed()));
        createListingButton.perform(click());

        ViewInteraction createListingName = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        createListingName.perform(replaceText("Studio in Kits"), closeSoftKeyboard());

        ViewInteraction createListingType = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        createListingType.perform(replaceText("studio"), closeSoftKeyboard());

        ViewInteraction createListingDesc = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        createListingDesc.perform(replaceText("This is a lovely and affordable studio right by Kits Beach"), closeSoftKeyboard());

        ViewInteraction createListingPrice = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                3),
                        isDisplayed()));
        createListingPrice.perform(replaceText("1500"), closeSoftKeyboard());

        ViewInteraction createListingPetFriendly = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                4),
                        isDisplayed()));
        createListingPetFriendly.perform(replaceText("N"), closeSoftKeyboard());

        ViewInteraction createListingConfirm = onView(
                allOf(withId(android.R.id.button1), withText("Create")));
        createListingConfirm.perform(scrollTo(), click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.idListingsRV),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        mUiDevice.waitForIdle(3000);

        // Editing the listing
        ViewInteraction editListingTypeButton = onView(withId(R.id.edit_housing_type));
        editListingTypeButton.perform(click());

        // Update Housing Type from Studio -> 1-bedroom
        ViewInteraction editListingType = onView(
                allOf(withText("studio"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editListingType.perform(replaceText("1-bedroom"));

        editListingType = onView(
                allOf(withText("1-bedroom"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editListingType.perform(closeSoftKeyboard());

        ViewInteraction currSaveButton = onView(
                allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        currSaveButton.perform(scrollTo(), click());

        // Update Listing Title from Studio in Kits to 1-Bedroom in Kits
        ViewInteraction editListingTitleButton = onView(
                allOf(withId(R.id.edit_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        editListingTitleButton.perform(click());

        ViewInteraction editListingTitle = onView(
                allOf(withText("Studio in Kits"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editListingTitle.perform(replaceText("1-Bedroom in Kits"));

        editListingTitle = onView(
                allOf(withText("1-Bedroom in Kits"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editListingTitle.perform(closeSoftKeyboard());

        currSaveButton = onView(
                allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        currSaveButton.perform(scrollTo(), click());

        // Update Pet Friendly to Yes
        ViewInteraction editPetFriendlyButton = onView(
                Matchers.allOf(withId(R.id.edit_pet_friendly),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                10),
                        isDisplayed()));
        editPetFriendlyButton.perform(click());

        ViewInteraction editPetFriendlyChoice = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Yes"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("android.widget.ScrollView")),
                                        0),
                                3)));
        editPetFriendlyChoice.perform(scrollTo(), click());

        // Updating Housing Description for 1-bedroom
        ViewInteraction editHousingDescButton = onView(
                Matchers.allOf(withId(R.id.edit_housing_desc),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        editHousingDescButton.perform(click());

        ViewInteraction editHousingDesc = onView(
                Matchers.allOf(withText("This is a lovely and affordable studio right by Kits Beach"),
                        childAtPosition(
                                Matchers.allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(Matchers.is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editHousingDesc.perform(click());

        editHousingDesc = onView(
                Matchers.allOf(withText("This is a lovely and affordable studio right by Kits Beach"),
                        childAtPosition(
                                Matchers.allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(Matchers.is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editHousingDesc.perform(replaceText("This is a lovely and affordable 1-bedroom right by Kits Beach"));

        editHousingDesc = onView(
                Matchers.allOf(withText("This is a lovely and affordable 1-bedroom right by Kits Beach"),
                        childAtPosition(
                                Matchers.allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(Matchers.is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editHousingDesc.perform(closeSoftKeyboard());

        currSaveButton = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("android.widget.ScrollView")),
                                        0),
                                3)));
        currSaveButton.perform(scrollTo(), click());

        // Checking if the posting has been updated immediately on the frontend
        onView(withId(R.id.housing_type))
                .check(matches(withText("1-bedroom")));
        onView(withId(R.id.pet_friendly))
                .check(matches(withText("Pets: Allowed")));
        onView(withId(R.id.listing_name))
                .check(matches(withText("1-Bedroom in Kits")));
        onView(withId(R.id.listing_desc))
                .check(matches(withText("This is a lovely and affordable 1-bedroom right by Kits Beach")));

        // Return to listings fragment
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
