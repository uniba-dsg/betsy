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
                        System.out.println(http.getContentType())
                        System.out.println(http.getContent())
                        System.out.println(http.getResponseMessage())
                        BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        String jsonResponse = sb.toString();
                        System.out.println(jsonResponse)
                        JSONArray jsonArray = new JSONArray(jsonResponse)
                        JSONObject obj = (JSONObject)jsonArray.get(0)
                        System.out.println(obj.get("id"))
                    }else{
                        System.out.println("error 1")
                        System.out.println(http.getResponseCode())
                        System.out.println(http.getContentType())
                        //System.out.println(http.getContent())
                        System.out.println(http.getResponseMessage())
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
