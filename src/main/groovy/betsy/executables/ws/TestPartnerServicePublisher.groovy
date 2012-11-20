package betsy.executables.ws

import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType

import javax.xml.ws.Endpoint

class TestPartnerServicePublisher {

    Endpoint regularEndpoint
    String regularUrl = "http://0.0.0.0:2000/bpel-testpartner"

    Endpoint partnerLinkAssignmentEndpoint
    String partnerLinkAssignmentUrl = "http://0.0.0.0:2000/bpel-assigned-testpartner"

    AntBuilder ant = new AntBuilder()

    void publish() {
        publishRegularEndpoint(regularUrl)
        publishPartnerLinkAssignmentEndpoint(partnerLinkAssignmentUrl)
    }

    private void publishRegularEndpoint(String url) {
        TestPartnerPortType portType = new TestPartnerServiceMock(true)
        regularEndpoint = Endpoint.publish(url, portType)
        ant.echo message: "Published regular TestPartnerService to ${url}"
    }

    private void publishPartnerLinkAssignmentEndpoint(String url) {
        TestPartnerPortType portType = new TestPartnerServiceMock(false)
        partnerLinkAssignmentEndpoint = Endpoint.publish(url, portType)
        ant.echo message: "Published regular TestPartnerService to ${url}"
    }

    void unpublish() {
        unpublishEndpoint(regularEndpoint, regularUrl)
        unpublishEndpoint(partnerLinkAssignmentEndpoint, partnerLinkAssignmentUrl)
    }

    private void unpublishEndpoint(Endpoint endpoint, String url) {
        if (endpoint == null) {
            ant.echo "Cannot unpublish TestPartnerService from ${url} as it has not been published yed."
        } else {
            try {
                endpoint.stop()
                ant.echo "Unpublished TestPartnerService from ${url}"
            } catch (NullPointerException ignore) {
                // do nothing, as this expected
            }

        }
    }

}
