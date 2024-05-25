package com.example.gourmetcompass;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
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
public class ManageCollTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void manageCollTest() throws InterruptedException {
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
                allOf(withId(R.id.my_collections_btn), withText("My Collections"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());

        Thread.sleep(2000);
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btn_add_my_collections), withContentDescription("Open bottom sheet"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction radioButton = onView(
                allOf(withId(R.id.radio_restaurant), withText("Restaurant"),
                        childAtPosition(
                                allOf(withId(R.id.radio_group_my_colls),
                                        childAtPosition(
                                                withId(R.id.bottom_sheet_new_coll_container),
                                                4)),
                                0),
                        isDisplayed()));
        radioButton.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_text_field_text),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.btms_coll_name_my_colls)),
                                        0),
                                0),
                        isDisplayed()));
        editText.perform(replaceText("My restaurants"));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.edit_text_field_text), withText("My restaurants"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.btms_coll_name_my_colls)),
                                        0),
                                0),
                        isDisplayed()));
        editText2.perform(closeSoftKeyboard());

        ViewInteraction button = onView(
                allOf(withId(R.id.btn_done_my_colls), withText("Done"),
                        childAtPosition(
                                allOf(withId(R.id.bottom_sheet_new_coll_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                5),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_my_coll_name), withText("My restaurants"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.my_coll_res_recyclerview),
                                        1),
                                0)));
        appCompatButton3.perform(scrollTo(), click());

        Thread.sleep(2000);
        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.my_coll_detail_btn_more), withContentDescription("Open bottom sheet"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        Thread.sleep(2000);
        ViewInteraction button2 = onView(
                allOf(withId(R.id.btn_rename_btms_my_coll_detail), withText("Rename collection"),
                        childAtPosition(
                                allOf(withId(R.id.btms_my_coll_detail_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                1),
                        isDisplayed()));
        button2.perform(click());

        Thread.sleep(2000);
        ViewInteraction editText3 = onView(
                allOf(withId(R.id.edit_text_field_text),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.btms_rename_text_field)),
                                        0),
                                0),
                        isDisplayed()));
        editText3.perform(click());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.edit_text_field_text),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.btms_rename_text_field)),
                                        0),
                                0),
                        isDisplayed()));
        editText4.perform(replaceText("My aus restaurants"));

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.edit_text_field_text), withText("My aus restaurants"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.btms_rename_text_field)),
                                        0),
                                0),
                        isDisplayed()));
        editText5.perform(closeSoftKeyboard());

        ViewInteraction button3 = onView(
                allOf(withId(R.id.btms_rename_btn_done), withText("Done"),
                        childAtPosition(
                                allOf(withId(R.id.btms_rename_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                3),
                        isDisplayed()));
        button3.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.my_coll_detail_btn_more), withContentDescription("Open bottom sheet"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction button4 = onView(
                allOf(withId(R.id.btn_delete_btms_my_coll_detail), withText("Delete collection"),
                        childAtPosition(
                                allOf(withId(R.id.btms_my_coll_detail_container),
                                        childAtPosition(
                                                withId(com.google.android.material.R.id.design_bottom_sheet),
                                                0)),
                                2),
                        isDisplayed()));
        button4.perform(click());
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
