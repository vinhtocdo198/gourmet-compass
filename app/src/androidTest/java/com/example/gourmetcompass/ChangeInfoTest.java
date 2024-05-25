package com.example.gourmetcompass;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
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
public class ChangeInfoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void changeInfoTest() throws InterruptedException {
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
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.personal_info_btn), withText("Personal Information"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        Thread.sleep(2000);
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edit_text_field_text),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.username_basic_info)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("vinhltv"));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.edit_text_field_text), withText("vinhltv"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.username_basic_info)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.edit_text_field_text),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.phone_basic_info)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("0947848048"));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.edit_text_field_text), withText("0947848048"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.phone_basic_info)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_save_basic_info), withText("Save"),
                        childAtPosition(
                                allOf(withId(R.id.basic_info_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatButton3.perform(click());
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
