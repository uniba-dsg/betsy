package betsy.common.model;

import pebl.benchmark.test.TestCase;

public interface AssertionScript {

    void check(TestCase testCase) throws AssertionError;
}
