package betsy.data.assertions

import betsy.data.TestAssertion

class NotDeployableAssertion extends TestAssertion {

    String reason

    @Override
    public String toString() {
        return "NotDeployableAssertion{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
