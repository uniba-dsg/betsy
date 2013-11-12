package betsy.executables.ws

import ant.tasks.AntUtil
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType
import org.apache.log4j.Logger

import javax.xml.ws.Endpoint

import betsy.Configuration

class TestPartnerServicePublisher {

    private static final Logger log = Logger.getLogger(TestPartnerServicePublisher.class)

    Endpoint regularEndpoint
    String regularUrl = "http://${Configuration.get("partner.ipAndPort")}/bpel-testpartner"

    Endpoint partnerLinkAssignmentEndpoint
    String partnerLinkAssignmentUrl = "http://${Configuration.get("partner.ipAndPort")}/bpel-assigned-testpartner"

    AntBuilder ant = AntUtil.builder()

    public static void main(String[] args) {
        new TestPartnerServicePublisher().publish()
    }

    void publish() {
        publishRegularEndpoint(regularUrl)
        publishPartnerLinkAssignmentEndpoint(partnerLinkAssignmentUrl)
    }

    private void publishRegularEndpoint(String url) {
        TestPartnerPortType portType = new TestPartnerServiceMock(true)
        regularEndpoint = Endpoint.publish(url, portType)
        log.info "Published regular TestPartnerService to ${url}"
    }

    private void publishPartnerLinkAssignmentEndpoint(String url) {
        TestPartnerPortType portType = new TestPartnerServiceMock(false)
        partnerLinkAssignmentEndpoint = Endpoint.publish(url, portType)
        log.info "Published regular TestPartnerService to ${url}"
    }

    void unpublish() {
        unpublishEndpoint(regularEndpoint, regularUrl)
        unpublishEndpoint(partnerLinkAssignmentEndpoint, partnerLinkAssignmentUrl)
    }

    private void unpublishEndpoint(Endpoint endpoint, String url) {
        if (endpoint == null) {
            log.info "Cannot unpublish TestPartnerService from ${url} as it has not been published yed."
        } else {
            try {
                log.info "Unpublishing TestPartnerService from ${url}"
				endpoint.stop()
                log.info "Unpublished TestPartnerService from ${url}"
			} catch (NullPointerException ignore) {
				// do nothing, as this expected
			}

		}
	}

}
