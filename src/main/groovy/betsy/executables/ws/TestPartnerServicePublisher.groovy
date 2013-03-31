package betsy.executables.ws

import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType

import javax.xml.ws.Endpoint

class TestPartnerServicePublisher {

	Configuration config = Configuration.getInstance()

	Endpoint regularEndpoint
	String regularUrl = "http://${config.getValueAsString('PARTNER_IP_AND_PORT')}/bpel-testpartner"

	Endpoint partnerLinkAssignmentEndpoint
	String partnerLinkAssignmentUrl = "http://${config.getValueAsString('PARTNER_IP_AND_PORT')}/bpel-assigned-testpartner"

	AntBuilder ant = new AntBuilder()

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
