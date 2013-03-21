package betsy.executables.soapui

class TestMessages {

    public static String createSyncTestPartnerInputMessage(String input) {
        """
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <testElementSyncRequest xmlns="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner">${input}</testElementSyncRequest>
   </soapenv:Body>
</soapenv:Envelope>
        """
    }

    public static String createSyncInputMessage(String input) {
        """
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <testElementSyncRequest xmlns="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface">${input}</testElementSyncRequest>
   </soapenv:Body>
</soapenv:Envelope>
        """
    }

    public static String createSyncStringInputMessage(String input) {
        """
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <testElementSyncStringRequest xmlns="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface">${input}</testElementSyncStringRequest>
   </soapenv:Body>
</soapenv:Envelope>
        """
    }

    public static String createAsyncInputMessage(String input) {
        """
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <testElementAsyncRequest xmlns="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface">${input}</testElementAsyncRequest>
   </soapenv:Body>
</soapenv:Envelope>
        """
    }

}
