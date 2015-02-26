package betsy.bpel.ws;

import betsy.common.config.Configuration;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType;
import org.apache.log4j.Logger;

import javax.xml.ws.Endpoint;
import java.util.Objects;

public final class TestPartnerServicePublisherInternalRegular implements TestPartnerService {

    private static final Logger log = Logger.getLogger(TestPartnerServicePublisherInternalRegular.class);

    private final String url;

    /**
     * mutable state
     */
    private Endpoint endpoint;

    public TestPartnerServicePublisherInternalRegular() {
        this("http://" + Configuration.get("partner.ipAndPort") + "/bpel-testpartner");
    }

    public TestPartnerServicePublisherInternalRegular(String url) {
        this.url = Objects.requireNonNull(url);
    }

    public static void main(String[] args) {
        new TestPartnerServicePublisherInternalRegular().startup();
    }

    @Override
    public void startup() {
        TestPartnerPortType portType = new TestPartnerPortTypeRegular();
        endpoint = Endpoint.publish(url, portType);
        log.info("Started TestPartnerService at " + url);
    }

    @Override
    public void shutdown() {
        if (!isRunning()) {
            log.info("Cannot shutdown TestPartnerService at " + url + " as it is not running");
            return;
        }

        log.info("Stopping TestPartnerService at " + url);
        endpoint.stop();
        log.info("Stopped TestPartnerService at " + url);
    }

    @Override
    public boolean isRunning() {
        return endpoint != null; // simple check
    }

}
