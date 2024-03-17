package com.suzu.matcher.store;

import com.suzu.dataprovider.response.AccountInfoModel;
import org.hamcrest.*;

import static com.suzu.matchers.OptionalMatchers.verify;

public class AccountMatcher {
    public static Matcher<AccountInfoModel> sameAs(AccountInfoModel actResult) {
        return new TypeSafeMatcher<AccountInfoModel>() {
            final Description mismatchDescriber = new StringDescription();

            @Override
            protected boolean matchesSafely(AccountInfoModel expResult) {
                return verify(actResult, Matchers.equalToObject(expResult)
                        , mismatchDescriber);
            }

            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected void describeMismatchSafely(AccountInfoModel item, Description mismatchDescription) {
                mismatchDescription.appendText(mismatchDescriber.toString());
            }
        };
    }
}
