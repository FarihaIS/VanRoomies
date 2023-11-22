package com.chads.vanroomies;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MatchesTest {
    final static String TAG = "MatchesTest";
    private UiDevice mUiDevice;
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    public void loginHelper() throws Exception {
        // Google Sign-In using UiDevice in conjunction with Espresso for third party popup compatibility
        ViewInteraction id = onView(
                CoreMatchers.allOf(withText("Sign in"),
                        childAtPosition(
                                CoreMatchers.allOf(withId(R.id.login_button),
                                        childAtPosition(
                                                withClassName(CoreMatchers.is("androidx.constraintlayout.widget.ConstraintLayout")),
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

    @Before
    public void before() throws Exception {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }
    @Test
    public void matchesTest() throws Exception {
        UiObject login = mUiDevice.findObject(new UiSelector().textContains("Sign In"));
        if (login.exists()) {
            loginHelper();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Check card deck is not empty

        // Store name on card

        // Swipe left
        ViewInteraction swipeDeck = onView(
                allOf(withId(R.id.matches_swipe_deck),
                        childAtPosition(
                            childAtPosition(withId(R.id.matches_relative_layout), 0),
                                0),
                        isDisplayed()));
        swipeDeck.perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_LEFT, Press.FINGER));

        // For loop to check name is in matches

        // Store name on card

        // Swipe right
        swipeDeck.perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_RIGHT, Press.FINGER));

        // Click chat fragment
//        ViewInteraction bottomNavigationItemView2 = onView(
//                allOf(withId(R.id.menu_chat), withContentDescription("Chat"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.bottomNavigationView),
//                                        0),
//                                3),
//                        isDisplayed()));
//        bottomNavigationItemView2.perform(click());
//
//        // Click chat layout
//        ViewInteraction recyclerView = onView(
//                allOf(withId(R.id.chatlistrecycle),
//                        childAtPosition(
//                                withClassName(is("android.widget.FrameLayout")),
//                                0)));
//        recyclerView.perform(actionOnItemAtPosition(0, click()));
//
//        // Check last message is Hello
//
//        // Click back
//        pressBack();
//
//        // Click Matches fragment
//        ViewInteraction bottomNavigationItemView3 = onView(
//                allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.bottomNavigationView),
//                                        0),
//                                2),
//                        isDisplayed()));
//        bottomNavigationItemView3.perform(click());
//
//        // For loop to check name is not in matches
//
//        // Click chat fragment
//        ViewInteraction bottomNavigationItemView4 = onView(
//                allOf(withId(R.id.menu_chat), withContentDescription("Chat"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.bottomNavigationView),
//                                        0),
//                                3),
//                        isDisplayed()));
//        bottomNavigationItemView4.perform(click());
//
//        // Click unmatch button
//        ViewInteraction appCompatImageButton = onView(
//                allOf(withId(R.id.chat_row_unmatch), withContentDescription("Unmatch with User"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("androidx.cardview.widget.CardView")),
//                                        0),
//                                2),
//                        isDisplayed()));
//        appCompatImageButton.perform(click());
//
//        // Check dialog message is made
//
//        // Click "Yes"
//        ViewInteraction materialButton = onView(
//                allOf(withId(android.R.id.button1), withText("Yes"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.ScrollView")),
//                                        0),
//                                3)));
//        materialButton.perform(scrollTo(), click());
//
//        // Check there is no chat layout with name
//
//        // Click on matches fragment
//        ViewInteraction bottomNavigationItemView5 = onView(
//                allOf(withId(R.id.menu_matches), withContentDescription("Matches"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.bottomNavigationView),
//                                        0),
//                                2),
//                        isDisplayed()));
//        bottomNavigationItemView5.perform(click());

        // Check name is in list of matches again
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

    public static Matcher<View> firstChildOf(final Matcher<View> parentMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with first child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {

                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }
                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(0).equals(view);

            }
        };
    }

}
