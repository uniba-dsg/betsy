package betsy.data.engines.camunda

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 25.02.14
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
class CamundaTester {

    String restURL

    /**
     * runs a single test
     */
    void runTest(){

        // needed process information
        String key = "SimpleApplication"
        HashMap<String, Boolean> variableResults = new HashMap<String, Boolean>()
        variableResults.put("successfull", true)

        //first request to get id
        JSONObject response = get(restURL + "/process-definition?key=${key}")
        String id = response.get("id")


        //assembling JSONObject for second request
        JSONObject requestBody = new JSONObject()
        JSONObject variables = new JSONObject()
        for(String variable : variableResults.keySet()){
            JSONObject variableObject = new JSONObject()
            variableObject.put("value", false)
            variableObject.put("type", "Boolean")
            variables.put(variable, variableObject)
        }
        /*JSONObject amount = new JSONObject()
        JSONObject customerId = new JSONObject()
        amount.put("value", 200)
        amount.put("type", "Integer")
        customerId.put("value", "test")
        customerId.put("type","String")
        variables.put("amount", amount)
        variables.put("customerId", customerId)*/
        requestBody.put("variables", variables)
        requestBody.put("businessKey", "key-${key}")

        //second request to start process using id and Json to get the process instance id
        response = post(restURL + "/process-definition/${id}/start", requestBody)
        String processInstanceId = response.get("id")

        //third request to get variable back
        /*for (String variable : variableResults.keySet()){
            response = get(restURL + "/variable-instance?processInstanceIdIn=${processInstanceId}&variableName=${variable}")
            println response.get("value")
            //TODO: Assertion
        }*/

    }

    /**
     * GET request to Camunda REST Interface
     * @param url Camunda REST URL
     * @return JSONObject with response data
     */
    JSONObject get(String url){
        try {
            URL requestUrl = new URL(url);
            URLConnection conn = requestUrl.openConnection();
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

                    return obj
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace()
        } catch (IOException ignored) {
            ignored.printStackTrace()
        }

        return null
    }

    /**
     * POST request to Camunda REST Interface
     * @param url Camunda REST URL
     * @param requestBody JSONObject with request data to be uploaded
     * @return JSONObject with response data
     */
    JSONObject post(String url, JSONObject requestBody){

        try {

            URL startUrl = new URL(url);
            HttpURLConnection startCall = startUrl.openConnection();
            startCall.setRequestMethod("POST")
            startCall.setDoOutput(true)
            startCall.setDoInput(true)
            startCall.setRequestProperty("Content-Type", "application/json")
            //Send request
            DataOutputStream wr = new DataOutputStream (
                    startCall.getOutputStream ());
            wr.writeBytes (requestBody.toString());
            wr.flush ();
            wr.close ();

            //get response
            BufferedReader br = new BufferedReader(new InputStreamReader(startCall.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            String jsonResponse = sb.toString();
            JSONObject response = new JSONObject(jsonResponse)

            return response

        } catch (MalformedURLException e) {
            e.printStackTrace()
        } catch (IOException ignored) {
            ignored.printStackTrace()
        }

        return null

    }

}
