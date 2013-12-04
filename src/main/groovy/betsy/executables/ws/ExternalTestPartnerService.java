package betsy.executables.ws;

import org.apache.log4j.Logger;

public class ExternalTestPartnerService implements TestPartnerService {

    private static final Logger log = Logger.getLogger(ExternalTestPartnerService.class);

    @Override
    public void publish() {
        log.info("Using external 3rd party partner service (must be already published)");
    }

    @Override
    public void unpublish() {
        log.info("Using external 3rd party partner service (will not be unpublished)");
    }
}
