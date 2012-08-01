package betsy.executables

import betsy.executables.ws.TestPartnerServicePublisher
import betsy.data.TestSuite
import java.util.concurrent.ExecutorService


class ExecutionContext {
    TestPartnerServicePublisher testPartner = new TestPartnerServicePublisher()
    TestSuite testSuite
    int requestTimeout = 5000
}
