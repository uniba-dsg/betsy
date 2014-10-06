package betsy.bpel.ws;

import org.apache.log4j.Logger;

public class TestPartnerServicePublisherExternal implements TestPartnerService {

    private static final Logger log = Logger.getLogger(TestPartnerServicePublisherExternal.class);

    @Override
    public void publish() {
        log.info("Using external 3rd party partner service (must be already published)");
    }

    @Override
    public void unpublish() {
        log.info("Using external 3rd party partner service (will not be unpublished)");
    }
}
