package com.suzu.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.suzu.matchers.OptionalMatchers.verify;


public class ListObjectMatcher extends TypeSafeMatcher<List<HashMap<String, Object>>> {

    Description mismatchDescriber = new StringDescription();

    private List<?> expectedObjects;
    private Class objMatcherClass;
    private List<String> keys;
    private boolean result = true;

    public ListObjectMatcher(List<?> expectedObjects, Class objMatcherClass, List<String> keys) {
        this.expectedObjects = expectedObjects;
        this.objMatcherClass = objMatcherClass;
        this.keys = keys;
    }

    public static Matcher<List<HashMap<String, Object>>> sameAs(List<?> expectedObjects, Class objMatcherClass,
                                                                List<String> keys) {
        return new ListObjectMatcher(expectedObjects, objMatcherClass, keys);
    }

    boolean getResult() {
        return this.result;
    }

    void setResult(boolean r) {
        this.result &= r;
    }

    @Override
    protected boolean matchesSafely(List<HashMap<String, Object>> actualObjects) {
        if (actualObjects.isEmpty() && expectedObjects.isEmpty()) {
            setResult(true);
        }
        expectedObjects.forEach(expectObj -> compareListMapWithObject(actualObjects, expectObj));
        return getResult();
    }

    @Override
    public void describeTo(Description description) {
        if (!expectedObjects.isEmpty()) {
            description.appendText("should be the same as " + expectedObjects.get(0).getClass().getSimpleName());
        } else {
            description.appendText("should be the same as expected result");
        }
    }

    @Override
    protected void describeMismatchSafely(List<HashMap<String, Object>> item, Description mismatchDescription) {
        mismatchDescription.appendText(mismatchDescriber.toString());
    }

    Optional<HashMap<String, Object>> assignOpt(List<HashMap<String, Object>> actualObjects,
                                                Object expectObj) {
        for (HashMap<String, Object> actualObj : actualObjects) {
            if (compare(actualObj, expectObj, keys)) {
                return Optional.of(actualObj);
            }
        }
        return Optional.empty();
    }


    void compareListMapWithObject(List<HashMap<String, Object>> actualObjects, Object expectObj) {
        Optional<HashMap<String, Object>> optActualObj = assignOpt(actualObjects, expectObj);
        if (optActualObj.isEmpty()) {
            mismatchDescriber.appendText("idInDB: " + expectObj + " Was missing in actual result\n");
        }
        setResult(OptionalMatchers.verify(optActualObj, OptionalMatchers.isPresent(), mismatchDescriber));
        optActualObj.ifPresent(subActualObj -> {
            try {
                setResult(OptionalMatchers.verify(
                        subActualObj,
                        (Matcher<HashMap<String, Object>>) objMatcherClass
                                .getMethod("sameAs", expectObj.getClass())
                                .invoke(objMatcherClass.getDeclaredConstructor().newInstance(), expectObj),
                        mismatchDescriber));
            } catch (Exception e) {
                setResult(false);
                mismatchDescriber.appendText(e.getMessage() + "\n");
            }
        });
    }

    boolean compare(HashMap<String, Object> actualObj, Object expectObj, List<String> keys) {

        String expectValue = null;
        for (String str : keys) {
            try {
                expectValue = String.valueOf(expectObj.getClass().getMethod(fieldToGetMethod(str)).invoke(expectObj));
            } catch (Exception e) {
                setResult(false);
                mismatchDescriber.appendText(e.getMessage() + "\n");
            }
            if (!String.valueOf(actualObj.get(str)).equals(expectValue)) {
                return false;
            }
        }
        return true;
    }

    String fieldToGetMethod(String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

}
