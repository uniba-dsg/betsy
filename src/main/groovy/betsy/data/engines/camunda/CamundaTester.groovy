package betsy.data.engines.camunda

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: stavorndran
 * Date: 25.02.14
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
class CamundaTester {

    String restURL

    void runTest(){
        try {
            URL url = new URL(restURL + "/process-definition?key=approve-loan");
            try {
                URLConnection conn = url.openConnection();
                if (conn instanceof HttpURLConnection) {
                    HttpURLConnection http = (HttpURLConnection) conn;
                    int code = http.getResponseCode();
                    if (code > 0 && code < 400) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        String jsonResponse = sb.toString();

                        JSONArray jsonArray = new JSONArray(jsonResponse)
                        JSONObject obj = (JSONObject)jsonArray.get(0)
                        String id = obj.get("id")


                        URL startUrl = new URL(restURL + "/process-definition/${id}/start");
                        HttpURLConnection startCall = startUrl.openConnection();
                        startCall.setRequestMethod("POST")
                        startCall.setDoOutput(true)
                        startCall.setDoInput(true)
                        startCall.setRequestProperty("Content-Type", "application/json")

                        //Assemble JSON
                        JSONObject requestBody = new JSONObject()
                        JSONObject variables = new JSONObject()
                        JSONObject amount = new JSONObject()
                        JSONObject customerId = new JSONObject()
                        amount.put("value", 200)
                        amount.put("type", "Integer")
                        customerId.put("value", "test")
                        customerId.put("type","String")
                        variables.put("amount", amount)
                        variables.put("customerId", customerId)
                        requestBody.put("variables", variables)
                        requestBody.put("businessKey", "testKey")

                        //Send request
                        DataOutputStream wr = new DataOutputStream (
                                startCall.getOutputStream ());
                        wr.writeBytes (requestBody.toString());
                        wr.flush ();
                        wr.close ();

                        //get response
                        BufferedReader br2 = new BufferedReader(new InputStreamReader(startCall.getInputStream()));
                        StringBuilder sb2 = new StringBuilder();
                        String line2;
                        while ((line2 = br2.readLine()) != null) {
                            sb2.append(line2+"\n");
                        }
                        br2.close();
                        String jsonResponse2 = sb2.toString();
                        JSONObject response = new JSONObject(jsonResponse2)

                        //get variable back
                        URL variableUrl = new URL(restURL + "/variable-instance?processInstanceIdIn=${response.get("id")}&variableName=amount");
                        HttpURLConnection variableCall = variableUrl.openConnection();

                        //get response
                        BufferedReader br3 = new BufferedReader(new InputStreamReader(variableCall.getInputStream()));
                        StringBuilder sb3 = new StringBuilder();
                        String line3;
                        while ((line3 = br3.readLine()) != null) {
                            sb3.append(line3+"\n");
                        }
                        br3.close();
                        String jsonResponse3 = sb3.toString();

                        JSONArray jsonArray3 = new JSONArray(jsonResponse3)
                        JSONObject obj3 = (JSONObject)jsonArray3.get(0)
                        int value = obj3.get("value")

                        println value
                    }else{
                        System.out.println("error 1")
                    }
                }else{
                    System.out.println("error 2")
                }
            } catch (IOException ignored) {
                ignored.printStackTrace()
            }
        } catch (MalformedURLException e) {
            e.printStackTrace()
        }
    }
}
