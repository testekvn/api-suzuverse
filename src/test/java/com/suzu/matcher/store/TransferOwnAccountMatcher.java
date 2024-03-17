package com.suzu.matcher.store;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;

import static com.suzu.matchers.CombinableMatcher.all;
import static com.suzu.matchers.OptionalMatchers.has;
import static com.suzu.matchers.OptionalMatchers.verify;
import static org.hamcrest.Matchers.equalTo;

public class TransferOwnAccountMatcher {

    public static Matcher<HashMap<String, Object>> sameAs(HashMap<String, Object> expected) {
        return new TypeSafeMatcher<>() {
            final Description mismatchDescriber = new StringDescription();

            @SuppressWarnings("unchecked")
            @Override
            protected boolean matchesSafely(HashMap<String, Object> actual) {
                mismatchDescriber.appendText(String.format("\n======= Validate Transfer To Own Account From Account ======= <%s>", actual.get("fromAccountNumber")));
                return verify(expected, all(has("fromAccountNumber", equalTo(actual.get("fromAccountNumber"))))
                                .and(has("toAccountNumber", equalTo(actual.get("toAccountNumber"))))
                                .and(has("amount", equalTo(actual.get("amount"))))
                                .and(has("transactionsNotes", equalTo(actual.get("transactionsNotes"))))
                        , mismatchDescriber);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("\nNAV from API should be the same as database");
            }

            @Override
            protected void describeMismatchSafely(HashMap<String, Object> item, Description mismatchDescription) {
                mismatchDescription.appendText(mismatchDescriber.toString());
            }
        };
    }
}
