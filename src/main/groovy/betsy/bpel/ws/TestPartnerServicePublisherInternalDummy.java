package betsy.bpel.ws;

import java.util.Objects;

import javax.xml.ws.Endpoint;

import betsy.common.config.Configuration;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType;
import org.apache.log4j.Logger;

public final class TestPartnerServicePublisherInternalDummy implements TestPartnerService {

    private static final Logger log = Logger.getLogger(TestPartnerServicePublisherInternalDummy.class);

    private final String url;

    private Endpoint endpoint;

    public TestPartnerServicePublisherInternalDummy() {
        this("http://" + Configuration.get("partner.ipAndPort") + "/bpel-assigned-testpartner");
    }

    public TestPartnerServicePublisherInternalDummy(String url) {
        this.url = Objects.requireNonNull(url);
    }

    public static void main(String... args) {
        new TestPartnerServicePublisherInternalDummy().startup();
    }

    @Override
    public void startup() {
        TestPartnerPortType portType = new TestPartnerPortTypeDummy();
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
