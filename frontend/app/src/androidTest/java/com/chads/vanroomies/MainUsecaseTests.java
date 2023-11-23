package com.chads.vanroomies;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
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
import static org.junit.Assert.assertTrue;

import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import androidx.test.espresso.Root;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.daprlabs.cardstack.SwipeDeck;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainUsecaseTests {
    final static String TAG = "MainUsecaseTests";
    private UiDevice mUiDevice;
    private ArrayList<String> matches = new ArrayList<>(Arrays.asList("John Man", "Mary Jane", "John Doe", "Kevin Zhang"));

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

    @Test
    public void matchesTest() throws Exception {
        UiObject login = mUiDevice.findObject(new UiSelector().textContains("Sign In"));
        if (login.exists()) {
            loginHelper();
        }

        // Click on matches fragment
        ViewInteraction bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Check card deck is not empty
        ViewInteraction swipeDeck = onView(
                Matchers.allOf(withId(R.id.matches_swipe_deck),
                        childAtPosition(
                                childAtPosition(withId(R.id.matches_relative_layout),
                                        0),
                                0),
                        isDisplayed()));
        swipeDeck.check((view, noViewFoundException) -> {
            if (view instanceof SwipeDeck) {
                int childCount = ((SwipeDeck) view).getChildCount();
                assertTrue(childCount > 0);
            }
        });

        // Swipe left on "John Man"
        swipeDeck.perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_LEFT, Press.FINGER));

        // For loop to swipe left through all other matches
        for (int i = 0; i < matches.size()-1; i++) {
            onView(Matchers.allOf(withId(R.id.matches_name), withText(matches.get(i % 4)),
                    withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                    isDisplayed()));
            swipeDeck.perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_LEFT, Press.FINGER));
        }

        // Swipe right on "John Man" this time
        swipeDeck.perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_RIGHT, Press.FINGER));

        // Click on chat fragment
        bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_chat), withContentDescription("Chat"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Check chat is with "John Man"
        ViewInteraction chatNameTextView = onView(
                Matchers.allOf(withId(R.id.chat_row_name), withText("John Man"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        chatNameTextView.check(matches(withText("John Man")));

        // Click on chat with "John Man"
        ViewInteraction chatRecyclerView = onView(
                Matchers.allOf(withId(R.id.chatlistrecycle),
                        childAtPosition(
                                withClassName(Matchers.is("android.widget.FrameLayout")),
                                0)));
        chatRecyclerView.perform(actionOnItemAtPosition(0, click()));

        // Check last message is "Hello!"
        ViewInteraction chatMessageTextView = onView(
                Matchers.allOf(withId(R.id.chat_me_text), withText("Hello!"),
                        withParent(Matchers.allOf(withId(R.id.chat_me_container),
                                withParent(withId(R.id.chat_me_message)))),
                        isDisplayed()));
        chatMessageTextView.check(matches(withText("Hello!")));

        // Click back
        pressBack();

        // Click on matches fragment
        bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Check card deck is not empty
        swipeDeck = onView(
                Matchers.allOf(withId(R.id.matches_swipe_deck),
                        childAtPosition(
                                childAtPosition(withId(R.id.matches_relative_layout),
                                        0),
                                0),
                        isDisplayed()));
        swipeDeck.check((view, noViewFoundException) -> {
            if (view instanceof SwipeDeck) {
                int childCount = ((SwipeDeck) view).getChildCount();
                assertTrue(childCount > 0);
            }
        });

        // For loop to check that 'John Man' is not in matches
        for (int i = 1; i < matches.size(); i++) {
            ViewInteraction cardNameTextView = onView(Matchers.allOf(withId(R.id.matches_name), withText(matches.get(i % 4)),
                    withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                    isDisplayed()));
            cardNameTextView.check(matches(withText(matches.get(i))));
            swipeDeck.perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_LEFT, Press.FINGER));
        }

        // Click on chat fragment
        bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_chat), withContentDescription("Chat"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Click unmatch button on the chat with "John Man"
        ViewInteraction unmatchButton = onView(
                Matchers.allOf(withId(R.id.chat_row_unmatch), withContentDescription("Unmatch with User"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        unmatchButton.perform(click());

        // Check dialog message is made
        ViewInteraction dialogAlert = onView(
                Matchers.allOf(withId(android.R.id.message), withText("Are you sure you want to unmatch with John Man? You will lose your chat with them permanently."),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        dialogAlert.check(matches(withText("Are you sure you want to unmatch with John Man? You will lose your chat with them permanently.")));

        // Click "Yes"
        ViewInteraction yesButton = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Yes"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.is("android.widget.ScrollView")),
                                        0),
                                3)));
        yesButton.perform(scrollTo(), click());

        // Check there is no chat layout with "John Man"
        ViewInteraction chatFragment = onView(
                Matchers.allOf(withParent(Matchers.allOf(withId(R.id.chatlistrecycle),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        chatFragment.check(doesNotExist());

        // Click on matches fragment
        bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Check card deck is not empty
        swipeDeck = onView(
                Matchers.allOf(withId(R.id.matches_swipe_deck),
                        childAtPosition(
                                childAtPosition(withId(R.id.matches_relative_layout),
                                        0),
                                0),
                        isDisplayed()));
        swipeDeck.check((view, noViewFoundException) -> {
            if (view instanceof SwipeDeck) {
                int childCount = ((SwipeDeck) view).getChildCount();
                assertTrue(childCount > 0);
            }
        });

        // Check that "John Man" is in list of matches again
        onView(Matchers.allOf(withId(R.id.matches_name), withText(matches.get(0)),
                withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                isDisplayed()));
    }

    @Test
    public void chatTest() throws Exception {
        UiObject login = mUiDevice.findObject(new UiSelector().textContains("Sign In"));
        if (login.exists()) {
            loginHelper();
        }

        // Click on matches fragment
        ViewInteraction bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Check card deck is not empty
        ViewInteraction swipeDeck = onView(
                Matchers.allOf(withId(R.id.matches_swipe_deck),
                        childAtPosition(
                                childAtPosition(withId(R.id.matches_relative_layout),
                                        0),
                                0),
                        isDisplayed()));
        swipeDeck.check((view, noViewFoundException) -> {
            if (view instanceof SwipeDeck) {
                int childCount = ((SwipeDeck) view).getChildCount();
                assertTrue(childCount > 0);
            }
        });

        // Swipe right on "John Man"
        swipeDeck.perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_RIGHT, Press.FINGER));

        // Click on chat fragment
        bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_chat), withContentDescription("Chat"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Click on the chat with "John Man"
        ViewInteraction chatRecyclerView = onView(
                Matchers.allOf(withId(R.id.chatlistrecycle),
                        childAtPosition(
                                withClassName(Matchers.is("android.widget.FrameLayout")),
                                0)));
        chatRecyclerView.perform(actionOnItemAtPosition(0, click()));

        // Click on the "Send" button
        ViewInteraction sendButton = onView(
                Matchers.allOf(withId(R.id.chat_send_button), withText("Send"),
                        childAtPosition(
                                Matchers.allOf(withId(R.id.layout_chat_chatbox),
                                        childAtPosition(
                                                withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        sendButton.perform(click());

        // Check for Toast message
        onView(withText(R.string.invalid_input_message)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        // Input "This is a test :)" in chatbox
        ViewInteraction appCompatEditText = onView(
                Matchers.allOf(withId(R.id.edit_chat_message),
                        childAtPosition(
                                Matchers.allOf(withId(R.id.layout_chat_chatbox),
                                        childAtPosition(
                                                withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("This is a test :)"), closeSoftKeyboard());

        // Click on the "Send" button
        sendButton = onView(
                Matchers.allOf(withId(R.id.chat_send_button), withText("Send"),
                        childAtPosition(
                                Matchers.allOf(withId(R.id.layout_chat_chatbox),
                                        childAtPosition(
                                                withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        sendButton.perform(click());

        // Check that the message sent is displayed in the chat channel
        ViewInteraction chatMessage = onView(
                Matchers.allOf(withId(R.id.chat_me_text), withText("This is a test :)"),
                        withParent(Matchers.allOf(withId(R.id.chat_me_container),
                                withParent(withId(R.id.chat_me_message)))),
                        isDisplayed()));
        chatMessage.check(matches(withText("This is a test :)")));

        // Click back
        pressBack();

        // Click on another screen (e.g. profile fragment)
        bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_profile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Click back on the chat fragment
        bottomNavigationItemView = onView(
                Matchers.allOf(withId(R.id.menu_chat), withContentDescription("Chat"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Click on the chat with "John Man"
        chatRecyclerView = onView(
                Matchers.allOf(withId(R.id.chatlistrecycle),
                        childAtPosition(
                                withClassName(Matchers.is("android.widget.FrameLayout")),
                                0)));
        chatRecyclerView.perform(actionOnItemAtPosition(0, click()));

        // Check that the message sent is displayed in the chat channel
        chatMessage = onView(
                Matchers.allOf(withId(R.id.chat_me_text), withText("This is a test :)"),
                        withParent(Matchers.allOf(withId(R.id.chat_me_container),
                                withParent(withId(R.id.chat_me_message)))),
                        isDisplayed()));
        chatMessage.check(matches(withText("This is a test :)")));
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

    private static class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }
    }


}
