package com.suzu.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.suzu.matchers.OptionalMatchers.verify;

public class ListHashMapMatcher extends TypeSafeMatcher<List<HashMap<String, Object>>> {
    Description mismatchDescriber = new StringDescription();
    private boolean result = true;
    private List<HashMap<String, Object>> expectedObjects;
    private Class objMatcherClass;
    private List<String> keys;
    public ListHashMapMatcher(List<HashMap<String, Object>> expectedObjects, Class objMatcherClass, List<String> keys) {
        this.expectedObjects = expectedObjects;
        this.objMatcherClass = objMatcherClass;
        this.keys = keys;
    }

    public static Matcher<List<HashMap<String, Object>>> sameAs(List<HashMap<String, Object>> expectedObjects, Class objMatcherClass, List<String> keys) {
        return new ListHashMapMatcher(expectedObjects, objMatcherClass, keys);
    }

    boolean getResult() {
        return this.result;
    }

    void setResult(boolean r) {
        this.result &= r;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean matchesSafely(List<HashMap<String, Object>> actualObjects) {
        String key1 = keys.size() > 0 ? keys.get(0) : null;
        String key2 = keys.size() > 1 ? keys.get(1) : null;
        String key3 = keys.size() > 2 ? keys.get(2) : null;
        String key4 = keys.size() > 3 ? keys.get(3) : null;
        if (actualObjects.isEmpty() && expectedObjects.isEmpty()) {
            setResult(true);
        }

        if (expectedObjects.size() != actualObjects.size()) {
            setResult(false);
            mismatchDescriber.appendText(String.format("%s list size: expected [%s] but was [%s].\n",
                    expectedObjects.isEmpty() ? "" : expectedObjects.get(0).getClass().getSimpleName(),
                    expectedObjects.size(),
                    actualObjects.size()
            ));
        } else {
            expectedObjects.forEach(expectObj -> {
                Optional<HashMap<String, Object>> optActualObj = Optional.empty();
                for (HashMap<String, Object> actualObj : actualObjects) {
                    if (CompareUtils.equalsWithNulls(actualObj.get(key1), expectObj.get(key1)) && CompareUtils.equalsWithNulls(actualObj.get(key2)
                            , expectObj.get(key2)) && CompareUtils.equalsWithNulls(actualObj.get(key3), expectObj.get(key3))
                            && CompareUtils.equalsWithNulls(actualObj.get(key4), expectObj.get(key4))) {
                        optActualObj = Optional.of(actualObj);
                        break;
                    }
                }
                if (optActualObj.isEmpty()) {
                    mismatchDescriber.appendText("idInDB: " + expectObj + " Was missing in actual result\n");
                }

                setResult(OptionalMatchers.verify(optActualObj, OptionalMatchers.isPresent(), mismatchDescriber));
                optActualObj.ifPresent(subActualObj -> {
                    try {
                        setResult(OptionalMatchers.verify(
                                subActualObj,
                                (Matcher<HashMap<String, Object>>) objMatcherClass
                                        .getMethod("sameAs", HashMap.class)
                                        .invoke(objMatcherClass.getDeclaredConstructor().newInstance(), expectObj),
                                mismatchDescriber)
                        );
                    } catch (Exception e) {
                        setResult(false);
                        mismatchDescriber.appendText(e.getMessage() + "\n");
                        e.printStackTrace();
                    }
                });
            });
        }
        return getResult();
    }

    @Override
    public void describeTo(Description description) {
        if (expectedObjects.size() > 0) {
            description.appendText("should be the same as " + expectedObjects.get(0).getClass().getSimpleName());
        } else {
            description.appendText("should be the same as expected result");
        }
    }

    @Override
    protected void describeMismatchSafely(List<HashMap<String, Object>> item, Description mismatchDescription) {
        mismatchDescription.appendText(mismatchDescriber.toString());
    }
}
