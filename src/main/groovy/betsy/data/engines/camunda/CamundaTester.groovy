package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.tasks.FileTasks
import org.codehaus.groovy.tools.RootLoader
import org.json.JSONArray
import org.json.JSONObject

import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 25.02.14
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
class CamundaTester {

    String restURL
    private static final AntBuilder ant = AntUtil.builder()

    /**
     * runs a single test
     */
    void runTest(){

        // needed process information
        String key = "SimpleApplication"
        HashMap<String, Boolean> variableResults = new HashMap<String, Boolean>()
        variableResults.put("successfull", true)
        String reportDir = "reports/test"

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
        requestBody.put("variables", variables)
        requestBody.put("businessKey", "key-${key}")

        //second request to start process using id and Json to get the process instance id
        response = post(restURL + "/process-definition/${id}/start", requestBody)
        //String processInstanceId = response.get("id")

        //prepare junit test
        FileTasks.deleteDirectory(Paths.get(reportDir))
        FileTasks.mkdirs(Paths.get(reportDir))
        FileTasks.deleteFile(Paths.get("downloads/unitTests/bin/unitTest.class"))

        //setup path to 'tools.jar' for the javac ant task
        String javaHome = System.getProperty("java.home")
        if(javaHome.endsWith("jre")){
            javaHome = javaHome.substring(0, javaHome.length() - 4)
        }
        RootLoader rl = (RootLoader) this.class.classLoader.getRootLoader()
        if(rl == null){
            Thread.currentThread().getContextClassLoader().addURL(new URL("file:///${javaHome}/lib/tools.jar"))
        }else{
            rl.addURL(new URL("file:///${javaHome}/lib/tools.jar"))
        }

        // compile test sources
        ant.javac(srcdir: "downloads/unitTests/src", destdir: "downloads/unitTests/bin", includeantruntime: false) {
            classpath{
                pathelement(location: "downloads/junit-4.10.jar")    //TODO use junit from library home
            }
        }
        ant.junit(printsummary: "on", fork: "true", haltonfailure: "yes"){
            classpath{
                pathelement(location: "downloads/junit-4.10.jar")    //TODO use junit from library home
                pathelement(location: "downloads/unitTests/bin")
            }
            formatter(type: "xml")
            batchtest(todir: reportDir){
                fileset(dir: "downloads/unitTests/src"){
                    include(name: "**/*Test*.java")
                }
            }
        }

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
