package betsy.bpel.soapui;

public class TestMessages {

    public static String createSyncTestPartnerInputMessage(String input) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <testElementSyncRequest xmlns=\"http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner\">" + input + "</testElementSyncRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    public static String createSyncInputMessage(String input) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <testElementSyncRequest xmlns=\"http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface\">" + input + "</testElementSyncRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    public static String createSyncStringInputMessage(String input) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <testElementSyncStringRequest xmlns=\"http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface\">" + input + "</testElementSyncStringRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    public static String createAsyncInputMessage(String input) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <testElementAsyncRequest xmlns=\"http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface\">" + input + "</testElementAsyncRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

}
