package peal.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import betsy.common.tasks.URLTasks;
import org.apache.log4j.Logger;

public class WSTester {

    public static final String VALUE = "13371337";

    public static void assertCorrectWorkingProcess(URL url) throws IOException {
        log.info("Testing SOAP service at url " + url);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
        con.setRequestProperty("SOAPAction", "sync");
        con.setDoOutput(true);
        con.setDoInput(true);

        // Posting the SOAP request XML message
        OutputStream reqStream = con.getOutputStream();
        reqStream.write(getMessage().getBytes());
        reqStream.flush();

        // Reading the SOAP response XML message
        String responseMessage = URLTasks.inputStreamToString(con.getInputStream());

        if(!responseMessage.contains(VALUE)) {
           throw new AssertionError("Response is wrong. Expected " + VALUE + " in the message, got " + responseMessage);
        } else {
            log.info("SOAP service at url " + url + " works correctly");
        }
    }

    private static String getMessage() {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <testElementSyncRequest xmlns=\"http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface\">" + VALUE + "</testElementSyncRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    private static final Logger log = Logger.getLogger(WSTester.class);

}
