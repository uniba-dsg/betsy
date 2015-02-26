package betsy.bpel.ws;

import betsy.common.config.Configuration;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType;
import org.apache.log4j.Logger;

import javax.xml.ws.Endpoint;
import java.util.Objects;

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

    public static void main(String[] args) {
        new TestPartnerServicePublisherInternalDummy().startup();
    }

    @Override
    public void startup() {
        TestPartnerPortType portType = new TestPartnerPortTypeDummy();
        endpoint = Endpoint.publish(url, portType);
        log.info("Published TestPartnerService to " + url);
    }

    @Override
    public void shutdown() {
        if (!isRunning()) {
            log.info("Cannot shutdown TestPartnerService at " + url + " as it is not running");
            return;
        }

        log.info("Unpublishing TestPartnerService from " + url);
        endpoint.stop();
        log.info("Unpublished TestPartnerService from " + url);
    }

    @Override
    public boolean isRunning() {
        return endpoint != null; // simple check
    }

}
