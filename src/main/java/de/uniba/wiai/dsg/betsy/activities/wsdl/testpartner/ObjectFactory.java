package de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.uniba.wiai.dsg.bpel_engine_comparison.activities.wsdl.testpartner package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TestElementSyncRequest_QNAME = new QName("http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", "testElementSyncRequest");
    private final static QName _TestElementAsyncRequest_QNAME = new QName("http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", "testElementAsyncRequest");
    private final static QName _TestElementSyncResponse_QNAME = new QName("http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", "testElementSyncResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.uniba.wiai.dsg.bpel_engine_comparison.activities.wsdl.testpartner
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", name = "testElementSyncRequest")
    public JAXBElement<Integer> createTestElementSyncRequest(Integer value) {
        return new JAXBElement<Integer>(_TestElementSyncRequest_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", name = "testElementAsyncRequest")
    public JAXBElement<Integer> createTestElementAsyncRequest(Integer value) {
        return new JAXBElement<Integer>(_TestElementAsyncRequest_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", name = "testElementSyncResponse")
    public JAXBElement<Integer> createTestElementSyncResponse(Integer value) {
        return new JAXBElement<Integer>(_TestElementSyncResponse_QNAME, Integer.class, null, value);
    }

}
