package com.example.gourmetcompass;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

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
public class ManageNotiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void manageNotiTest() throws InterruptedException {
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
        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.noti_fragment), withContentDescription("Notification"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.noti_recyclerview),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                3)));
        recyclerView.perform(actionOnItemAtPosition(0, longClick()));

        Thread.sleep(2000);
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_noti_mark), withText("Mark as read"),
                        childAtPosition(
                                allOf(withId(R.id.btms_noti_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.noti_recyclerview),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                3)));
        recyclerView2.perform(actionOnItemAtPosition(0, longClick()));

        Thread.sleep(2000);
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_noti_mark), withText("Mark as unread"),
                        childAtPosition(
                                allOf(withId(R.id.btms_noti_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.btn_check_noti), withContentDescription("Check noti"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatImageButton3.perform(click());
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
