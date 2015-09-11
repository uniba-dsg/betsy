package betsy.bpel.ws;

public final class DummyAndRegularTestPartnerService implements TestPartnerService {

    private final TestPartnerService testPartnerServiceDummy = new TestPartnerServicePublisherInternalDummy();
    private final TestPartnerService testPartnerServiceRegular = new TestPartnerServicePublisherInternalRegular();

    @Override
    public void startup() {
        testPartnerServiceDummy.startup();
        testPartnerServiceRegular.startup();
    }

    @Override
    public void shutdown() {
        testPartnerServiceDummy.shutdown();
        testPartnerServiceRegular.shutdown();
    }

    @Override
    public boolean isRunning() {
        return testPartnerServiceDummy.isRunning() && testPartnerServiceRegular.isRunning();
    }

    public static void main(String... args) {
        new DummyAndRegularTestPartnerService().startup();
    }
}
