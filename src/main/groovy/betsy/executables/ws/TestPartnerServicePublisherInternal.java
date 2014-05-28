package betsy.executables.ws;

import betsy.config.Configuration;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType;
import org.apache.log4j.Logger;

import javax.xml.ws.Endpoint;

public class TestPartnerServicePublisherInternal implements TestPartnerService {

    private static final Logger log = Logger.getLogger(TestPartnerServicePublisherInternal.class);

    private Endpoint regularEndpoint;
    private Endpoint dummyEndpoint;

    public static void main(String[] args) {
        new TestPartnerServicePublisherInternal().publish();
    }

    @Override
    public void publish() {
        publishRegularEndpoint(getRegularUrl());
        publishDummyEndpoint(getDummyUrl());
    }

    private void publishRegularEndpoint(final String url) {
        TestPartnerPortType portType = new TestPartnerPortTypeRegular(true);
        regularEndpoint = Endpoint.publish(url, portType);
        log.info("Published regular TestPartnerService to " + url);
    }

    private void publishDummyEndpoint(final String url) {
        TestPartnerPortType portType = new TestPartnerPortTypeDummy();
        dummyEndpoint = Endpoint.publish(url, portType);
        log.info("Published regular TestPartnerService to " + url);
    }

    @Override
    public void unpublish() {
        unpublishEndpoint(regularEndpoint, getRegularUrl());
        unpublishEndpoint(dummyEndpoint, getDummyUrl());
    }

    private void unpublishEndpoint(Endpoint endpoint, final String url) {
        if (endpoint == null) {
            log.info("Cannot unpublish TestPartnerService from " + url + " as it has not been published yed.");
        } else {
            try {
                log.info("Unpublishing TestPartnerService from " + url);
                endpoint.stop();
                log.info("Unpublished TestPartnerService from " + url);
            } catch (NullPointerException ignore) {
                // do nothing, as this expected
            }
        }
    }

    private String getRegularUrl() {
        return "http://" + Configuration.get("partner.ipAndPort") + "/bpel-testpartner";
    }

    private String getDummyUrl() {
        return "http://" + Configuration.get("partner.ipAndPort") + "/bpel-assigned-testpartner";
    }

}
