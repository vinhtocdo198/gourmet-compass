package com.example.gourmetcompass;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddReviewTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addReviewTest() throws InterruptedException {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.account_fragment), withContentDescription("Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_text_field_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.email_log_in),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("vinh180902@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_text_field_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.password_log_in),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("vinh180902"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_log_in_mid), withText("Log In"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.main_frame_layout),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        Thread.sleep(2000);
        ViewInteraction bottomNavigationItemView1 = onView(
                allOf(withId(R.id.home_fragment), withContentDescription("Home"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView1.perform(click());

        onView(withId(R.id.first_scroll)).perform(actionOnItemAtPosition(0, click()));

        Thread.sleep(2000);
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btn_plus), withContentDescription("Round white btn"),
                        childAtPosition(
                                allOf(withId(R.id.app_bar_layout),
                                        childAtPosition(
                                                withId(R.id.app_bar),
                                                2)),
                                3),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.btn_add_review_btms_res), withText("Add a review"),
                        childAtPosition(
                                allOf(withId(R.id.btms_res_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                2),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.dialog_text_add_review),
                        childAtPosition(
                                allOf(withId(R.id.dialog_add_review),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("I love it"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_submit_add_review), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.dialog_add_review),
                                        3),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.review_layout),
                        childAtPosition(
                                withId(R.id.review_fragment_layout),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, longClick()));

        Thread.sleep(2000);
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_edit_btms_edit_review), withText("Edit review"),
                        childAtPosition(
                                allOf(withId(R.id.btms_edit_review_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.dialog_text_add_review), withText("I love it"),
                        childAtPosition(
                                allOf(withId(R.id.dialog_add_review),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("I love it so"));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.dialog_text_add_review), withText("I love it so"),
                        childAtPosition(
                                allOf(withId(R.id.dialog_add_review),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText6.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_submit_add_review), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.dialog_add_review),
                                        3),
                                1),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.review_layout),
                        childAtPosition(
                                withId(R.id.review_fragment_layout),
                                0)));
        recyclerView3.perform(actionOnItemAtPosition(0, longClick()));

        Thread.sleep(2000);
        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_delete_btms_edit_review), withText("Delete review"),
                        childAtPosition(
                                allOf(withId(R.id.btms_edit_review_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatButton5.perform(click());
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
