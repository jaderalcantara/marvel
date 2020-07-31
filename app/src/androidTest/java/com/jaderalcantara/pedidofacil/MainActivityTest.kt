package com.jaderalcantara.pedidofacil


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.jaderalcantara.pedidofacil.feature.presentation.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    /*
        I just did two simple ui test, but I could create
        test for add button and scroll and test the state
     */
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun plusButton_onClick_shouldPlusOneOnQuantity() {
        val appCompatImageView = onView(
            allOf(
                withId(R.id.plus),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.list),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.quantity), withText("1"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.list),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("1")))
    }

    @Test
    fun minusButton_onClick_shouldMinusOneOnQuantity() {
        val plusButton = onView(
            allOf(
                withId(R.id.plus),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.list),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        plusButton.perform(click())
        plusButton.perform(click())

        val minusButton = onView(
            allOf(
                withId(R.id.minus),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.list),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        minusButton.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.quantity), withText("1"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.list),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("1")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
