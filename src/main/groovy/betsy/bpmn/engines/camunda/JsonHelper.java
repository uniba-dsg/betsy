package betsy.bpmn.engines.camunda;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class JsonHelper {

    /**
     * GET request to Camunda REST Interface
     *
     * @param url Camunda REST URL
     * @return JSONObject with response data
     */
    public static JSONObject get(String url) {
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
                        sb.append(line);
                        sb.append("\n");
                    }
                    br.close();
                    String jsonResponse = sb.toString();
                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    return (JSONObject) jsonArray.get(0);
                }
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }

        return null;
    }

    /**
     * POST request to Camunda REST Interface
     *
     * @param url         Camunda REST URL
     * @param requestBody JSONObject with request data to be uploaded
     * @return JSONObject with response data
     */
    public static JSONObject post(String url, JSONObject requestBody) {

        try {

            URL startUrl = new URL(url);
            HttpURLConnection startCall = (HttpURLConnection) startUrl.openConnection();
            startCall.setRequestMethod("POST");
            startCall.setDoOutput(true);
            startCall.setDoInput(true);
            startCall.setRequestProperty("Content-Type", "application/json");

            //Send request
            DataOutputStream wr = new DataOutputStream(startCall.getOutputStream());
            wr.writeBytes(requestBody.toString());
            wr.flush();
            wr.close();

            //get response
            BufferedReader br = new BufferedReader(new InputStreamReader(startCall.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            String jsonResponse = sb.toString();

            return new JSONObject(jsonResponse);

        } catch (IOException ignored) {
            ignored.printStackTrace();
        }

        return null;

    }

}
