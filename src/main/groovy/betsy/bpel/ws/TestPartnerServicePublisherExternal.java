package betsy.bpel.ws;

import org.apache.log4j.Logger;

public final class TestPartnerServicePublisherExternal implements TestPartnerService {

    private static final Logger log = Logger.getLogger(TestPartnerServicePublisherExternal.class);

    @Override
    public void startup() {
        log.info("Using external 3rd party partner service (must be already published)");
    }

    @Override
    public void shutdown() {
        log.info("Using external 3rd party partner service (will not be unpublished)");
    }

    @Override
    public boolean isRunning() {
        return true;
    }
}
