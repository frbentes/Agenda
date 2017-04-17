package com.frbentes.agendaac;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.frbentes.agendaac.view.activity.SignInActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by frbentes on 17/04/17.
 */
@android.support.test.filters.LargeTest
@RunWith(AndroidJUnit4.class)
public class NewAppointmentTest {
    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule =
            new ActivityTestRule<>(SignInActivity.class);

    @Test
    public void newAppointmentTest() {
        // Generate user and appointment content
        String username = "user" + randomDigits();
        String email = username + "@example.com";
        String password = "testuser";
        String appointmentTitle = "Title " + randomDigits();
        String appointmentDescription = "Description " + randomDigits();
        String appointmentDate = "17/04/2017";
        String appointmentTime = "12:00";

        // Go back to the sign in screen if we're logged in from a previous test
        logOutIfPossible();

        // Select email field
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edt_email),
                        withParent(withId(R.id.ll_email_password)),
                        isDisplayed()));
        appCompatEditText.perform(click());

        // Enter email address
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edt_email),
                        withParent(withId(R.id.ll_email_password)),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(email));

        // Enter password
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edt_password),
                        withParent(withId(R.id.ll_email_password)),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText(password));

        // Click sign up
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_sign_up), withText(R.string.sign_up_button),
                        withParent(withId(R.id.ll_buttons)),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Click new appointment button
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_new_appointment), isDisplayed()));
        floatingActionButton.perform(click());

        // Enter appointment title
        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.edt_title), isDisplayed()));
        appCompatEditText4.perform(replaceText(appointmentTitle));

        // Enter appointment description
        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.edt_description), isDisplayed()));
        appCompatEditText5.perform(replaceText(appointmentDescription));

        // Enter appointment date
        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.edt_date), isDisplayed()));
        appCompatEditText5.perform(replaceText(appointmentDate));

        // Enter appointment time
        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.edt_time), isDisplayed()));
        appCompatEditText5.perform(replaceText(appointmentTime));

        // Click save button on menu item
        onView(withId(R.id.button_save))
                .perform(click());

        // Check that the title is correct
        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_title), withText(appointmentTitle), isDisplayed()));
        textView.check(matches(withText(appointmentTitle)));

        // Check that the description is correct
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_description), withText(appointmentDescription), isDisplayed()));
        textView.check(matches(withText(appointmentDescription)));

        // Check that the date and time are correct
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tv_date), withText(appointmentDate + " " + appointmentTime),
                        isDisplayed()));
        textView.check(matches(withText(appointmentDate + " " + appointmentTime)));
    }

    /**
     * Click the 'Log Out' overflow menu if it exists (which would mean we're signed in).
     */
    private void logOutIfPossible() {
        try {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
            onView(withText(R.string.action_logout)).perform(click());
        } catch (NoMatchingViewException e) {
            // Ignore exception since we only want to do this operation if it's easy.
        }
    }

    /**
     * Generate a random string of digits.
     */
    private String randomDigits() {
        Random random = new Random();
        return String.valueOf(random.nextInt(99999999));
    }

}
