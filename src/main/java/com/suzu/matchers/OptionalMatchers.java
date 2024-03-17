package com.suzu.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;

public class OptionalMatchers {

    //This is an utility class that must not be instantiated.
    private OptionalMatchers() {
    }

    /**
     * Creates a matcher that matches when the examined {@code Optional}
     * contains a value.
     * <pre>
     *     Optional&lt;String&gt; optionalObject = Optional.of("dummy value");
     *     assertThat(optionalObject, isPresent());
     * </pre>
     *
     * @return a matcher that matches when the examined {@code Optional}
     * contains a value.
     */
    public static Matcher<Optional<?>> isPresent() {
        return new PresenceMatcher();
    }

    /**
     * Creates a matcher that matches when the examined {@code Optional}
     * contains no value.
     * <pre>
     *     Optional&lt;String&gt; optionalObject = Optional.empty();
     *     assertThat(optionalObject, isEmpty());
     * </pre>
     *
     * @return a matcher that matches when the examined {@code Optional}
     * contains no value.
     */
    public static Matcher<Optional<?>> isEmpty() {
        return new EmptyMatcher();
    }

    /**
     * Creates a matcher that matches when the examined {@code Optional}
     * contains a value that is logically equal to the {@code operand}, as
     * determined by calling the {@code equals} method on the value.
     * <pre>
     *     Optional&lt;String&gt; optionalInt = Optional.of("dummy value");
     *     assertThat(optionalInt, isPresentAndIs("dummy value"));
     * </pre>
     *
     * @param operand the object that any examined {@code Optional} value
     *                should equal
     * @param <T>     the class of the value.
     * @return a matcher that matches when the examined {@code Optional}
     * contains a value that is logically equal to the {@code operand}.
     */
    public static <T> Matcher<Optional<T>> isPresentAndIs(T operand) {
        return new HasValue<>(equalTo(operand));
    }

    /**
     * Creates a matcher that matches when the examined {@code Optional}
     * contains a value that satisfies the specified matcher.
     * <pre>
     *     Optional&lt;String&gt; optionalObject = Optional.of("dummy value");
     *     assertThat(optionalObject, isPresentAnd(startsWith("dummy")));
     * </pre>
     *
     * @param matcher a matcher for the value of the examined {@code Optional}.
     * @param <T>     the class of the value.
     * @return a matcher that matches when the examined {@code Optional}
     * contains a value that satisfies the specified matcher.
     */
    public static <T> Matcher<Optional<T>> isPresentAnd(Matcher<? super T> matcher) {
        return new HasValue<>(matcher);
    }

    public static Matcher<HashMap<String, Object>> has(String property, Matcher<? super Object> matcher) {
        return new TypeSafeMatcher<HashMap<String, Object>>() {
            Description mismatchDescriber = new StringDescription();

            @Override
            protected boolean matchesSafely(HashMap<String, Object> actual) {
                Object value = featureValueOf(actual);
                return verify(value, matcher, mismatchDescriber);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("\"%s\" expected ", property)).appendDescriptionOf(matcher);
            }

            @SuppressWarnings("unchecked")
            Object featureValueOf(HashMap<String, Object> actual) {
                String[] subProperty = property.split("\\.");
                Object data = new HashMap<>(actual);
                for (String s : subProperty) {
                    if (data instanceof HashMap) {
                        data = ((HashMap<String, Object>) data).get(s);
                    } else if (data instanceof ArrayList) {
                        if (s.matches("^\\{\\w*=\\w*\\}$")) {
                            String[] strArr = s.substring(1, s.length() - 1).split("=");
                            String subKey = strArr[0];
                            String subValue = strArr[1];
                            data = ((ArrayList<HashMap<String, Object>>) Objects.requireNonNull(data)).stream().filter(obj ->
                                    obj.get(subKey).toString().equals(subValue)).findFirst().orElse(null);
                        } else {
                            data = ((ArrayList<Object>) data).get(Integer.parseInt(s));
                        }
                    }
                }
                return data;
            }


            @Override
            protected void describeMismatchSafely(HashMap<String, Object> theMismatchItem, Description mismatchDescription) {
                mismatchDescription.appendText(mismatchDescriber.toString());
            }
        };
    }

    public static <T> boolean verify(T a, Matcher<? super T> matcher, Description mismatchDesc) {
        boolean result = verify(a, matcher);
        if (!result) {
            matcher.describeMismatch(a, mismatchDesc);
        }
        return result;
    }

    public static <T> boolean verify(T a, Matcher<? super T> matcher) {
        return matcher.matches(a);
    }

    private static class PresenceMatcher extends TypeSafeMatcher<Optional<?>> {

        public void describeTo(Description description) {
            description.appendText("is <Present>");
        }

        @Override
        protected boolean matchesSafely(Optional<?> item) {
            return item.isPresent();
        }

        @Override
        protected void describeMismatchSafely(Optional<?> item, Description mismatchDescription) {
            mismatchDescription.appendText("was missing\n");
        }
    }

    private static class EmptyMatcher extends TypeSafeMatcher<Optional<?>> {

        public void describeTo(Description description) {
            description.appendText("is <Empty>");
        }

        @Override
        protected boolean matchesSafely(Optional<?> item) {
            return !item.isPresent();
        }

        @Override
        protected void describeMismatchSafely(Optional<?> item, Description mismatchDescription) {
            mismatchDescription.appendText("had value ");
            mismatchDescription.appendValue(item.get());
        }
    }

    private static class HasValue<T> extends TypeSafeMatcher<Optional<T>> {
        private Matcher<? super T> matcher;

        public HasValue(Matcher<? super T> matcher) {
            this.matcher = matcher;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("has value that is ");
            matcher.describeTo(description);
        }

        @Override
        protected boolean matchesSafely(Optional<T> item) {
            return item.isPresent() && matcher.matches(item.get());
        }

        @Override
        protected void describeMismatchSafely(Optional<T> item, Description mismatchDescription) {
            if (item.isPresent()) {
                mismatchDescription.appendText("value ");
                matcher.describeMismatch(item.get(), mismatchDescription);
            } else {
                mismatchDescription.appendText("was <Empty>");
            }
        }
    }
}