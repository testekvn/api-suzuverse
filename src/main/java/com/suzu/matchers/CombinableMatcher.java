package com.suzu.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

public class CombinableMatcher<T> extends BaseMatcher<T> {
    private final List<Matcher<? super T>> matchers = new ArrayList<>();
    private final List<Matcher<? super T>> failed = new ArrayList<>();

    private CombinableMatcher(final Matcher matcher) {
        matchers.add(matcher);
    }

    public static <T> CombinableMatcher<T> all(final Matcher<? super T> matcher) {
        return new CombinableMatcher<T>(matcher);
    }

    public CombinableMatcher and(final Matcher matcher) {
        matchers.add(matcher);
        return this;
    }

    @Override
    public boolean matches(final Object item) {
        boolean finalResult = true;
        for (final Matcher<? super T> matcher : matchers) {
            if (!matcher.matches(item)) {
                failed.add(matcher);
                finalResult = false;
            }
        }
        return finalResult;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendList("(", " " + "and" + " ", ")", matchers);
    }

    @Override
    public void describeMismatch(final Object item, final Description
            description) {
        int i = 1;
        description.appendText("\n");
        for (final Matcher<? super T> matcher : failed) {
            description.appendText(String.format("%d. ", i));
            description.appendDescriptionOf(matcher).appendText(" but actual ");
            matcher.describeMismatch(item, description);
            description.appendText("\n");
            i += 1;
        }
    }
}